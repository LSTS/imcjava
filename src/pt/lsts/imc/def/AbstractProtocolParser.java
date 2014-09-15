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
