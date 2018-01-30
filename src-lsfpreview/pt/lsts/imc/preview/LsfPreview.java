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
 * $Id:: LsfInspectorPanel.java 333 2013-01-02 11:11:44Z zepinto               $:
 */
package pt.lsts.imc.preview;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import javax.imageio.ImageIO;

import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.LogBookEntry;
import pt.lsts.imc.lsf.LsfIndex;

public class LsfPreview {

    Vector<EstimatedState> states = new Vector<EstimatedState>();
    Vector<LogBookEntry> logbook = new Vector<LogBookEntry>();

    double lastTime = 0;
    double timeStep = 1;

    public LsfPreview(LsfIndex index) {
        for (long i = index.getFirstMessageOfType("EstimatedState"); i != -1; i = index.getNextMessageOfType(
                "EstimatedState", i)) {
            if (index.timeOf(i) - lastTime >= timeStep) {
                try {
                    EstimatedState state = new EstimatedState();
                    state.copyFrom(index.getMessage(i));
                    states.add(state);
                    lastTime = index.timeOf(i);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (long i = index.getFirstMessageOfType("LogBookEntry"); i != -1; i = index.getNextMessageOfType(
                "LogBookEntry", i)) {
            try {
                LogBookEntry entry = new LogBookEntry();
                entry.copyFrom(index.getMessage(i));
                logbook.add(entry);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public BufferedImage generatePathImage(int width, int height) {
        return PathPreviewPanel.generateImage(states, width, height);
    }

    public static void generatePreviewRecursive(String folder) throws Exception {
        File f = new File(folder);
        if (new File(folder,"Data.lsf").canRead())
            generatePreview(folder);

        for (File child : f.listFiles()) {

            if (child.isDirectory()) {
                System.out.println("recursing to "+child.getCanonicalPath());
                generatePreviewRecursive(child.getCanonicalPath());
            }
        }
    }
    
    public static void generatePreview(String folder) throws Exception {
        LsfIndex index = null;
        if (new File(folder, "Data.lsf").canRead()) {
            if (new File(folder, "IMC.xml").canRead())
                index = new LsfIndex(new File(folder, "Data.lsf"), new IMCDefinition(new FileInputStream(new File(
                        folder, "IMC.xml"))));

            else {
                index = new LsfIndex(new File(folder, "Data.lsf"), IMCDefinition.getInstance());
            }

            LsfPreview preview = new LsfPreview(index);
            BufferedImage image = preview.generatePathImage(500, 500);
            File output = new File(folder, "preview.png");
            ImageIO.write(image, "PNG", output);
            System.out.println("Generated "+output.getCanonicalPath());
        }
        else {
            System.err.println("Folder '"+folder+"' does not contain a Data.lsf file.");
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0)
            generatePreviewRecursive(new File(".").getAbsolutePath());
        else
            generatePreviewRecursive(args[0]);
    }
}