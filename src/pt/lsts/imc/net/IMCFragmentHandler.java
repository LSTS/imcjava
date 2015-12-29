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
package pt.lsts.imc.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.MessagePart;

/**
 * This class is used to split / reassemble any IMC message to/from MessagePart
 * messages
 * 
 * @author zp
 */
public class IMCFragmentHandler {

	private static int uid = (int) (Math.random() * 255);
	private LinkedHashMap<Integer, Vector<MessagePart>> incoming = new LinkedHashMap<Integer, Vector<MessagePart>>();
	private IMCDefinition definitions;		
	
	public IMCFragmentHandler(IMCDefinition definitions) {
		this.definitions = definitions;
		
	}
	/**
	 * Add an incoming fragment
	 * 
	 * @param fragment
	 *            The fragment to add to the list of incoming fragments
	 * @return The resulting assembled message if this was the last fragment
	 *         required to build it or <code>null</code> if this is not the last
	 *         fragment.
	 */
	public IMCMessage setFragment(MessagePart fragment) {
		int hash = (fragment.getSrc() + "" + fragment.getUid()).hashCode();
		if (!incoming.containsKey(hash)) {
			incoming.put(hash, new Vector<MessagePart>());
		}
		incoming.get(hash).add(fragment);
		if (incoming.get(hash).size() >= fragment.getNumFrags()) {
			Vector<MessagePart> parts = incoming.get(hash);
			incoming.remove(hash);
			try {
				IMCMessage m = reassemble(parts);
				return m;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Given a list of message fragments try to reassemble the fragments into an IMCMessage
	 * @param parts The fragments to process
	 * @return The resulting assembled message
	 * @throws Exception In case the fragments do not result in a valid message
	 */
	public IMCMessage reassemble(List<MessagePart> parts) throws Exception {
		Collections.sort(parts, new Comparator<MessagePart>() {
			@Override
			public int compare(MessagePart o1, MessagePart o2) {
				return o1.getFragNumber() - o2.getFragNumber();
			}
		});

		int totalSize = 0;
		for (MessagePart p : parts) {
			totalSize += p.getData().length;
		}
		byte[] res = new byte[totalSize];
		int pos = 0;
		for (MessagePart p : parts) {
			System.arraycopy(p.getData(), 0, res, pos, p.getData().length);
			pos += p.getData().length;
		}

		return definitions.nextMessage(
				new ByteArrayInputStream(res));
	}

	/**
	 * Fragment a message into smaller MessagePart's
	 * @param message The message to be fragmented
	 * @param maxFragLength The maximum size of any generated MessagePart. Must be greater than 25.
	 * @return A List of messages containing fragments of the original message
	 * @throws Exception In case the message cannot be fragmented
	 */
	public MessagePart[] fragment(IMCMessage message, int maxFragLength)
			throws Exception {
		int id = uid = (uid + 1) % 255;
		int dataFragLength = maxFragLength
				- definitions.headerLength() - 5;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		message.serialize(new IMCOutputStream(baos));
		byte[] data = baos.toByteArray();
		int part = 0;
		int pos = 0;
		Vector<MessagePart> parts = new Vector<MessagePart>();
		int numfrags = (int) Math.ceil(data.length / (double) dataFragLength);

		while (pos < data.length) {
			int remaining = data.length - pos;
			int size = Math.min(dataFragLength, remaining);
			byte[] partData = Arrays.copyOfRange(data, pos, pos + size);
			pos += size;
			MessagePart tmp = new MessagePart((short) id, (short) part++,
					(short) numfrags, partData);
			tmp.setSrc(message.getSrc());
			tmp.setSrcEnt(message.getSrcEnt());
			tmp.setDst(message.getDst());
			tmp.setDstEnt(message.getDstEnt());
			tmp.setTimestamp(message.getTimestamp());
			parts.add(tmp);
		}

		return parts.toArray(new MessagePart[] {});
	}
}
