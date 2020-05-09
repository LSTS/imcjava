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
 * $Id:: ScriptableConsole.java 333 2013-01-02 11:11:44Z zepinto               $:
 */
package pt.lsts.imc.scripting;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScriptableConsole {

    public SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");

    public void log(String message) {
        log(message, null);
    }

    public void error(String message) {
        error(message, null);
    }

    public void debug(String message) {
        debug(message, null);
    }

    public void warn(String message) {
        warn(message, null);
    }

    public void info(String message) {
        info(message, null);
    }

    public void log(String message, Object o) {
        String date = format.format(new Date());
        System.out.print("[" + date);
        if (o != null)
            System.out.print("|" + o.toString());
        System.out.print("] ");
        System.out.println(message);

    }

    public void error(String message, Object o) {
        String date = format.format(new Date());
        System.err.print("[" + date);
        if (o != null)
            System.err.print("|" + o.toString());
        System.err.print("] ");
        System.err.println(message);
    }

    public void debug(String message, Object o) {
        if (o == null)
            o = "";
        else
            o = " " + o;
        log(message, "DEBUG" + o);
    }

    public void info(String message, Object o) {
        if (o == null)
            o = "";
        else
            o = " " + o;
        log(message, "INFO" + o);
    }

    public void warn(String message, Object o) {
        if (o == null)
            o = "";
        else
            o = " " + o;
        log(message, "WARN" + o);
    }
}
