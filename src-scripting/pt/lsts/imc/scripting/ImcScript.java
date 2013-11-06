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
 * $Id:: ImcScript.java 333 2013-01-02 11:11:44Z zepinto                       $:
 */
package pt.lsts.imc.scripting;

import java.io.File;
import java.io.FileReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.debugger.Main;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.JSConsole;

public class ImcScript {

    protected Context context;
    protected Global global;
    protected Script script;

    public ImcScript() throws Exception {
        init();
    }

    public void showConsole() {
        JSConsole console = new JSConsole(new String[0]);
        console.pack();
        console.setSize(800, 600);
        console.setVisible(true);
    }

    public void exec(File f) throws Exception {
        script = context.compileReader(new FileReader(f), f.getName(), 1, null);
        script.exec(context, global);
    }

    public void openIDE() {
        Main m = Main.mainEmbedded(context.getFactory(), global, "JS Debugger");
        System.setErr(m.getErr());
        System.setOut(m.getOut());
        System.setIn(m.getIn());
    }

    protected void init() throws Exception {
        context = Context.enter();
        global = new Global();
        global.init(context);
        
        ScriptableObject.defineClass(global, ScriptableMessage.class);
        ScriptableObject.defineClass(global, ScriptableIMC.class);
        
        Object o = Context.javaToJS(new ScriptableConsole(), global);
        ScriptableObject.putProperty(global, "console", o);
        
        o = Context.javaToJS(new ScriptableGui(), global);
        ScriptableObject.putProperty(global, "gui", o);

        Scriptable imc = context.newObject(global, "IMC", null);
        global.put("imc", global, imc);
    }

    public static void main(String[] args) {
        if (args.length < 1 || args[0].equals("--debug")) {
            try {
                ImcScript is = new ImcScript();
                is.openIDE();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        else {
            File f = new File(args[0]);
            if (!f.canRead()) {
                System.out.println("Unable to open script " + args[0] + "\n");
                System.exit(1);
            }
            else {
                try {
                    ImcScript is = new ImcScript();
                    is.exec(f);
                }
                catch (Exception e) {
                    System.err.println("[Error] " + e.getMessage());
                }
            }
        }
    }
}
