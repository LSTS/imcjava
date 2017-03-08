/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2017, Laboratório de Sistemas e Tecnologia Subaquática
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the names of IMC, LSTS, IMCJava nor the names of its 
 *       contributors may be used to endorse or promote products derived from 
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL LABORATORIO DE SISTEMAS E TECNOLOGIA SUBAQUATICA
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package pt.lsts.autonomy;

import java.io.IOException;

import pt.lsts.imc.Abort;
import pt.lsts.imc.AcousticOperation;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControl.OP;
import pt.lsts.imc.PlanControl.TYPE;
import pt.lsts.imc.PlanControlState;
import pt.lsts.imc.PlanControlState.LAST_OUTCOME;
import pt.lsts.imc.PlanControlState.STATE;
import pt.lsts.imc.TextMessage;
import pt.lsts.imc.VehicleState;
import pt.lsts.imc.VehicleState.OP_MODE;
import pt.lsts.imc.net.Consume;
import pt.lsts.imc.net.PojoConfig;
import pt.lsts.imc.state.Parameter;
import pt.lsts.neptus.messages.listener.Periodic;

/**
 * @author zp
 *
 */
public class SequentialPlanExecution {

	int src = 0;
	VehicleState vehicleState = new VehicleState();
	PlanControlState planControlState = new PlanControlState();
	TcpClient connection = null;
	int index = 0;
	
	@Parameter(description="TCP hostname where to connect")
	public String host = "127.0.0.1";
	
	@Parameter(description="TCP port where to connect")
	public int port = 6002;
	
	@Parameter(description="Sequence of plan IDs to be executed")
	public String[] plans = new String[] {"plan1", "plan2"};
	
	@Parameter(description="Whether to send information messages via acoustic modem")
	public boolean acousticUpdates = false;
	
	enum StateEnum {
		GettingReady, 		// waiting for the vehicle to be in service mode
		StartingPlan,		// waiting for the vehicle to be in maneuver mode
		Executing,			// waiting for the plan execution to end
		Finished,			// all plans have been executed successfully
		Aborted;			// received an Abort
	}
	
	private StateEnum state = StateEnum.GettingReady;	
	
	{
		System.out.println("STATE: "+state);
	}

	/**
	 * Update stored message
	 */
	@Consume
	void on(VehicleState msg) {
		if (src == 0)
			src = msg.getSrc();
		
		if (msg.getSrc() == src)
			vehicleState = msg;			
	}
	
	/**
	 * Update stored message
	 */
	@Consume
	void on(PlanControlState msg) {
		if (msg.getSrc() == src)
			planControlState = msg;
	}
	
	/**
	 * When an abort is received, stops everything
	 */
	@Consume
	void on(Abort msg) {
		state = StateEnum.Aborted;				
	}
	
	/**
	 * State machine step funtion
	 */
	@Periodic(1000)
	public void step() {
		
		// connect if not already connected
		if (connection == null) {
			connect();
			return;
		}

		// check if connection was lost
		if (vehicleState.getAgeInSeconds() > 2 || planControlState.getAgeInSeconds() > 2) {
			System.err.println("Reconnecting...");
			connection.connect(host, port);
			state = StateEnum.GettingReady;
			System.out.println("STATE: "+state);
			return;
		}

		// check if already done
		if (index == plans.length) {
			state = StateEnum.Finished;
			try {
				reportAcoustically("Finished all plans.");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Nothing else to execute. Exiting.");
			System.exit(0);
		}

		// continue state machine execution
		switch (state) {
		case GettingReady:
			if (vehicleState.getOpMode() == OP_MODE.SERVICE) {
				System.out.println("Vehicle is ready for execution.");
				try {
					startExecution();
					state = StateEnum.StartingPlan;
					System.out.println("STATE: "+state);
				}
				catch (Exception e) {
					e.printStackTrace();
					System.err.println(e.getClass().getSimpleName()+": "+e.getMessage());
				}
			}
			break;
		case StartingPlan:
			if (plans[index].isEmpty()) {
				index++;
				break;
			}
			if (planControlState.getState() == STATE.EXECUTING) {
				System.out.println("Vehicle started executing "+plans[index]);
				state = StateEnum.Executing;
				System.out.println("STATE: "+state);
				try {
					reportAcoustically("Executing "+plans[index]);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (planControlState.getState() != STATE.INITIALIZING) {
				System.out.println("Could not start "+plans[index]);
				state = StateEnum.GettingReady;
				System.out.println("STATE: "+state);
			}
			break;
		case Executing:
			if (planControlState.getState() != STATE.EXECUTING) {
				if (planControlState.getLastOutcome() == LAST_OUTCOME.SUCCESS) {
					System.out.println("Vehicle successfully executed "+plans[index]);
					try {
						reportAcoustically("Finished "+plans[index]);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					index++;
				}
				else {
					System.err.println("Error couldn't execute "+plans[index]);
				}
				state = StateEnum.GettingReady;
				System.out.println("STATE: "+state);
			}
		default:
			break;
		}
	}
	
	int reqId = 0;
	void startExecution() throws Exception {
		PlanControl pc = new PlanControl();
		pc.setRequestId(++reqId);
		pc.setType(TYPE.REQUEST);
		pc.setOp(OP.START);
		pc.setPlanId(plans[index]);
		connection.send(pc);		
	}
	
	private void connect() {
		System.out.println("Connecting to "+host+":"+port);
		try {
			connection = new TcpClient();
			connection.register(this);
			connection.connect(host, port);
			connection.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void reportAcoustically(String text) throws IOException {
		if (acousticUpdates) {
			TextMessage msg = new TextMessage();
			msg.setText(text);
			AcousticOperation op = new AcousticOperation();
			op.setOp(AcousticOperation.OP.MSG);
			op.setSystem("broadcast");
			op.setMsg(msg);
			connection.send(op);
		}
	}
	
	public static void main(String[] args) throws Exception {
		SequentialPlanExecution exec = PojoConfig.create(SequentialPlanExecution.class, args);
		exec.connect();
	}
}
