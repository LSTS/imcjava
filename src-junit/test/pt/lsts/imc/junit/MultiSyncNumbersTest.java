package pt.lsts.imc.junit;

import org.junit.Test;
import pt.lsts.imc.Abort;
import pt.lsts.imc.Header;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCInputStream;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MultiSyncNumbersTest {
    @Test
    public void test() throws Exception {
        IMCDefinition defs = IMCDefinition.getInstance();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IMCOutputStream output = new IMCOutputStream(defs, baos);

        Abort abortMsg = new Abort(defs);
        abortMsg.setSrc(0x1020);

        long count = 0;

        output.writeMessage(abortMsg);
        count = printHex(baos.toByteArray(), count);
        long size = count;
        byte[] barray1 = Arrays.copyOfRange(baos.toByteArray(), (int) count - 22, (int) count);
        ByteBuffer buffer1 = ByteBuffer.wrap(barray1);


        output.writeMessage(abortMsg, 0xEAEB);
        count = printHex(baos.toByteArray(), count);
        byte[] barray2 = Arrays.copyOfRange(baos.toByteArray(), (int) count - 22, (int) count);
        ByteBuffer buffer2 = ByteBuffer.wrap(barray2);

        abortMsg.serialize(output);
        count = printHex(baos.toByteArray(), count);
        byte[] barray3 = Arrays.copyOfRange(baos.toByteArray(), (int) count - 22, (int) count);
        ByteBuffer buffer3 = ByteBuffer.wrap(barray3);

        abortMsg.serialize(output, 0x0102);
        count = printHex(baos.toByteArray(), count);
        byte[] barray4 = Arrays.copyOfRange(baos.toByteArray(), (int) count - 22, (int) count);
        ByteBuffer buffer4 = ByteBuffer.wrap(barray4);

        System.out.println("---------------------------------------");

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        IMCInputStream input = new IMCInputStream(bais, defs);

        IMCMessage msg = input.readMessage();
        defs.addAlternativeSyncNumber(0xEAEB);
        System.out.println(String.format("0x%04X ", msg.getHeader().get_sync()) + " >> " + msg.toString());
        msg = input.readMessage();
        System.out.println(String.format("0x%04X ", msg.getHeader().get_sync()) + " >> " + msg.toString());
        msg = input.readMessage();
        System.out.println(String.format("0x%04X ", msg.getHeader().get_sync()) + " >> " + msg.toString());
        try {
            msg = input.readMessage();
            System.out.println(String.format("0x%04X ", msg.getHeader().get_sync()) + " >> " + msg.toString());
        } catch (Exception e) {
            System.out.println("Expected exception: " + e.getMessage());
        }
    }

    private long printHex(byte[] data, long offset) {
        long count = offset;
        long count2 = 0;
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            if (++count2 <= offset)
                continue;
            count++;
            sb.append(String.format("%02X ", b));
        }
        System.out.println(sb.toString());
        return count;
    }
}

