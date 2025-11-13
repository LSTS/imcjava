/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2025, Laboratório de Sistemas e Tecnologia Subaquática
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
package pt.lsts.imc.lsf;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;

public class UnserializedMessage implements Comparable<UnserializedMessage> {

	private byte[] data;
	private boolean bigEndian = false;
	private IMCDefinition defs;
	
	private static void readBuffer(byte[] buffer, InputStream in) throws IOException {
		int read = 0;
		while (read < buffer.length) {
			int bytes_in = in.read(buffer, read, buffer.length - read);
			if (bytes_in < 0)
				throw new EOFException();
			read += bytes_in;
		}
	}
	
	public static UnserializedMessage readMessage(IMCDefinition defs, InputStream in) throws IOException {
		byte[] header = new byte[20];
		readBuffer(header, in);
	
		ByteBuffer hBuffer = ByteBuffer.wrap(header);
		boolean bigEndian = (hBuffer.getShort() & 0xFFFF) == defs.getSyncWord();
		if (!bigEndian)
			hBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		int mgid = 0xFFFF & hBuffer.getShort();
		int size = 0xFFFF & hBuffer.getShort();
		
		if (defs.getMessageName(mgid) == null)
			throw new IOException("Unknown message ID: "+mgid);
		
		byte[] payload_footer = new byte[size + 2];
		readBuffer(payload_footer, in);
		
		return new UnserializedMessage(bigEndian, concatArray(header, payload_footer), defs);
	}

	public UnserializedMessage(boolean bigEndian, byte[] message, IMCDefinition def) {
		this.data = message;
		this.bigEndian = bigEndian;
		this.defs = def;
	}
	
	private static byte[] concatArray(byte[] array1, byte[] array2) {
        if (array1 == null)
            return array2 == null ? null : array2.clone();
        else if (array2 == null)
			return array1 == null ? null : array1.clone();

        byte[] concatedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, concatedArray, 0, array1.length);
        System.arraycopy(array2, 0, concatedArray, array1.length, array2.length);
        return concatedArray;
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
	
	public Date getDate() {
		return new Date((long)(1000.0 * getTimestamp()));
	}
	
	public IMCMessage deserialize() throws IOException {
		return defs.nextMessage(new ByteArrayInputStream(data));
	}
	
	public byte[] getData() {
		return data;
	}

	public String getMessageName() {
		return defs.getMessageName(getMgId());
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
	
	@Override
	public int compareTo(UnserializedMessage o) {
		return Double.valueOf(getTimestamp()).compareTo(o.getTimestamp());
	}
	
}
