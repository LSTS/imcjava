/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2025, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: IMessage.java 334 2013-01-02 11:20:47Z zepinto                        $:
 */
package pt.lsts.neptus.messages;

public interface IMessage extends Cloneable {

	/**
	 * @return the message serial ID
	 */
	public int getMgid();

	/**
	 * @return the message abbreviated name
	 */
	public String getAbbrev();

	/**
	 * @return the message human readable name
	 */
	public String getLongName();

	/**
	 * @return the list of field names
	 */
	public String[] getFieldNames();

	/**
	 * Verifies if the message is valid by generating exceptions when the message is not valid
	 * @throws InvalidMessageException
	 */
	public void validate() throws InvalidMessageException;

	/**
	 * Retrieve the value of a field as an Object
	 * @param fieldName The name of the field to consult
	 * @return The value of the given field or <b>null</b> if the field does not exist or an error occurred
	 */
	public Object getValue(String fieldName);

	/**
	 * Retrieve the value of a field as a String
	 * @param fieldName The name of the field to consult
	 * @return The value of the field as a String or <b>null</b> in case of an error
	 */
	public String getAsString(String fieldName);

	/**
	 * Retrieve the value of the field as a Number
	 * @param fieldName The name of the field to consult
	 * @return The numeric value of the field of <b>null</b> in case the field is not numeric / does not exist
	 */
	public Number getAsNumber(String fieldName);

	/**
	 * Retrieve the field type of the given field
	 * @return The field type of given field like "uint8_t" or "plaintext"
	 */
	public String getTypeOf(String fieldName);

	/**
	 * Retrieves the units of the given field
	 * @return The units of the field or an empty String in case no units are defined
	 */
	public String getUnitsOf(String fieldName);

	/**
	 * Verify if the message has the given flag set
	 * @return <b>true</b> if the flag is set or <b>false</b> otherwise
	 */
	public boolean hasFlag(String flagName);

	/**
	 * Retrieve the long name (human readable) of a given field
	 * @param fieldName The field's (abbreviated) name
	 * @return the long name (human readable) of a given field
	 */
	public String getLongFieldName(String fieldName);

	/**
	 * Sets the value of a field to the given Object
	 * @param fieldName The name of the field to be changed
	 * @param value The new value to be given to the field
	 * @return TODO
	 * @throws InvalidFieldException in case this field does not exist in the message
	 */
	public IMessage setValue(String fieldName, Object value) throws InvalidFieldException;


	
	public IMessageProtocol<? extends IMessage> getProtocolFactory();

	/**
	 * @return a copy of this message
	 */
	public <M extends IMessage> M cloneMessage();
	
	public Object getHeaderValue(String field);
}
