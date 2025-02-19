/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2025, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: MergeBattle.java 334 2013-01-02 11:20:47Z zepinto                     $:
 */
package pt.lsts.imc.merge;

import java.io.File;

import pt.lsts.imc.lsf.LsfIndex;

public class MergeBattle {

    public static void main(String[] args) throws Exception {
        
        String dir_input1, dir_input2, dir_output_ribcar, dir_output_rsilva;
        
        
        dir_input1 = "/home/zp/teste1/";
        dir_input2 = "/home/zp/teste2/";
        
        dir_output_ribcar = "/home/zp/Desktop/logs/ribcar/";
        dir_output_rsilva = "/home/zp/Desktop/logs/rsilva/";

        long startMillis = System.currentTimeMillis();
        
        System.out.println(startMillis);
        
        LsfIndex index1 = new LsfIndex(new File(dir_input1+"Data.lsf"), null);
        LsfIndex index2 = new LsfIndex(new File(dir_input1+"Data.lsf"), null);
        
        new File(dir_output_ribcar).mkdirs();
        new File(dir_output_rsilva).mkdirs();
         
        LsfLogMerge.writestream(dir_output_ribcar+"Data.lsf", index1, index2);
        
        System.out.println(System.currentTimeMillis()+", "+(System.currentTimeMillis() - startMillis));

        
        startMillis = System.currentTimeMillis();
        
        System.out.println(startMillis);
        
        LSFMerger.main(new String[] {"-o", dir_output_rsilva, dir_input1, dir_input2});
     
        System.out.println(System.currentTimeMillis()+", "+(System.currentTimeMillis() - startMillis));
        
    }
    
}
