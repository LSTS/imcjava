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
 * $Id::                                                                       $:
 */
package pt.lsts.imc.lsf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class BigByteBuffer {

    protected MappedByteBuffer buffer;
    protected FileChannel channel;
    protected long startPos, endPos, fileLength;
    protected long bufferSize = (long)Math.pow(2, 20) * 64l;
    protected long bufferOverlap = (long)Math.pow(2, 16);
    protected ByteOrder order = ByteOrder.LITTLE_ENDIAN;
    
    public BigByteBuffer(FileChannel channel, long fileLength) {
        this.channel = channel;
        this.fileLength = fileLength;
        mapFrom(0);
    }

    protected boolean mapFrom(long startPos) {
        this.startPos = startPos;
        this.endPos = startPos + bufferSize + bufferOverlap;
        this.endPos = Math.min(endPos, fileLength);
        try {            
            buffer = channel.map(MapMode.READ_ONLY, startPos, endPos - startPos);
            buffer.order(order);
        }
        catch (Exception e) {
            e.printStackTrace();
            buffer = null;
            return false;
        }
        return true;
    }

    public boolean position(long position) {
        if (position >= fileLength)
            return false;
        if (position < startPos || position > endPos - bufferOverlap) {
            //new Exception("remapping "+position+" !in ["+startPos+", "+(endPos - bufferOverlap)+"]").printStackTrace();
            return mapFrom(position);
        }
        else {
            buffer.position((int)(position-startPos));
            return true;
        }        
    }
    
    public long position() {
        return startPos + buffer.position();
    }
    
    public final ByteBuffer getBuffer() {
        return buffer;
    }
    
    public void order(ByteOrder order) {
        this.order = order;
        //System.out.println("ORDER CHANGED TO "+order);
        mapFrom(startPos);
    }
}
