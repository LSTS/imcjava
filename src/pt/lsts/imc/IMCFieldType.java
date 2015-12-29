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
 * $Id:: IMCFieldType.java 334 2013-01-02 11:20:47Z zepinto                    $:
 */
package pt.lsts.imc;

import java.util.LinkedHashMap;

/**
 * @author zp
 *
 */
public enum IMCFieldType {

	TYPE_UINT8("uint8_t", 1, int.class),
	TYPE_UINT16("uint16_t", 2, int.class),
	TYPE_UINT32("uint32_t", 4, long.class),
	TYPE_INT8("int8_t", 1, int.class),
	TYPE_INT16("int16_t", 2, int.class),
	TYPE_INT32("int32_t", 4, int.class),
	TYPE_INT64("int64_t", 8, long.class),
	TYPE_FP32("fp32_t", 4, float.class),
	TYPE_FP64("fp64_t", 8, double.class),
	TYPE_RAWDATA("rawdata", -1, byte[].class),
	TYPE_PLAINTEXT("plaintext", -1, String.class),
	TYPE_MESSAGE("message", -1, IMCMessage.class),
	TYPE_MESSAGELIST("message-list", -1, IMCMessage[].class);
	
	private String name;
	private int size;
	private Class<?> javaType;
	
	private IMCFieldType(String name, int size, Class<?> javaType) {
		this.name = name;
		this.size = size;
		this.javaType = javaType;
	}

	public String getTypeName() {
		return name;
	}
	
	public int getSizeInBytes() {
		return size;
	}
	
	public boolean isSizeKnown() {
		return size != -1;
	}
	
	public String toString() {
		return name;
	}
	
	public Class<?> getJavaType() {
	    return javaType;
	}
	
	static LinkedHashMap<String, IMCFieldType> types = new LinkedHashMap<String, IMCFieldType>();
	
	static {
		types.put("uint8_t", IMCFieldType.TYPE_UINT8);
		types.put("uint16_t", IMCFieldType.TYPE_UINT16);
		types.put("uint32_t", IMCFieldType.TYPE_UINT32);
		types.put("int8_t", IMCFieldType.TYPE_INT8);
		types.put("int16_t", IMCFieldType.TYPE_INT16);
		types.put("int32_t", IMCFieldType.TYPE_INT32);
		types.put("int64_t", IMCFieldType.TYPE_INT64);
		types.put("fp32_t", IMCFieldType.TYPE_FP32);
		types.put("fp64_t", IMCFieldType.TYPE_FP64);
		types.put("message", IMCFieldType.TYPE_MESSAGE);
		types.put("plaintext", IMCFieldType.TYPE_PLAINTEXT);
		types.put("rawdata", IMCFieldType.TYPE_RAWDATA);
		types.put("message-list", IMCFieldType.TYPE_MESSAGELIST);
	}	
	
	public static IMCFieldType getType(String typeName) {
		return types.get(typeName);
	}
	
}
