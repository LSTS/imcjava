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
 * $Id:: ScriptableGui.java 333 2013-01-02 11:11:44Z zepinto                   $:
 */
package pt.lsts.imc.scripting;

import javax.swing.JOptionPane;

import org.mozilla.javascript.Undefined;

public class ScriptableGui {

    public void alert(Object... obs) {
        
        if (obs == null || obs.length == 0) {
            JOptionPane.showMessageDialog(null, "", "Message from script", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (obs.length == 1) {
            JOptionPane.showMessageDialog(null, obs[0], "Message from script", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (obs.length == 2) {
            JOptionPane.showMessageDialog(null, obs[0], obs[1].toString(), JOptionPane.WARNING_MESSAGE);
            return;
        }
    }
    
    public void info(Object... obs) {
        
        if (obs == null || obs.length == 0) {
            JOptionPane.showMessageDialog(null, "", "Message from script", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        else if (obs.length == 1) {
            JOptionPane.showMessageDialog(null, obs[0], "Message from script", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        else {
            JOptionPane.showMessageDialog(null, obs[0], obs[1].toString(), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }
    
    public void error(Object... obs) {
        
        if (obs == null || obs.length == 0) {
            JOptionPane.showMessageDialog(null, "", "Message from script", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        else if (obs.length == 1) {
            JOptionPane.showMessageDialog(null, obs[0], "Message from script", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        else {
            JOptionPane.showMessageDialog(null, obs[0], obs[1].toString(), JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    
    public boolean confirm(Object... obs) {
        if (obs == null || obs.length == 0) {
            int opt = JOptionPane.showConfirmDialog(null, "", "Question from script", JOptionPane.YES_NO_OPTION);
            return opt == JOptionPane.YES_OPTION;
        }
        else if (obs.length == 1) {
            int opt = JOptionPane.showConfirmDialog(null, obs[0], "Message from script", JOptionPane.YES_NO_OPTION);
            return opt == JOptionPane.YES_OPTION;
        }
        
        else {
            int opt = JOptionPane.showConfirmDialog(null, obs[0], obs[1].toString(), JOptionPane.YES_NO_OPTION);
            return opt == JOptionPane.YES_OPTION;
        }
    }
    
    public Object input(String message) {
        String ret = JOptionPane.showInputDialog(null, message, "input required", JOptionPane.QUESTION_MESSAGE);
        if (ret == null)
            return Undefined.instance;
        return ret;
    }
    
}
