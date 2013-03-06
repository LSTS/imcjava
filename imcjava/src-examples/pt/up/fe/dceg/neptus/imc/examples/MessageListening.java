package pt.up.fe.dceg.neptus.imc.examples;

import pt.up.fe.dceg.neptus.imc.Announce;
import pt.up.fe.dceg.neptus.imc.EstimatedState;
import pt.up.fe.dceg.neptus.imc.net.IMCProtocol;

import com.google.common.eventbus.Subscribe;

public class MessageListening {

    @Subscribe
    public void onState(EstimatedState state) {
        System.out.println("Received Estimated State from "+state.getSourceName()+":");
        System.out.println(state.getX()+", "+state.getY()+", "+state.getZ());
    }
    
    @Subscribe
    public void onAnnounce(Announce ann) {
        System.out.println("Received Announce from "+ann.getSourceName());
    }
    
    
    public static void main(String[] args) {
        IMCProtocol protocol = new IMCProtocol(6006);
        protocol.register(new MessageListening());
    }
}
