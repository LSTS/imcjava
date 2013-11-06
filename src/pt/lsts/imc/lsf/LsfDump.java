/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2013, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id::                                                                       $:
 */
package pt.lsts.imc.lsf;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.RandomAccessFile;

import pt.lsts.imc.IMCDefinition;

public class LsfDump {

    public void dump(File lsfFile, long maxSizeBytes) throws Exception {
        RandomAccessFile raf = new RandomAccessFile(lsfFile, "r");
        IMCDefinition defs = IMCDefinition.getInstance();
        byte[] header = new byte[defs.headerLength()];
        while (raf.getFilePointer() < raf.length()) {
            raf.read(header);
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(header));
            int sync = dis.readShort() & 0xFFFF;
            int mgid = dis.readShort() & 0xFFFF;
            int size = dis.readShort() & 0xFFFF;
            boolean swapped = false;
            if (sync == defs.getSwappedWord()) {
                swapped = true;
            }

            if (swapped) {
                mgid = Short.reverseBytes((short)mgid);
                size = Short.reverseBytes((short)size);
                System.out.println("Message type: "+mgid+" ("+defs.getMessageName(mgid)+") - "+(size&0xFFFF)+" ftell():"+raf.getFilePointer());
            }
            raf.skipBytes(size+2);
        }
        raf.close();
    }
    
    public static void main(String[] args) throws Exception {
        new LsfDump().dump(new File("/home/zp/Desktop/Data.lsf"), 1024 * 1024 * 512);
    }

}
