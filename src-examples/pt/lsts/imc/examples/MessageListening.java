package pt.lsts.imc.examples;

import pt.lsts.imc.Announce;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.net.IMCProtocol;
import pt.lsts.neptus.messages.listener.Consume;

public class MessageListening {

    @Consume
    public void onState(EstimatedState state) {
        System.out.println("Received Estimated State from "+state.getSourceName()+":");
        System.out.println(state.getX()+", "+state.getY()+", "+state.getZ());
    }
    
    @Consume
    public void onAnnounce(Announce ann) {
        System.out.println("Received Announce from "+ann.getSourceName());
    }
    
    
    public static void main(String[] args) {
        IMCProtocol protocol = new IMCProtocol(6006);
        protocol.register(new MessageListening());
        try {
        	Thread.sleep(100000);
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
    }
}
