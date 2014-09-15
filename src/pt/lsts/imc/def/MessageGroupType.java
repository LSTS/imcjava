package pt.lsts.imc.def;

import java.util.ArrayList;

import pt.lsts.imc.IMCMessageType;

public class MessageGroupType {

	private String name, abbrev;
	private ArrayList<String> subTypes = new ArrayList<String>();
	private IMCMessageType msgType;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the abbrev
	 */
	public String getAbbrev() {
		return abbrev;
	}

	/**
	 * @param abbrev
	 *            the abbrev to set
	 */
	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}

	/**
	 * @return the subTypes
	 */
	public ArrayList<String> getSubTypes() {
		return subTypes;
	}

	/**
	 * @return the msgType
	 */
	public IMCMessageType getMsgType() {
		return msgType;
	}

	/**
	 * @param msgType the msgType to set
	 */
	public void setMsgType(IMCMessageType msgType) {
		this.msgType = msgType;
	}
}
