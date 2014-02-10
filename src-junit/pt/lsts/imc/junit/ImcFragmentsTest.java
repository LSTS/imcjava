package pt.lsts.imc.junit;

import java.io.ByteArrayInputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCInputStream;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCUtil;
import pt.lsts.imc.MessagePart;
import pt.lsts.imc.net.IMCFragmentHandler;

public class ImcFragmentsTest {
	 	@Test
	    public void test() throws Exception {
	 		IMCDefinition defs = IMCDefinition.getInstance();
	 		IMCFragmentHandler handler = new IMCFragmentHandler();
	 		
	        for (String abbrev: defs.getMessageNames()) {
	            IMCMessage m = defs.create(abbrev);
	            IMCUtil.fillWithRandomData(m);
	            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
	            defs.serialize(m, baos1);
	            byte[] data1 = baos1.toByteArray();            
	            IMCMessage unser = defs.nextMessage(new IMCInputStream(new ByteArrayInputStream(data1)));
	            MessagePart[] parts = handler.fragment(unser, 100);
	            IMCMessage res = null;
	            for (MessagePart p : parts) {
	    			res = handler.setFragment(p);
	    		}
	            
	            Assert.assertNotNull(res);
	            Assert.assertEquals(res.toString(), unser.toString());
	            
	        }	
	    }
}
