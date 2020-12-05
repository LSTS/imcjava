/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2020, Laboratório de Sistemas e Tecnologia Subaquática
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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class BigByteBuffer {

    protected MappedByteBuffer buffer;
    protected FileChannel channel;
    protected long startPos, endPos, fileLength;
    protected long bufferSize = (long) Math.pow(2, 20) * 64L;
    protected long bufferOverlap = (long) Math.pow(2, 16);
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
        return position(position, 1);
    }

    /**
     * Check if position is o to read position and size.
     * @param position file position
     * @param size Should be greater than 1. Otherwise will be truncated to 1
     * @return
     */
    public boolean position(long position, int size) {
        if (size < 1)
            size = 1;
        if (position >= fileLength || position + size >= fileLength)
            return false;
        if (position < startPos || position > endPos - bufferOverlap
                || position + size > endPos - bufferOverlap) {
            //new Exception("remapping "+position+" !in ["+startPos+", "+(endPos - bufferOverlap)+"]").printStackTrace();
            return mapFrom(position);
        }
        else {
            buffer.position((int) (position - startPos));
            return true;
        }
    }

    public long position() {
        return startPos + buffer.position();
    }
    
    // public final ByteBuffer getBuffer() {
    //    return buffer;
    //}
    
    public void order(ByteOrder order) {
        this.order = order;
        //System.out.println("ORDER CHANGED TO "+order);
        mapFrom(startPos);
    }

    public byte get() {
        return buffer.get();
    }

    public void put(byte b) {
        buffer.put(b);
    }

    public byte get(long index) {
        if (position(index))
            buffer.get();
        throw new BufferUnderflowException();
    }

    public void put(long index, byte b) {
        if (position(index))
            buffer.put(b);
        throw new BufferUnderflowException();
    }

    public final ByteOrder order() {
        return buffer.order();
    }

    public char getChar() {
        if (position(position(), 2))
            buffer.getChar();
        throw new BufferUnderflowException();
    }

    public void putChar(char value) {
        if (position(position(), 2))
            buffer.putChar(value);
        throw new BufferUnderflowException();
    }

    public char getChar(long index) {
        if (position(index, 2))
            buffer.getChar();
        throw new BufferUnderflowException();
    }

    public void putChar(long index, char value) {
        if (position(index, 2))
            buffer.putChar(value);
        throw new BufferUnderflowException();
    }

    public short getShort() {
        if (position(position(), 2))
            buffer.getShort();
        throw new BufferUnderflowException();
    }

    public void putShort(short value) {
        if (position(position(), 2))
            buffer.putShort(value);
        throw new BufferUnderflowException();
    }

    public short getShort(long index) {
        if (position(index, 2))
            buffer.getShort();
        throw new BufferUnderflowException();
    }

    public void putShort(long index, short value) {
        if (position(index, 2))
            buffer.putShort(value);
        throw new BufferUnderflowException();
    }

    public int getInt() {
        if (position(position(), 4))
            buffer.getInt();
        throw new BufferUnderflowException();
    }

    public void putInt(int value) {
        if (position(position(), 4))
            buffer.putInt(value);
        throw new BufferUnderflowException();
    }

    public int getInt(long index) {
        if (position(index, 4))
            buffer.getInt();
        throw new BufferUnderflowException();
    }

    public void putInt(long index, int value) {
        if (position(index, 4))
            buffer.putInt(value);
        throw new BufferUnderflowException();
    }

    public long getLong() {
        if (position(position(), 8))
            buffer.getLong();
        throw new BufferUnderflowException();
    }

    public void putLong(long value) {
        if (position(position(), 8))
            buffer.putLong(value);
        throw new BufferUnderflowException();
    }

    public long getLong(long index) {
        if (position(index, 8))
            buffer.getLong();
        throw new BufferUnderflowException();
    }

    public void putLong(long index, long value) {
        if (position(index, 8))
            buffer.putLong(value);
        throw new BufferUnderflowException();
    }

    public float getFloat() {
        if (position(position(), 4))
            buffer.getFloat();
        throw new BufferUnderflowException();
    }

    public void putFloat(float value) {
        if (position(position(), 4))
            buffer.putFloat(value);
        throw new BufferUnderflowException();
    }

    public float getFloat(long index) {
        if (position(index, 4))
            buffer.getFloat();
        throw new BufferUnderflowException();
    }

    public void putFloat(long index, float value) {
        if (position(index, 4))
            buffer.putFloat(value);
        throw new BufferUnderflowException();
    }

    public double getDouble() {
        if (position(position(), 8))
            buffer.getDouble();
        throw new BufferUnderflowException();
    }

    public void putDouble(double value) {
        if (position(position(), 8))
            buffer.putDouble(value);
        throw new BufferUnderflowException();
    }

    public double getDouble(long index) {
        if (position(index, 8))
            buffer.getDouble();
        throw new BufferUnderflowException();
    }

    public void putDouble(long index, double value) {
        if (position(index, 8))
            buffer.putDouble(value);
        throw new BufferUnderflowException();
    }

    static void checkBounds(int off, int len, int size) { // package-private
        if ((off | len | (off + len) | (size - (off + len))) < 0)
            throw new IndexOutOfBoundsException();
    }

    public final long remaining() {
        return  fileLength - (startPos + buffer.position());
    }

    /**
     * Tells whether there are any elements between the current position and
     * the limit.
     *
     * @return  <tt>true</tt> if, and only if, there is at least one element
     *          remaining in this buffer
     */
    public final boolean hasRemaining() {
        return (startPos + buffer.position()) < fileLength;
    }

    public void get(byte[] arr) {
        get(arr, 0, arr.length);
    }

    public void get(byte[] dst, int offset, int length) {
        checkBounds(offset, length, dst.length);
        position(position(), length);
        if (length > remaining())
            throw new BufferUnderflowException();
        int end = offset + length;
        for (int i = offset; i < end; i++)
            dst[i] = get();
    }
}
