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
 * $Id:: LSF2LLFGui.java 333 2013-01-02 11:11:44Z zepinto                      $:
 */
package pt.lsts.imc.llf;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import pt.lsts.imc.IMCDefinition;

public class LSF2LLFGui {
	public static void main(String[] args) throws Exception {
		
		final JFrame frame;
		try {
			frame = new JFrame();
		} catch (HeadlessException e1) {
		    e1.printStackTrace();
			LSF2LLF.main(args);
			return;
		}
		frame.setIconImage(new ImageIcon(ClassLoader.getSystemClassLoader()
				.getResource("images/imc.png")).getImage());
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (Exception e) {
		    e.printStackTrace();
		}
		final long startTime = System.currentTimeMillis();
		final ProgressMonitor monitor = new ProgressMonitor(frame, "Converting lsf file", "Loading IMC definitions", 0, 100);
		final LSF2LLF converter = new LSF2LLF();
		converter.addListener(new IConverterListener() {
			public void update(long filesize, long curPosition, long messageCount) {
				monitor.setNote("Converted " + messageCount + " msgs, "
						+ parseToEngineeringRadix2Notation(curPosition, 1)
						+ "B/" + parseToEngineeringRadix2Notation(filesize, 1)
						+ "B");
				monitor.setProgress((int)(curPosition*100/filesize));
				if (monitor.isCanceled()) {
					converter.abortConversion();
				}
			}
		});
		String filename = ".";
		if (args.length == 1) {
			if ("-h".equalsIgnoreCase(args[0]) || "--help".equalsIgnoreCase(args[0])) {
				JOptionPane.showMessageDialog(frame, LSF2LLF.getOptions()
						.replaceFirst("\\(c\\)", "\u00A9"), "LSF2LLF",
						JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
			filename = args[0];
		}
		else {
		    JFileChooser chooser = new JFileChooser(new File("."));
		    chooser.setFileFilter(new FileFilter() {
                
                @Override
                public String getDescription() {
                    return "LSF folders";
                }
                
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory())
                        return true;
                    if (f.getName().endsWith(".lsf"))
                        return true;
                    if (f.getName().endsWith(".lsf.gz"))
                        return true;
                    return false;
                }
            });
		    int opt = chooser.showOpenDialog(frame);
		    if (opt == JFileChooser.APPROVE_OPTION) {
		        filename = chooser.getSelectedFile().getAbsolutePath();
		    }
		    else {
		        System.exit(0);
		    }
		}
		final String fname = filename;
		System.out.println("Converting "+fname);
		
		new Thread() {
			public void run() {
				try {
					File data_lsf = new File(fname).getAbsoluteFile();
//					data_lsf = LSF2LLF.testIfZipAndUnzipIt(data_lsf, monitor);
					if (new File(data_lsf.getParentFile(), "IMC.xml").canRead()) {
						//System.out.println("Loading IMC definitions in "+new File(data_lsf.getParentFile(), "IMC.xml").getCanonicalPath());
						converter.convert(new FileInputStream(new File(data_lsf.getParentFile(), "IMC.xml")), data_lsf);
					}
					else
						converter.convert(IMCDefinition.getInstance(), data_lsf);
					JOptionPane.showMessageDialog(frame, "Generation took "+((float)(System.currentTimeMillis()-startTime)/1000)+" seconds", "LSF2LLF", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(frame, "Error converting file: "+e.getMessage(), "LSF2LLF", JOptionPane.ERROR_MESSAGE);
					System.exit(-1);
				}
			};
		}.start();
	}

	
	/**
	 * <p>
	 * IEEE 1541 recommends:
	 * </p>
	 * <ul>
	 * <li>a set of units to refer to quantities used in digital electronics and
	 * computing:
	 * <ul>
	 * <li><i>bit</i> (symbol 'b'), a binary digit;</li>
	 * <li><i>byte</i> (symbol 'B'), a set of adjacent bits (usually, but not
	 * necessarily, eight) operated on as a group;</li>
	 * <li><i>octet</i> (symbol 'o'), a group of eight bits;</li>
	 * 
	 * </ul>
	 * </li>
	 * <li>a set of prefixes to indicate binary multiples of the aforesaid
	 * units:
	 * <ul>
	 * <li><i>kibi</i> (symbol 'Ki'), 2<sup>10</sup> = <span
	 * style="white-space: nowrap;">1<span
	 * style="margin-left: 0.25em;">024</span></span>;</li>
	 * <li><i>mebi</i> (symbol 'Mi'), 2<sup>20</sup> = <span
	 * style="white-space: nowrap;">1<span
	 * style="margin-left: 0.25em;">048</span><span
	 * style="margin-left: 0.25em;">576</span></span>;</li>
	 * 
	 * <li><i>gibi</i> (symbol 'Gi'), 2<sup>30</sup> = <span
	 * style="white-space: nowrap;">1<span
	 * style="margin-left: 0.25em;">073</span><span
	 * style="margin-left: 0.25em;">741</span><span
	 * style="margin-left: 0.25em;">824</span></span>;</li>
	 * <li><i>tebi</i> (symbol 'Ti'), 2<sup>40</sup> = <span
	 * style="white-space: nowrap;">1<span
	 * style="margin-left: 0.25em;">099</span><span
	 * style="margin-left: 0.25em;">511</span><span
	 * style="margin-left: 0.25em;">627</span><span
	 * style="margin-left: 0.25em;">776</span></span>;</li>
	 * 
	 * <li><i>pebi</i> (symbol 'Pi'), 2<sup>50</sup> = <span
	 * style="white-space: nowrap;">1<span
	 * style="margin-left: 0.25em;">125</span><span
	 * style="margin-left: 0.25em;">899</span><span
	 * style="margin-left: 0.25em;">906</span><span
	 * style="margin-left: 0.25em;">842</span><span
	 * style="margin-left: 0.25em;">624</span></span>;</li>
	 * <li><i>exbi</i> (symbol 'Ei'), 2<sup>60</sup> = <span
	 * style="white-space: nowrap;">1<span
	 * style="margin-left: 0.25em;">152</span><span
	 * style="margin-left: 0.25em;">921</span><span
	 * style="margin-left: 0.25em;">504</span><span
	 * style="margin-left: 0.25em;">606</span><span
	 * style="margin-left: 0.25em;">846</span><span
	 * style="margin-left: 0.25em;">976</span></span>;</li>
	 * <li><i>zebi</i> (symbol 'Zi'), 2<sup>70</sup> = <span
	 * style="white-space: nowrap;">11<span
	 * style="margin-left: 0.25em;">805</span><span
	 * style="margin-left: 0.25em;">916</span><span
	 * style="margin-left: 0.25em;">207</span><span
	 * style="margin-left: 0.25em;">174</span><span
	 * style="margin-left: 0.25em;">113</span><span
	 * style="margin-left: 0.25em;">034</span><span
	 * style="margin-left: 0.25em;">241</span></span>;</li>
	 * <li><i>yobi</i> (symbol 'Yi'), 2<sup>80</sup> = <span
	 * style="white-space: nowrap;">1<span
	 * style="margin-left: 0.25em;">208</span><span
	 * style="margin-left: 0.25em;">925</span><span
	 * style="margin-left: 0.25em;">819</span><span
	 * style="margin-left: 0.25em;">614</span><span
	 * style="margin-left: 0.25em;">629</span><span
	 * style="margin-left: 0.25em;">174</span><span
	 * style="margin-left: 0.25em;">706</span><span
	 * style="margin-left: 0.25em;">176</span></span>;</li>
	 * 
	 * </ul>
	 * </li>
	 * <li>that the first part of the binary prefix is pronounced as the
	 * analogous SI prefix, and the second part is pronounced as <i>bee</i>;</li>
	 * <li>that SI prefixes are not used to indicate binary multiples.</li>
	 * </ul>
	 * <p>
	 * The <i>bi</i> part of the prefix comes from the word binary, so for
	 * example, kibibyte means a kilobinary byte, that is 1024 bytes.
	 * </p>
	 * <p>
	 * Note the capital 'K' for the <i>kibi-</i> symbol: while the symbol for
	 * the analogous SI prefix <i>kilo-</i> is a small 'k', a capital 'K' has
	 * been selected for consistency with the other prefixes and with the
	 * widespread use of the misspelled SI prefix (as in 'KB').
	 * </p>
	 * 
	 * <p>
	 * IEEE 1541 is closely related to Amendment 2 to IEC International Standard
	 * <a href="/wiki/IEC_60027" title="IEC 60027">IEC 60027</a>-2, except the
	 * latter uses 'bit' as the symbol for bit, as opposed to 'b'.
	 * </p>
	 * <p>
	 * Today the harmonized <a href="/wiki/ISO" title="ISO"
	 * class="mw-redirect">ISO</a>/<a
	 * href="/wiki/International_Electrotechnical_Commission"
	 * title="International Electrotechnical Commission">IEC</a> <a
	 * href="/wiki/ISO/IEC_80000" title="ISO/IEC 80000">IEC 80000-13:2008 -
	 * Quantities and units -- Part 13: Information science and technology</a>
	 * standard cancels and replaces subclauses 3.8 and 3.9 of IEC 60027-2:2005
	 * (those related to Information theory and Prefixes for binary multiples).
	 * </p>
	 * 
	 * @param val
	 * @param decimalHouses
	 * @return
	 */
	public static String parseToEngineeringRadix2Notation(double val,
			int decimalHouses) {
		int mulTmp = 0;
		int signal = 1;
		if (val < 0)
			signal = -1;
		else
			signal = 1;
		double valTmp = val < 0 ? val * -1 : val;
		if (val >= 1024) {
			do {
				mulTmp++;
				valTmp = valTmp / 1024.0;
			} while (valTmp >= 1024 || mulTmp == 8);
		} 
		else if (val == 0) {
			// Nothing to do
		}
		else if (val < 1.0) {
			do {
				mulTmp--;
				valTmp = valTmp * 1024.0;
			} while (valTmp < 1.0 || mulTmp == -8);

		}

		double vl = valTmp;
		vl = round(vl, decimalHouses);
		int mul = mulTmp * 10;
		String mulStr = "";
		switch (mul) {
		case 80:
			mulStr = "Yi";
			break;
		case 70:
			mulStr = "Zi";
			break;
		case 60:
			mulStr = "Ei";
			break;
		case 50:
			mulStr = "Pi";
			break;
		case 40:
			mulStr = "Ti";
			break;
		case 30:
			mulStr = "Gi";
			break;
		case 20:
			mulStr = "Mi";
			break;
		case 10:
			mulStr = "Ki";
			break;
		case -10:
			mulStr = "mi";
			break;
		case -20:
			mulStr = "ui";
			mulStr = "\u00B5i";
			break;
		case -30:
			mulStr = "ni";
			break;
		case -40:
			mulStr = "pi";
			break;
		case -50:
			mulStr = "fi";
			break;
		case -60:
			mulStr = "ai";
			break;
		case -70:
			mulStr = "zi";
			break;
		case -80:
			mulStr = "yi";
			break;
		default:
			mulStr = "";
			break;
		}
		if ("".equalsIgnoreCase(mulStr) && vl < 1024) {
			if (vl == (long) vl)
				return (signal > 0 ? "" : "-") + ((long) vl) + mulStr;
		}
		return (signal > 0 ? "" : "-") + vl + mulStr;
	}

	
	public static double round(double val, int decimalHouses) {
		double base = Math.pow(10d, decimalHouses);
		double result = Math.round(val * base) / base;
		return result;
	}

}
