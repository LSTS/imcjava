/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2018, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: Kraken.java 333 2013-01-02 11:11:44Z zepinto                          $:
 */
package pt.lsts.imc.scripting;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

public class Kraken {

    protected Context context;
    protected KrakenGlobal global;
    protected ScriptableObject scriptEnvironment;

    protected LinkedHashMap<String, Script> compiledScripts = new LinkedHashMap<String, Script>();    

    public Kraken() {
        context = Context.enter();
        global = new KrakenGlobal();
        global.init(context);
        try {
            ScriptableObject.defineClass(global, ScriptableMessage.class);
            ScriptableObject.defineClass(global, ScriptableIMC.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Object o = Context.javaToJS(new ScriptableConsole(), global);
        ScriptableObject.putProperty(global, "console", o);

        o = Context.javaToJS(new ScriptableGui(), global);
        ScriptableObject.putProperty(global, "gui", o);

        Scriptable imc = context.newObject(global, "IMC", null);
        global.put("imc", global, imc);   
    }



    public boolean installScript(String name, String source) {
        try {
            Script script = context.compileString(source, name, 1, null);
            if (script != null) {
                compiledScripts.put(name, script);
                return true;
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean deleteScript(String name) {
        return compiledScripts.remove(name) != null;     
    }

    public boolean testCondition(String name) throws Exception {
        Script s = compiledScripts.get(name);
        if (s == null)
            throw new Exception("Script does not exists");
        try {
            Object o = s.exec(context, global);
            if (o instanceof Boolean)
                return (Boolean)o;
            else if (o instanceof Number) {
                return ((Number)o).longValue() != 0;            
            }
            else return !(o == null) && !(o instanceof Undefined);
        }
        catch (Exception e) {
            reportError(name, e.getMessage());
            return false;
        }
    }

    public Collection<Boolean> testConditions(Collection<String> conditions) {
        Vector<Boolean> results = new Vector<Boolean>();

        for (String cond : conditions) {
            try {
                results.add(testCondition(cond));
            }
            catch (Exception e) {
                results.add(false);
            }
        }

        return results;
    }

    public void reportError(String scriptName, String error) {
        System.err.println("Error while executing \""+scriptName+"\": "+error);
    }

    public static void main(String[] args) throws Exception {
        Kraken kraken = new Kraken();
        kraken.installScript("x", "setInterval(function() {print ('5!')}, 5);setInterval(function() {clearInterval();}, 100);");
        System.out.println(kraken.testCondition("x"));
    }
}
