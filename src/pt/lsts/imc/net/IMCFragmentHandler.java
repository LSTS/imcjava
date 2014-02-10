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

public class IMCFragmentHandler {

	private static int uid = (int) (Math.random() * 255);
	private LinkedHashMap<Integer, Vector<MessagePart>> incoming = new LinkedHashMap<Integer, Vector<MessagePart>>();

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

		return IMCDefinition.getInstance().nextMessage(
				new ByteArrayInputStream(res));
	}

	public MessagePart[] fragment(IMCMessage message, int maxFragLength)
			throws Exception {
		int id = uid = (uid + 1) % 255;
		int dataFragLength = maxFragLength
				- IMCDefinition.getInstance().headerLength() - 5;
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
