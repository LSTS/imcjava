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
package pt.lsts.imc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Vector;

public class ImcDiff {

    public static void diff(IMCDefinition first, IMCDefinition second) {
        String firstName = "IMC-"+first.getVersion();
        String secondName = "IMC-"+second.getVersion();

        System.out.println("Comparison between "+firstName+" and "+secondName+": \n");

        LinkedHashMap<Integer, String> deletedMessages = new LinkedHashMap<Integer, String>();
        LinkedHashMap<Integer, String> addedMessages = new LinkedHashMap<Integer, String>();
        LinkedHashMap<Integer, String> renamedMessages = new LinkedHashMap<Integer, String>();
        LinkedHashMap<Integer, String> maintainedMessages = new LinkedHashMap<Integer, String>();

        for (String msgName : first.getMessageNames()) {
            String newName = second.getMessageName(first.getMessageId(msgName));

            if (newName == null)
                deletedMessages.put(first.getMessageId(msgName), msgName);
            else if (!msgName.equals(newName))
                renamedMessages.put(first.getMessageId(msgName), msgName);          
            else
                maintainedMessages.put(first.getMessageId(msgName), msgName);
        }

        for (String msgName : second.getMessageNames()) {
            String oldName = first.getMessageName(second.getMessageId(msgName));

            if (oldName == null)
                addedMessages.put(second.getMessageId(msgName), msgName);            
        }
        Vector<Integer> indexes = new Vector<Integer>();
        
        System.out.println("REMOVED:");
        indexes.addAll(deletedMessages.keySet());
        Collections.sort(indexes);
        for (int i : indexes) {
            System.out.println("   - "+deletedMessages.get(i)+" ("+i+")");
        }

        System.out.println("\nADDED:");
        indexes.clear();
        indexes.addAll(addedMessages.keySet());
        Collections.sort(indexes);
        for (int i : indexes) {
            System.out.println("   + "+addedMessages.get(i)+" ("+i+")");
        }

        System.out.println("\nRENAMED:");
        indexes.clear();
        indexes.addAll(renamedMessages.keySet());
        Collections.sort(indexes);
        for (int i : indexes) {
            System.out.println("   ? "+renamedMessages.get(i)+" renamed to "+second.getMessageName(i)+" ("+i+")");
        }

        System.out.println("\nCHANGED:");

        Vector<Integer> maintained = new Vector<Integer>();
        maintained.addAll(maintainedMessages.keySet());
        maintained.addAll(renamedMessages.keySet());

        Collections.sort(maintained);

        for (int i : maintained) {
            boolean changed = false;
            IMCMessageType firstType = first.getType(i);
            IMCMessageType secondType = second.getType(i);

            for (String field : firstType.getFieldNames()) {
                IMCFieldType ft = firstType.getFieldType(field);
                IMCFieldType st = secondType.getFieldType(field);

                if (st == null) {
                    System.out.println("   - "+second.getMessageName(i)+"."+field+" was removed");
                    changed = true;
                }
                else if (st != ft) {
                    System.out.println("   ? "+second.getMessageName(i)+"."+field+" changed from "+ft+" to "+st);
                    changed = true;
                }
                else {
                    String subFirst = firstType.getFieldSubtype(field);
                    String subSecond = secondType.getFieldSubtype(field);

                    if (subFirst != subSecond) {
                        System.out.println("   ? "+second.getMessageName(i)+"."+field+" is now sub-typed as "+subSecond+".");
                        changed = true;
                    }
                }
            }        

            for (String field : secondType.getFieldNames()) {
                IMCFieldType ft = firstType.getFieldType(field);

                if (ft == null) {
                    System.out.println("   + "+second.getMessageName(i)+"."+field+" was added");
                    changed = true;
                }
            }

            if (changed)
                System.out.println();


        }

    }
}
