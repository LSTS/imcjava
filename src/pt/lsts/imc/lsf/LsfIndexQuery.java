/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2013, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: LsfIndexQuery.java 333 2013-01-02 11:11:44Z zepinto                   $:
 */
package pt.lsts.imc.lsf;

import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.lsf.LsfIndexQuery.FILTER_TYPE;

/**
 * @author zp
 *
 */
public class LsfIndexQuery {
    public enum FILTER_TYPE {LIKE, EQUALS, LESS_THAN, MORE_THAN}
    
    protected int msgType = -1; 
    protected  int limit = 0;
    protected Vector<LsfQueryFilter> filters = new Vector<LsfQueryFilter>();
        
    
    public void setMessageType(int type) {
        msgType = type;
    }
    
    public void addFilter(FILTER_TYPE type, String fieldName, Object value) {
        filters.add(new LsfQueryFilter(type, fieldName, value));
    }
    
    public Vector<IMCMessage> doQuery(LsfIndex index, int limit) {
        Vector<IMCMessage> result = new Vector<IMCMessage>();
        
        if (limit == 0)
            limit = Integer.MAX_VALUE;
        
        if (msgType != -1) {
            for (int i = index.getFirstMessageOfType(msgType); i != -1 && result.size() < limit; i = index.getNextMessageOfType(msgType, i+1)) {
                IMCMessage m = index.getMessage(i);
                boolean matches = true;
                for (LsfQueryFilter filter : filters) {
                    if (!filter.matches(m)) {
                        matches = false;
                        break;
                    }
                }
                if (matches)
                    result.add(m);
            }
        }
        else {
            for (int i = 0; i < index.getNumberOfMessages() && result.size() < limit; i++) {
                IMCMessage m = index.getMessage(i);
                boolean matches = true;
                for (LsfQueryFilter filter : filters) {
                    if (!filter.matches(m)) {
                        matches = false;
                        break;
                    }
                }
                if (matches)
                    result.add(m);
            }
        }
        return result;
    }
    
    
    public static void main(String[] args) throws Exception {
        LsfIndex index = new LsfIndex(new File("/home/zp/Desktop/143245_rows_minus1.5m_1000rpm/Data.lsf"), new IMCDefinition(new FileInputStream(new File("/home/zp/Desktop/143245_rows_minus1.5m_1000rpm/IMC.xml"))));
        LsfIndexQuery query = new LsfIndexQuery();
        query.setMessageType(IMCDefinition.getInstance().getMessageId("EntityState"));
        query.addFilter(FILTER_TYPE.LIKE, "description", "^id.*");
        System.out.println(query.doQuery(index, 100).size());//firstElement().dump(System.out);
        query.doQuery(index, 1).firstElement().dump(System.out);
    }
}

class LsfQueryFilter {
    
    public LsfIndexQuery.FILTER_TYPE type;
    public String fieldName;
    public Object value;   
    
    
    public LsfQueryFilter(FILTER_TYPE type, String field, Object value) {
        this.type = type;
        this.fieldName = field;
        this.value = value;
    }
    
    public boolean matches(IMCMessage message) {
        switch (type) {
            case EQUALS:
                return message.getValue(fieldName).equals(value);
            case LESS_THAN:
                try {
                    double val = Double.parseDouble(value.toString());
                    return message.getDouble(fieldName) < val;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            case MORE_THAN:
                try {
                    double val = Double.parseDouble(value.toString());
                    return message.getDouble(fieldName) > val;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            case LIKE:
                return message.getString(fieldName).matches(value.toString());
            default:
                return false;
        }       
    }
    

    
}