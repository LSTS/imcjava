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
import java.util.Vector;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.LblBeacon;

public class LblConfigAdapter implements IMessageAdapter {
    
    
    protected IMCMessage original;
    protected String originalVersion;
    
    Vector<LblBeacon> beacons = new Vector<LblBeacon>();
    
    /**
     * @return the beacons
     */
    public final Vector<LblBeacon> getBeacons() {
        return beacons;
    }

    /**
     * @param beacons the beacons to set
     */
    public void setBeacons(Vector<LblBeacon> beacons) {
        this.beacons = beacons;
    }

    public Collection<String> getCompatibleMessages() {
        return Arrays.asList("LblConfig");
    };
    
    public IMCMessage getData(IMCDefinition definitions) {
        if (definitions.getVersion().equals(originalVersion))
            return original;        
        boolean preIMC5 = definitions.getVersion().compareTo("5.0.0") < 0;
        IMCMessage beaconConfig = definitions.create("LblConfig");
        if (preIMC5) {
            for (int i = 0; i < beacons.size(); i++)
                beaconConfig.setValue("beacon"+i, beacons.get(i));
        }
        else {
            beaconConfig.setValue("beacons", beacons);
        }
        
        return beaconConfig;
    };
    
    @Override
    public void setData(IMCMessage msg) {
        boolean preIMC5 = msg.getMessageType().getImcVersion().compareTo("5.0.0") < 0;
        try {
            if (preIMC5) {
                for (int i = 0; i < 6; i++) {
                    LblBeacon b = msg.getMessage(LblBeacon.class, "beacon"+i);
                    if (b != null)
                        beacons.add(b);
                }
            }
            else {
                beacons = msg.getMessageList("beacons", LblBeacon.class);
            }      
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }
}
