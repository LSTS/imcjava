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
 */
package pt.lsts.imc;


/**
 *  IMC Message File Fragment (912)<br/>
 *  File fragment.<br/>
 */

public class FileFragment extends IMCMessage {

	public static final int ID_STATIC = 912;

	public FileFragment() {
		super(ID_STATIC);
	}

	public FileFragment(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FileFragment(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FileFragment create(Object... values) {
		FileFragment m = new FileFragment();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FileFragment clone(IMCMessage msg) throws Exception {

		FileFragment m = new FileFragment();
		if (msg == null)
			return m;
		if(msg.definitions != m.definitions){
			msg = msg.cloneMessage();
			IMCUtil.updateMessage(msg, m.definitions);
		}
		else if (msg.getMgid()!=m.getMgid())
			throw new Exception("Argument "+msg.getAbbrev()+" is incompatible with message "+m.getAbbrev());

		m.getHeader().values.putAll(msg.getHeader().values);
		m.values.putAll(msg.values);
		return m;
	}

	public FileFragment(String id, int frag_number, int num_frags, byte[] data) {
		super(ID_STATIC);
		if (id != null)
			setId(id);
		setFragNumber(frag_number);
		setNumFrags(num_frags);
		if (data != null)
			setData(data);
	}

	/**
	 *  @return id - plaintext
	 */
	public String getId() {
		return getString("id");
	}

	/**
	 *  @param id id
	 */
	public FileFragment setId(String id) {
		values.put("id", id);
		return this;
	}

	/**
	 *  @return Fragment Number - uint16_t
	 */
	public int getFragNumber() {
		return getInteger("frag_number");
	}

	/**
	 *  @param frag_number Fragment Number
	 */
	public FileFragment setFragNumber(int frag_number) {
		values.put("frag_number", frag_number);
		return this;
	}

	/**
	 *  @return Total Number of fragments - uint16_t
	 */
	public int getNumFrags() {
		return getInteger("num_frags");
	}

	/**
	 *  @param num_frags Total Number of fragments
	 */
	public FileFragment setNumFrags(int num_frags) {
		values.put("num_frags", num_frags);
		return this;
	}

	/**
	 *  @return Fragment Data - rawdata
	 */
	public byte[] getData() {
		return getRawData("data");
	}

	/**
	 *  @param data Fragment Data
	 */
	public FileFragment setData(byte[] data) {
		values.put("data", data);
		return this;
	}

}
