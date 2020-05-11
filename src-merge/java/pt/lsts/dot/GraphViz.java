/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2020, Laboratório de Sistemas e Tecnologia Subaquática
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
 */
package pt.lsts.dot;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

/**
* <dl>
* <dt>Purpose: GraphViz Java API
* <dd>
*
* <dt>Description:
* <dd> With this Java class you can simply call dot
*      from your Java programs
* <dt>Example usage:
* <dd>
* <pre>
*    GraphViz gv = new GraphViz();
*    gv.addln(gv.start_graph());
*    gv.addln("A -> B;");
*    gv.addln("A -> C;");
*    gv.addln(gv.end_graph());
*    System.out.println(gv.getDotSource());
*
*    String type = "gif";
*    File out = new File("out." + type);   // out.gif in this example
*    gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
* </pre>
* </dd>
*
* </dl>
* @version v0.5, 2012/06/06 (June) -- Automatic detection of Operating System (zepinto@gmail.com)
* @version v0.4, 2011/02/05 (February) -- Patch of Keheliya Gallaba is added. Now you
* can specify the type of the output file: gif, dot, fig, pdf, ps, svg, png, etc.
* @version v0.3, 2010/11/29 (November) -- Windows support + ability 
* to read the graph from a text file
* @version v0.2, 2010/07/22 (July) -- bug fix
* @version v0.1, 2003/12/04 (December) -- first release
* @author  Laszlo Szathmary (<a href="jabba.laci@gmail.com">jabba.laci@gmail.com</a>)
*/
public class GraphViz
{
/**
* The dir. where temporary files will be created.
*/
private static String TEMP_DIR = "/tmp"; // Linux, MacOx

/**
* Where is dot located? It will be called externally.
*/
private static String DOT;
static {
    System.out.println(System.getProperty("os.name"));
    if (System.getProperty("os.name").contains("Linux"))
        DOT = "/usr/bin/dot"; // Linux
    else if (System.getProperty("os.name").contains("Windows")) {
        DOT = "c:/Program Files/Graphviz2.26.3/bin/dot.exe"; // Windows
        TEMP_DIR = "c:/temp";
    }
    else
        DOT = "/opt/local/bin/dot"; // MacOx
}

/**
* The source of the graph written in dot language.
*/
private StringBuilder graph = new StringBuilder();

/**
* Constructor: creates a new GraphViz object that will contain
* a graph.
*/
public GraphViz() {
}

/**
* Returns the graph's source description in dot language.
* @return Source of the graph in dot language.
*/
public String getDotSource() {
 return graph.toString();
}

/**
* Adds a string to the graph's source (without newline).
*/
public void add(String line) {
 graph.append(line);
}

/**
* Adds a string to the graph's source (with newline).
*/
public void addln(String line) {
 graph.append(line + "\n");
}

/**
* Adds a newline to the graph's source.
*/
public void addln() {
 graph.append('\n');
}

/**
 * Generate an Image from this graph
 * @return A BufferedImage generated by converting the dot into PNG and loading the result
 */
public BufferedImage generateImage() {
    byte[] png = getGraph(getDotSource(), "png");
    try {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(png));
        return img;
    }
    catch (Exception e) {
        e.printStackTrace();
        return null;
    }    
}

public JFrame showGraph(String frameTitle) {
    JLabel lbl = new JLabel(new ImageIcon(generateImage()));
    JScrollPane scroll = new JScrollPane(lbl);
    
    JFrame frame = new JFrame(frameTitle);
    
    frame.setSize(500, 500);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setContentPane(scroll);
    frame.setVisible(true);
    
    return frame;
}

/**
* Returns the graph as an image in binary format.
* @param dot_source Source of the graph to be drawn.
* @param type Type of the output image to be produced, e.g.: gif, dot, fig, pdf, ps, svg, png.
* @return A byte array containing the image of the graph.
*/
public byte[] getGraph(String dot_source, String type)
{
 File dot;
 byte[] img_stream = null;

 try {
    dot = writeDotSourceToFile(dot_source);
    if (dot != null)
    {
       img_stream = get_img_stream(dot, type);
       if (dot.delete() == false) 
          System.err.println("Warning: " + dot.getAbsolutePath() + " could not be deleted!");
       return img_stream;
    }
    return null;
 } catch (java.io.IOException ioe) { return null; }
}

/**
* Writes the graph's image in a file.
* @param img   A byte array containing the image of the graph.
* @param file  Name of the file to where we want to write.
* @return Success: 1, Failure: -1
*/
public int writeGraphToFile(byte[] img, String file)
{
 File to = new File(file);
 return writeGraphToFile(img, to);
}

/**
* Writes the graph's image in a file.
* @param img   A byte array containing the image of the graph.
* @param to    A File object to where we want to write.
* @return Success: 1, Failure: -1
*/
public int writeGraphToFile(byte[] img, File to)
{
 try {
    FileOutputStream fos = new FileOutputStream(to);
    fos.write(img);
    fos.close();
 } catch (java.io.IOException ioe) { return -1; }
 return 1;
}

/**
* It will call the external dot program, and return the image in
* binary format.
* @param dot Source of the graph (in dot language).
* @param type Type of the output image to be produced, e.g.: gif, dot, fig, pdf, ps, svg, png.
* @return The image of the graph in .gif format.
*/
private byte[] get_img_stream(File dot, String type)
{
 File img;
 byte[] img_stream = null;

 try {
    img = File.createTempFile("graph_", "."+type, new File(GraphViz.TEMP_DIR));
    Runtime rt = Runtime.getRuntime();
    
    // patch by Mike Chenault
    String[] args = {DOT, "-T"+type, dot.getAbsolutePath(), "-o", img.getAbsolutePath()};
    Process p = rt.exec(args);
    
    p.waitFor();

    FileInputStream in = new FileInputStream(img.getAbsolutePath());
    img_stream = new byte[in.available()];
    in.read(img_stream);
    // Close it if we need to
    if( in != null ) in.close();

    if (img.delete() == false) 
       System.err.println("Warning: " + img.getAbsolutePath() + " could not be deleted!");
 }
 catch (java.io.IOException ioe) {
    System.err.println("Error:    in I/O processing of tempfile in dir " + GraphViz.TEMP_DIR+"\n");
    System.err.println("       or in calling external command");
    ioe.printStackTrace();
 }
 catch (java.lang.InterruptedException ie) {
    System.err.println("Error: the execution of the external program was interrupted");
    ie.printStackTrace();
 }

 return img_stream;
}

/**
* Writes the source of the graph in a file, and returns the written file
* as a File object.
* @param str Source of the graph (in dot language).
* @return The file (as a File object) that contains the source of the graph.
*/
private File writeDotSourceToFile(String str) throws java.io.IOException
{
 File temp;
 try {
    temp = File.createTempFile("graph_", ".dot.tmp", new File(GraphViz.TEMP_DIR));
    FileWriter fout = new FileWriter(temp);
    fout.write(str);
    fout.close();
 }
 catch (Exception e) {
    System.err.println("Error: I/O error while writing the dot source to temp file!");
    return null;
 }
 return temp;
}

/**
* Returns a string that is used to start a graph.
* @return A string to open a graph.
*/
public String start_graph() {
 return "digraph G {\nconcentrate=true";
}

/**
* Returns a string that is used to end a graph.
* @return A string to close a graph.
*/
public String end_graph() {
 return "}";
}

/**
* Read a DOT graph from a text file.
* 
* @param input Input text file containing the DOT graph
* source.
*/
public void readSource(String input)
{
  StringBuilder sb = new StringBuilder();
  
  BufferedReader br = null;
  try
  {
      FileInputStream fis = new FileInputStream(input);
      DataInputStream dis = new DataInputStream(fis);
      br = new BufferedReader(new InputStreamReader(dis));
      String line;
      while ((line = br.readLine()) != null) {
          sb.append(line);
      }
      dis.close();
  }
  catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
  }
  finally {
      if (br != null)
        try {
            br.close();
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
  }
  
  this.graph = sb;
}

} // end of class GraphViz

