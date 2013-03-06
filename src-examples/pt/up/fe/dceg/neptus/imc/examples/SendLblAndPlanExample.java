package pt.up.fe.dceg.neptus.imc.examples;

import pt.up.fe.dceg.neptus.imc.Abort;
import pt.up.fe.dceg.neptus.imc.Goto;
import pt.up.fe.dceg.neptus.imc.IMCDefinition;
import pt.up.fe.dceg.neptus.imc.IMCMessage;
import pt.up.fe.dceg.neptus.imc.net.IMCProtocol;
import pt.up.fe.dceg.neptus.imc.types.PlanSpecificationAdapter;

public class SendLblAndPlanExample {

    
    public static void main(String[] args) throws Exception {
        IMCProtocol protocol = new IMCProtocol(6001);
        while (protocol.announceAgeMillis("lauv-seacon-4") > 10000) {
            Thread.sleep(1000);
            System.out.println("Waiting for an announce from LAUV-SEACON-4...");
        }
        PlanSpecificationAdapter adapter = new PlanSpecificationAdapter();
        
        
        Goto gt = new Goto();
        gt.setLat(Math.toRadians(41));
        gt.setLon(Math.toRadians(-8));
        gt.setZ(2);
        gt.setZUnits(Goto.Z_UNITS.DEPTH);
        gt.setSpeed(1000);
        gt.setSpeedUnits(Goto.SPEED_UNITS.RPM);
        adapter.addManeuver("goto1", gt);
        Goto gt2 = new Goto();
        gt2.setMessage(gt);
        gt2.setLat(Math.toRadians(41.0001));
        adapter.addManeuver("goto2", gt2);
        
        adapter.addTransition("goto1", "goto2", "ManeuverIsDone", null);
        IMCMessage msg = adapter.getData(IMCDefinition.getInstance());
        protocol.sendMessage("lauv-seacon-4", msg);
        System.out.println(msg.getSrc());
        System.out.println("sent: ");
        System.out.println(msg.asXml(false));
        
        protocol.sendMessage("lauv-seacon-4", new Abort());
        protocol.stop();
    }
    
    
}
