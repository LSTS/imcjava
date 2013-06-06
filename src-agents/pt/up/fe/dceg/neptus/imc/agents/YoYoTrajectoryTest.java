package pt.up.fe.dceg.neptus.imc.agents;

import java.util.Arrays;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;

import pt.up.fe.dceg.neptus.imc.AgentContext;
import pt.up.fe.dceg.neptus.imc.DesiredSpeed;
import pt.up.fe.dceg.neptus.imc.DesiredSpeed.SPEED_UNITS;
import pt.up.fe.dceg.neptus.imc.DesiredZ;
import pt.up.fe.dceg.neptus.imc.FollowRefState;
import pt.up.fe.dceg.neptus.imc.FollowReference;
import pt.up.fe.dceg.neptus.imc.IMCOutputStream;
import pt.up.fe.dceg.neptus.imc.ImcAgent;
import pt.up.fe.dceg.neptus.imc.PlanControl;
import pt.up.fe.dceg.neptus.imc.PlanControl.OP;
import pt.up.fe.dceg.neptus.imc.PlanControl.TYPE;
import pt.up.fe.dceg.neptus.imc.PlanManeuver;
import pt.up.fe.dceg.neptus.imc.PlanSpecification;
import pt.up.fe.dceg.neptus.imc.Reference;
import pt.up.fe.dceg.neptus.imc.TrexAttribute;
import pt.up.fe.dceg.neptus.imc.TrexOperation;
import pt.up.fe.dceg.neptus.imc.TrexToken;
import pt.up.fe.dceg.neptus.imc.annotations.Periodic;

import com.google.common.eventbus.Subscribe;

public class YoYoTrajectoryTest implements ImcAgent {

	protected AgentContext ctx;
	protected boolean active = false;

	protected double[] lats = new double[] {41.185065,41.185776,41.184992,41.183991}; 
	protected double[] lons = new double[] {-8.705527,-8.706348,-8.707587,-8.706472};
//	protected float[] depths = new float[] {0,0,0,0};
	protected float[] yoyoZ = new float[] {2, 10};
	protected int curDest = 0;
	protected int curZ = 0;

	@Override
	public void onStart(AgentContext conn) {
		this.ctx = conn;
		PlanControl startPlan = new PlanControl();
		startPlan.setType(TYPE.REQUEST);
		startPlan.setOp(OP.START);
		startPlan.setPlanId("follow_ref_test");
		FollowReference man = new FollowReference();
		man.setControlEnt((short)255);
		man.setControlSrc(65535);
		man.setAltitudeInterval(2);
		man.setTimeout(10);

		PlanSpecification spec = new PlanSpecification();
		spec.setPlanId("followref_test");
		spec.setStartManId("1");
		PlanManeuver pm = new PlanManeuver();
		pm.setData(man);
		pm.setManeuverId("1");
		spec.setManeuvers(Arrays.asList(pm));
		startPlan.setArg(spec);
		int reqId = 0;
		startPlan.setRequestId(reqId);
		startPlan.setFlags(0);
		ctx.send(startPlan);
	}

	@Subscribe
	public void on(FollowRefState state) {
		active = true;

		state.dump(System.err);
		
		if ((state.getProximity() & FollowRefState.PROX_XY_NEAR) != 0) {
			System.out.println("Arrived! Going for next point");
			curDest = (curDest + 1) % 4;
		}
		
		else if ((state.getProximity() & FollowRefState.PROX_Z_NEAR) != 0) {
			System.out.println("Changing Z");
			curZ = (curZ + 1) % 2;
		}
	}

	@Periodic(millisBetweenUpdates=1000)
	public void sendReference() {
		
		Reference ref = new Reference(
				(short)(Reference.FLAG_LOCATION | Reference.FLAG_SPEED | Reference.FLAG_Z),
				new DesiredSpeed(1, SPEED_UNITS.METERS_PS),
				new DesiredZ(yoyoZ[curZ], DesiredZ.Z_UNITS.DEPTH),
				Math.toRadians(lats[curDest]),
				Math.toRadians(lons[curDest]),0f
				);
		ctx.send(ref);
		ref.dump(System.err);
	}

	@Override
	public void onStop() {
		if (active) {
			PlanControl stop = new PlanControl();
			stop.setType(TYPE.REQUEST);
			stop.setOp(OP.STOP);
			ctx.send(stop);
		}
	}
	
	public static void main(String[] args) throws Exception {
		TrexOperation op = new TrexOperation();
		op.setOp(TrexOperation.OP.POST_GOAL);
		op.setGoalId("");
		TrexToken tok = new TrexToken();
		Vector<TrexAttribute> attrs = new Vector<TrexAttribute>();
		tok.setPredicate("At");
		tok.setTimeline("Navigator");
		attrs.add(new TrexAttribute("latitude", TrexAttribute.ATTR_TYPE.FLOAT, "41.834334234", "41.82343434"));
		attrs.add(new TrexAttribute("longitude", TrexAttribute.ATTR_TYPE.FLOAT, "-8.23234423", "-8.23423423"));
		attrs.add(new TrexAttribute("speed", TrexAttribute.ATTR_TYPE.FLOAT, "1.2", "1.2"));
		attrs.add(new TrexAttribute("z", TrexAttribute.ATTR_TYPE.FLOAT, "3", "25"));
		
		tok.setAttributes(attrs);
		op.setToken(tok);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayOutputStream zipped = new ByteArrayOutputStream();
		op.serialize(new IMCOutputStream(baos));
		System.out.println(baos.toByteArray().length);
		
		GZIPOutputStream gos = new GZIPOutputStream(zipped);
		gos.write(baos.toByteArray());
		gos.close();
		System.out.println(zipped.toByteArray().length);
		tok.dump(System.out);
	}
}
