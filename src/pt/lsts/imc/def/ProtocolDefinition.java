package pt.lsts.imc.def;

import java.util.Collection;

import pt.lsts.imc.IMCMessageType;

public interface ProtocolDefinition {
	String getVersion();

	String getName();

	String getDefinitionMD5();

	long getSyncWord();

	IMCMessageType getHeader();

	IMCMessageType getFooter();

	Collection<ValueDescriptor> getGlobalEnumerations();

	Collection<ValueDescriptor> getGlobalBitfields();

	Collection<IMCMessageType> getMessageDefinitions();
}
