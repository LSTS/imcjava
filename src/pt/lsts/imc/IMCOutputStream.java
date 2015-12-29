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
 *  
 * $Id:: IMCOutputStream.java 333 2013-01-02 11:11:44Z zepinto                 $:
 */
package pt.lsts.imc;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

public class IMCOutputStream implements DataOutput {

	private final static int[] crc16_table = new int[] {
		0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
  	  	0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
  	  	0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
  	  	0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
  	  	0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
  	  	0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
  	  	0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
  	  	0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
  	  	0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
  	  	0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
  	  	0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
  	  	0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
  	  	0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
  	  	0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
  	  	0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
  	  	0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
  	  	0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
  	  	0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
  	  	0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
  	  	0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
  	  	0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
  	  	0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
  	  	0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
  	  	0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
  	  	0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
  	  	0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
  	  	0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
  	  	0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
  	  	0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
  	  	0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
  	  	0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
  	  	0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040
	};
		
	protected int crc = 0;
	protected long count = 0;
	protected DataOutputStream output;
	protected boolean bigEndian = false;
	protected IMCDefinition def;// = IMCDefinition.getInstance();
	
	public void resetCRC() {
		crc = 0;
	}
	
	public int getCRC() {
		return crc;
	}
	
	public void resetCount() {
		count = 0;
	}
	
	public long getCount() {
		return count;
	}
	
	public boolean isBigEndian() {
		return bigEndian;
	}

	public void setBigEndian(boolean bigEndian) {
		this.bigEndian = bigEndian;
	}

	public IMCOutputStream(IMCDefinition def, OutputStream out) {
		output = new DataOutputStream(out);
		this.def = def;
	}
	
	public IMCOutputStream(OutputStream out) {
		output = new DataOutputStream(out);
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}
	
	@Override
	public void write(int b) throws IOException {
		crc = (crc >> 8) ^ crc16_table[(crc ^ b) & 0xff];		
		output.write(b);
		count++;
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = off; i < len; i++)
			crc = (crc >> 8) ^ crc16_table[(crc ^ b[i]) & 0xff];
		output.write(b, off, len);
		count += len;
	}
	
	@Override
	public void writeBoolean(boolean v) throws IOException {
		output.writeBoolean(v);
	}
	
	@Override
	public void writeByte(int v) throws IOException {
		//output.writeByte(v);
		write(v);
	}
	
	@Override
	public void writeBytes(String s) throws IOException {
		int len = s.length();
		for (int i = 0 ; i < len ; i++) {
		    write((byte)s.charAt(i));
		}		
	}
	
	@Override
	public void writeDouble(double v) throws IOException {
		long l = Double.doubleToLongBits(v);		
		writeLong(l);		
	}
	
	@Override
	public void writeFloat(float v) throws IOException {
		int i = Float.floatToIntBits(v);
		writeInt(i);
	}
	
	@Override
	public void writeUTF(String s) throws IOException {
		throw new IOException("Not implemented");
	}
	
	@Override
	public void writeChar(int v) throws IOException {
		write((v >>> 8) & 0xFF);
        write((v >>> 0) & 0xFF);        
	}
	
	@Override
	public void writeChars(String s) throws IOException {
		int len = s.length();
		for (int i = 0 ; i < len ; i++) {
			int v = s.charAt(i);
			write((v >>> 8) & 0xFF); 
			write((v >>> 0) & 0xFF); 
		}
	}
	
	public void writeUnsignedInt(long v) throws IOException {
		writeInt((int)v);
	}
	
	@Override
	public void writeInt(int v) throws IOException {
		if (!bigEndian)
			v = Integer.reverseBytes(v);
		 write((v >>> 24) & 0xFF);
	     write((v >>> 16) & 0xFF);
	     write((v >>>  8) & 0xFF);
	     write((v >>>  0) & 0xFF);
	}
	
	private byte writeBuffer[] = new byte[8];

	public void writeUnsignedLong(BigInteger val) throws IOException {
		writeLong(val.longValue());
	}
	
	@Override
	public void writeLong(long v) throws IOException {
		if (!bigEndian)
			v = Long.reverseBytes(v);
		
		writeBuffer[0] = (byte)(v >>> 56);
		writeBuffer[1] = (byte)(v >>> 48);
		writeBuffer[2] = (byte)(v >>> 40);
		writeBuffer[3] = (byte)(v >>> 32);
		writeBuffer[4] = (byte)(v >>> 24);
		writeBuffer[5] = (byte)(v >>> 16);
		writeBuffer[6] = (byte)(v >>>  8);
		writeBuffer[7] = (byte)(v >>>  0);
		write(writeBuffer, 0, 8);
	}
	
	@Override
	public void writeShort(int v) throws IOException {
		if (!bigEndian)
			v = Short.reverseBytes((short)v);
		write((v >>> 8) & 0xFF);
        write((v >>> 0) & 0xFF);
	}
	
	public void writeUnsignedShort(int v) throws IOException {
		writeShort(v);
	}
	
	public void writeRawdata(byte[] data) throws IOException {
		writeShort(data.length);
		write(data);
	}
	
	public void writePlaintext(String t) throws IOException {
		writeRawdata(t.getBytes("UTF-8"));		
	}
	
	public void writeInlineMessage(IMCMessage message) throws IOException {
		writeShort((short)message.getInteger("mgid"));
		if (def != null)
		    def.serializeFields(message, this);
		else
		    IMCDefinition.getInstance().serializeFields(message, this);
	}
	
	public void writeMessage(IMCMessage message) throws IOException {
		message.serialize(this);
	}
	
	public void close() throws IOException {
		output.close();
	}
}
