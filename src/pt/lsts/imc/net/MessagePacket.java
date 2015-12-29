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
 * $Id:: MessagePacket.java 333 2013-01-02 11:11:44Z zepinto                   $:
 */
package pt.lsts.imc.net;

import java.net.InetSocketAddress;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCUtil;

public class MessagePacket {

    protected int mgid;
    protected int length;
    protected byte[] data;
    protected static int idOffset = 2;//IMCDefinition.getInstance().createHeader().getType().getOffsetOf("mgid");
    protected InetSocketAddress address;
    protected long time;
    

	public MessagePacket(IMCDefinition def, byte[] data, int length, InetSocketAddress addr, long time) {
        if (def == null)
            def = IMCDefinition.getInstance();
        if (idOffset == -1)
            idOffset = def.createHeader().getMessageType().getOffsetOf("mgid");
        
        this.data = data;
        this.length = length;
        int  sync= ((data[0]&0xFF)<<8) | (data[1]&0xFF);
        if (sync == def.getSyncWord())
            mgid = ((data[idOffset]&0xFF)<<8) | (data[idOffset+1]&0xFF);        
        else if (sync == def.getSwappedWord())
            mgid = ((data[idOffset+1]&0xFF)<<8) | (data[idOffset]&0xFF);
        this.time = time;
        address = addr;
    }

    public boolean validCRC() {
        int crc = IMCUtil.computeCrc16(data, 0, length-2, 0);
        int b1 = data[length-2] & 0xFF;
        int b2 = data[length-1] & 0xFF;
        return crc == (b1 << 8 | b2) || crc == (b2 << 8 | b1);
    }

    public InetSocketAddress getAddress() {
		return address;
	}


	public long getTimestampMillis() {
		return time;
	}

    public static void main(String[] args) {

        int s = 0xFE43;

        System.out.println(IMCDefinition.getInstance().getSyncWord());
        System.out.println(s);
    }

}
