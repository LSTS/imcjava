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
 */
package pt.lsts.imc.def;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.LinkedHashMap;

import pt.lsts.imc.IMCMessageType;

public abstract class AbstractProtocolParser implements ProtocolDefinition {

	protected String version = "n/a", md5 = "n/a", name = "n/a";
	protected int sync = 0;

	protected LinkedHashMap<String, ValueDescriptor> enums = new LinkedHashMap<String, ValueDescriptor>();
	protected LinkedHashMap<String, ValueDescriptor> bitfields = new LinkedHashMap<String, ValueDescriptor>();
	protected LinkedHashMap<String, IMCMessageType> messages = new LinkedHashMap<String, IMCMessageType>();
	protected IMCMessageType header = null, footer = null;

	public abstract ProtocolDefinition parseDefinitions(InputStream is) throws Exception;

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getDefinitionMD5() {
		return md5;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getSyncWord() {
		return sync;
	}

	@Override
	public IMCMessageType getHeader() {
		return header;
	}

	@Override
	public IMCMessageType getFooter() {
		return footer;
	}

	@Override
	public Collection<ValueDescriptor> getGlobalBitfields() {
		return bitfields.values();
	}

	@Override
	public Collection<ValueDescriptor> getGlobalEnumerations() {
		return enums.values();
	}

	@Override
	public Collection<IMCMessageType> getMessageDefinitions() {
		return messages.values();
	}

	public byte[] computeMD5(InputStream defStream) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			MessageDigest md = MessageDigest.getInstance("MD5");

			byte[] extra = new byte[50000];
			int ret = 0;
			for (;;) {
				ret = defStream.read(extra);
				if (ret != -1) {
					byte[] extra1 = new byte[ret];
					System.arraycopy(extra, 0, extra1, 0, ret);
					baos.write(extra1);
					baos.flush();
				} else {
					break;
				}
			}

			md.update(baos.toByteArray());
			return md.digest();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String computeMD5String(InputStream defStream) {
		byte[] md5Array = computeMD5(defStream);
		if (md5Array != null) {
			BigInteger bi = new BigInteger(1, md5Array);
			return bi.toString(16);
		} else {
			return "";
		}
	}

}
