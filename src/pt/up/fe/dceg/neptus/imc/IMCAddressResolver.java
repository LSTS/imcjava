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
 * $Id:: IMCAddressResolver.java 333 2013-01-02 11:11:44Z zepinto              $:
 */
package pt.up.fe.dceg.neptus.imc;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IMCAddressResolver {

    protected LinkedHashMap<Integer, String> addresses = new LinkedHashMap<Integer, String>();
    protected LinkedHashMap<String, Integer> addressesReverse = new LinkedHashMap<String, Integer>();
    protected static final int DEFAULT_ID = (1 << 16) - 1;

    public IMCAddressResolver() {
        try  {
            loadImcAddresses(getAddressesXmlStream());
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
        }  
    }

    public IMCAddressResolver(InputStream is) {        
        try  {
            loadImcAddresses(is);
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try  {
            loadImcAddresses(getAddressesXmlStream());
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }

    public String resolve(int imcId) {
        String name = addresses.get(imcId);
        return (name != null)? name : "unknown ("+imcId+")";
    }


    public int resolve(String imcName) {
        Integer id = addressesReverse.get(imcName);
        if (id == null)
            return -1;    
        return id;
    }

    public void addEntry(int imcid, String imcName) {
        addresses.put(imcid, imcName);
        addressesReverse.put(imcName, imcid);
    }

    protected InputStream getAddressesXmlStream() {
        return ClassLoader.getSystemClassLoader().getResourceAsStream("msgdefs/IMC_Addresses.xml");
    }    

    protected void loadImcAddresses(InputStream is) throws IOException {
        addresses.clear();
        addressesReverse.clear();

        if (is == null) {
            System.err.println("Failed to load imc addresses table");
            return;
        }
        addEntry(DEFAULT_ID, "*");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(is);
            Element root = doc.getDocumentElement();

            NodeList addresses = root.getElementsByTagName("address");

            for (int i = 0; i < addresses.getLength(); i++) {
                Node address = addresses.item(i);
                String id = address.getAttributes().getNamedItem("id").getTextContent();
                String name = address.getAttributes().getNamedItem("name").getTextContent();
                int imcid = 0;
                if (id.contains("x")) {
                    id = id.substring(id.indexOf('x')+1);
                    imcid = Integer.parseInt(id.substring(id.indexOf('x')+1), 16);
                }
                else {
                    imcid = Integer.parseInt(id);
                }
                addEntry(imcid, name);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }
}
