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
 */
package pt.lsts.imc;

/**
 *  IMC Message Header 
 */

public class Header extends IMCMessage {

	public Header() {
		super(IMCDefinition.getInstance().getHeaderType());
	}

	public Header(IMCDefinition defs) {
		super(defs.getHeaderType());
	}

	/**
	 *  @return Synchronization Number - uint16_t
	 */
	public int get_sync() {
		return getInteger("sync");
	}

	/**
	 *  @return Message Identification Number - uint16_t
	 */
	public int get_mgid() {
		return getInteger("mgid");
	}

	/**
	 *  @return Message size (byte) - uint16_t
	 */
	public int get_size() {
		return getInteger("size");
	}

	/**
	 *  @return Time stamp (s) - fp64_t
	 */
	public double get_timestamp() {
		return getDouble("timestamp");
	}

	/**
	 *  @return Source Address - uint16_t
	 */
	public int get_src() {
		return getInteger("src");
	}

	/**
	 *  @return Source Entity - uint8_t
	 */
	public short get_src_ent() {
		return (short) getInteger("src_ent");
	}

	/**
	 *  @return Destination Address - uint16_t
	 */
	public int get_dst() {
		return getInteger("dst");
	}

	/**
	 *  @return Destination Entity - uint8_t
	 */
	public short get_dst_ent() {
		return (short) getInteger("dst_ent");
	}

	/**
	 *  @param sync Synchronization Number
	 */
	public Header set_sync(int sync) {
		values.put("sync", sync);
		return this;
	}

	/**
	 *  @param mgid Message Identification Number
	 */
	public Header set_mgid(int mgid) {
		values.put("mgid", mgid);
		return this;
	}

	/**
	 *  @param size Message size (byte)
	 */
	public Header set_size(int size) {
		values.put("size", size);
		return this;
	}

	/**
	 *  @param timestamp Time stamp (s)
	 */
	public Header set_timestamp(double timestamp) {
		values.put("timestamp", timestamp);
		return this;
	}

	/**
	 *  @param src Source Address
	 */
	public Header set_src(int src) {
		values.put("src", src);
		return this;
	}

	/**
	 *  @param src_ent Source Entity
	 */
	public Header set_src_ent(short src_ent) {
		values.put("src_ent", src_ent);
		return this;
	}

	/**
	 *  @param dst Destination Address
	 */
	public Header set_dst(int dst) {
		values.put("dst", dst);
		return this;
	}

	/**
	 *  @param dst_ent Destination Entity
	 */
	public Header set_dst_ent(short dst_ent) {
		values.put("dst_ent", dst_ent);
		return this;
	}

}
