/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2016, Laboratório de Sistemas e Tecnologia Subaquática
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
package pt.lsts.imc.adapter;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Vector;

import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanDB;
import pt.lsts.imc.PlanDB.OP;
import pt.lsts.imc.PlanDB.TYPE;
import pt.lsts.imc.PlanDBInformation;
import pt.lsts.imc.PlanDBState;
import pt.lsts.imc.PlanSpecification;

/**
 * @author zp
 *
 */
public class PlanDbManager {

	protected LinkedHashMap<String, PlanSpecification> planDb = new LinkedHashMap<String, PlanSpecification>();
	protected LinkedHashMap<String, PlanDBInformation> infoDb = new LinkedHashMap<String, PlanDBInformation>();
	
	public PlanSpecification getSpec(String planId) {
		return planDb.get(planId);
	}
	
	private PlanDBInformation createInfo(PlanSpecification spec) {
		PlanDBInformation info = new PlanDBInformation();
		info.setPlanId(spec.getPlanId());
		info.setChangeTime(System.currentTimeMillis()/1000.0);
		info.setChangeSname(spec.getSourceName());
		info.setChangeSid(spec.getSrc());
		info.setPlanSize(spec.getPayloadSize());
		info.setMd5(spec.payloadMD5());
		return info;		
	}
	
	public PlanDB setPlanControl(PlanControl pc) {
		if (pc.getType() != PlanControl.TYPE.REQUEST)
			return null;
		if (pc.getOp() != PlanControl.OP.LOAD && pc.getOp() != PlanControl.OP.START)
			return null;
		if (pc.getArg() == null)
			return null;
		if (pc.getArg().getMgid() != PlanSpecification.ID_STATIC)
			return null;
		try {
			PlanSpecification spec = pc.getArg(PlanSpecification.class);
			planDb.put(spec.getPlanId(), spec);
			PlanDBInformation info = createInfo(spec);
			infoDb.put(spec.getPlanId(), info);
			
			PlanDB reply = new PlanDB();
			reply.setPlanId(spec.getPlanId());
			reply.setDst(pc.getSrc());
			reply.setDstEnt(pc.getSrcEnt());
			reply.setOp(OP.GET_INFO);
			reply.setType(TYPE.SUCCESS);
			reply.setArg(info);
			return reply;			
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PlanDB query(PlanDB query) {
		if (query.getType() != PlanDB.TYPE.REQUEST)
			return null;
		
		PlanDB reply = new PlanDB();
		try {
			reply.copyFrom(query);
		}
		catch (Exception e) { }
		
		reply.setDst(query.getSrc());
		reply.setDstEnt(query.getSrcEnt());
		reply.setType(TYPE.FAILURE);
		
		switch(query.getOp()) {
		case DEL:
			synchronized (planDb) {
				if (planDb.containsKey(query.getPlanId())) {
					planDb.remove(query.getPlanId());
					infoDb.remove(query.getPlanId());
					reply.setType(TYPE.SUCCESS);					
				}
			}			
			break;
		case SET:
			synchronized (planDb) {
				try {
					planDb.put(query.getPlanId(), query.getArg(PlanSpecification.class));
					PlanDBInformation info = createInfo(query.getArg(PlanSpecification.class));
					infoDb.put(query.getPlanId(), info);
					reply.setType(TYPE.SUCCESS);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}	
			break;
		case GET:
			synchronized (planDb) {
				if (planDb.containsKey(query.getPlanId())) {
					reply.setArg(planDb.get(query.getPlanId()));
					reply.setType(TYPE.SUCCESS);
				}				
			}
			break;
		case CLEAR:
			synchronized (planDb) {
				planDb.clear();				
				infoDb.clear();
				reply.setType(TYPE.SUCCESS);
			}
			break;
		case GET_INFO:
			synchronized (planDb) {
				if (infoDb.containsKey(query.getPlanId())) {
					reply.setArg(infoDb.get(query.getPlanId()));
					reply.setType(TYPE.SUCCESS);
				}					
			}
			break;
		case GET_DSTATE:
		case GET_STATE:
			synchronized (planDb) {
				PlanDBState ret = new PlanDBState();
				ArrayList<String> planIds = new ArrayList<String>();
				Vector<PlanDBInformation> infos = new Vector<PlanDBInformation>();
				MessageDigest md;
				try {
					md = MessageDigest.getInstance("MD5");
				}
				catch (Exception e) {
					e.printStackTrace();
					break;
				}
				
				planIds.addAll(infoDb.keySet());
				Collections.sort(planIds);
				
				PlanDBInformation latest = null;
				int totalSize = 0;
				
				for (String p : planIds) {
					PlanDBInformation pdi = infoDb.get(p);
					totalSize += pdi.getSize();
					infos.add(pdi);
					if (latest == null || latest.getChangeTime() < pdi.getChangeTime())
						latest = pdi;
					md.update(pdi.getMd5());
				}
				ret.setPlansInfo(infos);
				ret.setPlanCount(planIds.size());
				ret.setPlanSize(totalSize);
				ret.setMd5(md.digest());
				
				if (latest != null) {
					ret.setChangeSid(latest.getChangeSid());
					ret.setChangeSname(latest.getChangeSname());
					ret.setChangeTime(latest.getChangeTime());
				}
				reply.setArg(ret);
				reply.setType(TYPE.SUCCESS);
			}
		default:
			break;
		}				
		return reply;
	}
	
}
