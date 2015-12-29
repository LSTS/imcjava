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

public class SonarDataAdapter implements IMessageAdapter {

    protected IMCMessage original;
    protected String originalVersion;
    
    protected byte[] data;
    protected int bitsPerPoint, frequency;
    protected double minRange, maxRange;
    
    @Override
    public IMCMessage getData(IMCDefinition definitions) {
        if (definitions.getVersion().equals(originalVersion))
            return original;
        
        boolean preIMC5 = definitions.getVersion().compareTo("5.0.0") < 0;

        if (preIMC5) {
            IMCMessage msg = new IMCMessage("SidescanPing");
            msg.setValue("data", data);
            msg.setValue("range", maxRange);
            msg.setValue("frequency", frequency);
            return msg;
        }
        else {
            IMCMessage msg = new IMCMessage("SonarData");
            msg.setValue("data", data);
            msg.setValue("max_range", maxRange);
            msg.setValue("min_range", minRange);
            msg.setValue("frequency", frequency);
            msg.setValue("bits_per_point", bitsPerPoint);
            return msg;
        }
    }

    @Override
    public void setData(IMCMessage msg) {
        this.original = msg;
        this.originalVersion = msg.getMessageType().getImcVersion();
        
        boolean preIMC5 = originalVersion.compareTo("5.0.0") < 0;
        
        data = msg.getRawData("data");
        frequency = msg.getInteger("frequency");
        
        if (preIMC5) {
            minRange = 0;
            maxRange = msg.getDouble("range");
            bitsPerPoint = 8;
        }
        else {
            minRange = msg.getInteger("min_range");
            maxRange = msg.getInteger("max_range");            
            bitsPerPoint = msg.getInteger("bits_per_point");
        }
    }

    @Override
    public Collection<String> getCompatibleMessages() {
        return Arrays.asList("SidescanPing", "SonarData");
    }
}
