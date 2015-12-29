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
 * $Id::                                                                       $:
 */
package pt.lsts.imc.types;

import java.util.Arrays;
import java.util.Collection;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;

public class FieldsRenamedAdapter implements IMessageAdapter {

    protected String[] imc4FieldNames, imc5FieldNames;
    protected Object[] values;
    protected String msgName;
    
    protected IMCMessage original;
    protected String originalVersion;
    
    public FieldsRenamedAdapter(String msgName, String[] imc4FieldNames, String[] imc5FieldNames) {
        this.msgName = msgName;
        this.imc4FieldNames = imc4FieldNames;
        this.imc5FieldNames = imc5FieldNames;
        values = new Object[imc4FieldNames.length];
        
    }
    
    @Override
    public IMCMessage getData(IMCDefinition definitions) {
        if (definitions.getVersion().equals(originalVersion))
            return original;
        
        boolean preIMC5 = definitions.getVersion().compareTo("5.0.0") < 0;
        IMCMessage msg = definitions.create(msgName);
        try {
            msg.copyFrom(original);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        if (preIMC5) {
            for (int i = 0; i < imc4FieldNames.length; i++)
                msg.setValue(imc4FieldNames[i], values[i]);
        }
        else {
            for (int i = 0; i < imc5FieldNames.length; i++)
                msg.setValue(imc5FieldNames[i], values[i]);
        }
        return msg;
    }

    @Override
    public void setData(IMCMessage msg) {
        this.original = msg;
        originalVersion = msg.getMessageType().getImcVersion();
        boolean preIMC5 = originalVersion.compareTo("5.0.0") < 0;
        
        if (preIMC5) {
            for (int i = 0; i < imc4FieldNames.length; i++)
                values[i] = msg.getValue(imc4FieldNames[i]);
        }
        else {
            for (int i = 0; i < imc5FieldNames.length; i++)
                values[i] = msg.getValue(imc5FieldNames[i]);
        }
    }

    @Override
    public Collection<String> getCompatibleMessages() {
        return Arrays.asList(msgName);
    }

}
