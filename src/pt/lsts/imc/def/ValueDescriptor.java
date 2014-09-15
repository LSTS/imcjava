package pt.lsts.imc.def;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class ValueDescriptor {

	private String name, abbrev, prefix;
	private LinkedHashMap<Long, String> values = new LinkedHashMap<Long, String>();

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
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the values
	 */
	public LinkedHashMap<Long, String> getValues() {
		return values;
	}

	public static class ValueType {
		int value;
		String name, abbrev;

		/**
		 * @return the value
		 */
		public int getValue() {
			return value;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(int value) {
			this.value = value;
		}

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
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{\n");
		
		for (Entry<Long, String> entry : getValues().entrySet()) {
			sb.append("\t"+entry.getKey()+" -> \t"+entry.getValue()+"\n");
		}
		
		sb.append("}");
		return sb.toString();
	}

}
