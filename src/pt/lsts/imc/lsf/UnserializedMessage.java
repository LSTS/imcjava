package pt.lsts.imc.lsf;

import java.io.DataInput;
import java.io.IOException;

import pt.lsts.imc.IMCDefinition;

public class UnserializedMessage {

	private byte[] data;
	private boolean bigEndian = false;
	
	public static UnserializedMessage readMessage(IMCDefinition defs, DataInput in) throws IOException {
		int sync = in.readUnsignedShort() & 0xFFFF;
		int origSize, size = defs.getHeaderType().getComputedLength();
		boolean be = false;
		if (sync == defs.getSyncWord())
			be = true;
		else if (sync == defs.getSwappedWord())
			be = false;
		else {
			System.out.printf("Unrecognized sync word: %X.", sync);
			throw new IOException("Unrecognized syncword ("+sync+")");
		}
		int mgid = in.readUnsignedShort();
		origSize = in.readUnsignedShort();
		//System.out.printf("sync: %X, mgid: %X, size: %x\n", sync, mgid, origSize);
		
		if (!be) 
			size += 0xFFFF & Short.reverseBytes((short)origSize); //payload
		else
			size += origSize; //payload

		size += 2; // footer
		byte[] data = new byte[size];
		in.readFully(data, 0, size - 6);
		
		System.arraycopy(data, 0, data, 6, data.length-6);
		data[0] = (byte)((sync & 0xFF00) >> 8);
		data[1] = (byte)((sync & 0x00FF));
		data[2] = (byte)((mgid & 0xFF00) >> 8);
		data[3] = (byte)((mgid & 0x00FF));
		data[4] = (byte)((origSize & 0xFF00) >> 8);
		data[5] = (byte)((origSize & 0x00FF));

		return new UnserializedMessage(be, data);
	}

	public UnserializedMessage(boolean bigEndian, byte[] message) {
		this.data = message;
		this.bigEndian = bigEndian;
	}
	
	private int getShort(int msb, int lsb) {
		return ((data[msb]&0xFF) << 8) + (data[lsb]&0xFF); 
	}

	public int getPayloadSize() {
		if (bigEndian)
			return getShort(4, 5);
		else 
			return getShort(5, 4);
	}
	
	public double getTimestamp() {
		int index = 0;
		int pos = 6;
		long result = 0;
		for (index = 7; index >= 0; index--, pos++) {
			result += ((long)(data[pos] & 0xFF) << (index * 8));
		}
		if (!bigEndian)
			result = Long.reverseBytes(result);
		
		return Double.longBitsToDouble(result);
	}
	
	public byte[] getData() {
		return data;
	}

	public int getMgId() {
		if (bigEndian)
			return getShort(2, 3);
		else 
			return getShort(3, 2);
	}
	
	public int getSrc() {
		if (bigEndian)
			return getShort(14, 15);
		else
			return getShort(15, 14);			
	}
	
	public int getSrcEnt() {
		return data[16] & 0xFF;
	}
	
	public int getDst() {
		if (bigEndian)
			return getShort(17, 18);
		else
			return getShort(18, 17);
	}
	
	public int getDstEnt() {
		return data[19] & 0xFF;
	}
	
	
	
	public long getHash() {
		return ((data[2] & 0xFF) << 32)| ((data[3] & 0xFF) << 24) | ((data[14] & 0xFF) << 16) | ((data[15] & 0xFF) << 8) | data[16] & 0xFF; 
	}
	
	
}
