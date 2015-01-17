/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2015, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: KrakenScript.java 333 2013-01-02 11:11:44Z zepinto                    $:
 */
package pt.lsts.imc.scripting;

import java.util.LinkedHashMap;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;

import pt.lsts.imc.IMCMessage;

public class KrakenScript extends ScriptableObject {

    private static final long serialVersionUID = 1L;

    protected String name, source;
    private LinkedHashMap<String, Object> env = new LinkedHashMap<String, Object>();
    
    public KrakenScript(String name, String source) {
        this.name = name;
        this.source = source;
    }
    
    @Override
    public void put(String name, Scriptable start, Object value) {
        env.put(name, value);
        super.put(name, start, value);
    }

    @Override
    public String getClassName() {
        return "Script";
    }

  
    public IMCMessage serialize() {
        return null;
    }

    public static void main(String[] args) throws Exception {
        // initialization
        Context cx = Context.enter();
        Global s = new Global();
        s.init(cx);
        cx.evaluateString(s, "importClass(java.lang.Thread);", "x", 0, null);
        ScriptableObject.defineClass(s, KrakenScript.class);
        // end of initialization

        cx.evaluateString(s, "m = new Message('EstimatedState');\n"
                + "spawn(function() {print('Hello!'); Thread.sleep(5000); print('Goodbye!');});\n"
                + "m.ref = 'NED_LLD';\n" + "m.x = -100.567;\n" + "m.p = (Math.PI / 180) * 10;\n" + "m.dump();",
                "inline", 1, null);
        Context.exit();
    }
}
