package pt.lsts.imc.net;

import pt.lsts.imc.IMCMessage;

/**
 * This interface is implemented by components that provide message logging capabilities 
 * @author zp
 */
public interface IMessageLogger {

	/**
	 * Store this message as it is
	 * @param message The message to be stored
	 */
	public void logMessage(IMCMessage message) throws Exception;
	
}
