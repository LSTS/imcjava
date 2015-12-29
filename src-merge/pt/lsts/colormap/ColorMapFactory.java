/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2016, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: ColorMapFactory.java 334 2013-01-02 11:20:47Z zepinto                 $:
 */
package pt.lsts.colormap;

import java.awt.Color;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Vector;

public class ColorMapFactory {

    public static Vector<String> colorMapNamesList = new Vector<String>();

    static {
        colorMapNamesList.add("Autumn");
        colorMapNamesList.add("Bone");
        colorMapNamesList.add("Cool");
        colorMapNamesList.add("Copper");
        colorMapNamesList.add("Gray");
        colorMapNamesList.add("Hot");
        colorMapNamesList.add("Jet");
        colorMapNamesList.add("Pink");
        colorMapNamesList.add("Spring");
        colorMapNamesList.add("Summer");
        colorMapNamesList.add("Winter");
        colorMapNamesList.add("AllColors");
        colorMapNamesList.add("RedGreenBlue");
        colorMapNamesList.add("BlueToRed");
        colorMapNamesList.add("White");
        colorMapNamesList.add("Rainbow");
        colorMapNamesList.add("Bronze");
        colorMapNamesList.add("StoreData");

        Collections.sort(colorMapNamesList);
    }

    private ColorMapFactory() {}

    public static ColorMap getColorMapByName(String name) {
        if ("autumn".equalsIgnoreCase(name))
            return createAutumnColorMap();
        else if ("bone".equalsIgnoreCase(name))
            return createBoneColorMap();
        else if ("cool".equalsIgnoreCase(name))
            return createCoolColorMap();
        else if ("copper".equalsIgnoreCase(name))
            return createCopperColorMap();
        else if ("gray".equalsIgnoreCase(name))
            return createGrayScaleColorMap();
        else if ("hot".equalsIgnoreCase(name))
            return createHotColorMap();
        else if ("jet".equalsIgnoreCase(name))
            return createJetColorMap();
        else if ("pink".equalsIgnoreCase(name))
            return createPinkColorMap();
        else if ("spring".equalsIgnoreCase(name))
            return createSpringColorMap();
        else if ("summer".equalsIgnoreCase(name))
            return createSummerColorMap();
        else if ("winter".equalsIgnoreCase(name))
            return createWinterColorMap();
        else if ("allColors".equalsIgnoreCase(name))
            return createAllColorsColorMap();
        else if ("redGreenBlue".equalsIgnoreCase(name))
            return createRedGreenBlueColorMap();
        else if ("blueToRed".equalsIgnoreCase(name))
            return createRedGreenBlueColorMap();
        else if ("white".equalsIgnoreCase(name))
            return createWhiteColorMap();
        else if ("greenRadar".equalsIgnoreCase(name))
            return createGreenRadarColorMap();
        else if ("rainbow".equalsIgnoreCase(name))
            return createRainbowColormap();
        else if ("redYellowGreen".equalsIgnoreCase(name))
            return createRedYellowGreenColorMap();
        else if ("bronze".equalsIgnoreCase(name))
            return createBronzeColormap();

        else {
            for (int i = 0; i < ColorMap.cmaps.length; i++) {
                if (ColorMap.cmaps[i].toString().equalsIgnoreCase(name))
                    return ColorMap.cmaps[i];
            }
        }
        return createJetColorMap();
    }

    private static ColorMap storeDataColormap = null;
    public static ColorMap createStoreDataColormap() {


        if (storeDataColormap == null)
            storeDataColormap = new InterpolationColorMap("Store Data Colormap", new double[] { 0.0, 0.333333,
                    0.666666, 1.0 }, new Color[] { new Color(0, 0, 0), new Color(255, 0, 0), new Color(255, 255, 0),
                    new Color(255, 255, 255) }
                    );
        return storeDataColormap;
    }
    
    
    private static ColorMap grayscale = null;
    public static ColorMap createGrayScaleColorMap() {


        if (grayscale == null)
            grayscale = new InterpolationColorMap("Gray scale",
                    new double[] {0.0, 1.0},
                    new Color[] {new Color(0,0,0),new Color(255,255,255)} 
                    );
        /*grayscale = new ColorMap() {
			public Color getColor(double value) {
				int rgbVal = (int) (value * 255);
				rgbVal = Math.min(rgbVal, 255);
				rgbVal = Math.max(rgbVal, 0);
				return new Color(rgbVal, rgbVal, rgbVal);
			}
			@Override
			public String toString() {
				return "Gray scale";
			}
		};*/
        return grayscale;
    }

    private static ColorMap white = null;
    public static ColorMap createWhiteColorMap() {
        if (white == null)
            white = new InterpolationColorMap("All white",
                    new double[] {0.0, 1.0},
                    new Color[] {Color.white, Color.white}
                    );			
        return white;
    }

    private static ColorMap hot = null;
    public static ColorMap createHotColorMap() {
        if (hot == null) {
            InputStreamReader isr = new InputStreamReader(ClassLoader.getSystemResourceAsStream("colormaps/hot.colormap"));
            try {
                hot = new TabulatedColorMap(isr);
            }
            catch (Exception e) {
                hot = new InterpolationColorMap("Hot",
                        new double[] {0.0, 0.3333333, 0.66666666, 1.0},
                        new Color[] {Color.BLACK, Color.RED, Color.YELLOW, Color.WHITE}
                        );
            }
        }
        return hot;
    }

    private static ColorMap allColors = null;
    public static ColorMap createAllColorsColorMap() {
        if (allColors == null)
            allColors = new InterpolationColorMap("All Colors",
                    new double[] {0.0, 0.3333333, 0.66666666, 1.0},
                    new Color[] {Color.BLACK, Color.BLUE, Color.YELLOW, Color.WHITE}
                    );
        return allColors;
    }


    private static ColorMap rgb = null;
    public static ColorMap createRedGreenBlueColorMap() {
        if (rgb == null)
            rgb = new InterpolationColorMap("RGB",
                    new double[] {0.0, 0.5, 1.0},
                    new Color[] {new Color(255,0,0),new Color(0,255,0),new Color(0,0,255)} 
                    );
        return rgb;
    }

    private static ColorMap bluered = null;
    public static ColorMap createBlueToRedColorMap() {
        if (bluered == null)
            bluered = new InterpolationColorMap("Blue to Red",
                    new double[] {0.0, 1.0},
                    new Color[] {Color.BLUE, Color.RED} 
                    );
        return bluered;
    }

    private static ColorMap autumn = null;
    public static ColorMap createAutumnColorMap() {
        if (autumn == null)
            autumn = new InterpolationColorMap("Autumn",
                    new double[] {0.0, 1.0},
                    new Color[] {Color.RED, Color.YELLOW} 
                    );
        return autumn;
    }

    private static ColorMap cool = null;
    public static ColorMap createCoolColorMap() {
        if (cool == null)
            cool = new InterpolationColorMap("Cool",
                    new double[] {0.0, 1.0},
                    new Color[] {Color.CYAN,Color.MAGENTA} 
                    );
        return cool;
    }
    private static ColorMap jet = null;
    public static ColorMap createJetColorMap() {
        if (jet == null) {
            InputStreamReader isr = new InputStreamReader(ClassLoader.getSystemResourceAsStream("colormaps/jet.colormap"));
            try {
                jet = new TabulatedColorMap(isr);
            }
            catch (Exception e) {
                jet = new InterpolationColorMap("Jet",
                        new double[] {0.0, 0.25, 0.5, 0.75, 1.0},
                        new Color[] {Color.blue ,Color.cyan, Color.yellow, Color.red, new Color(128,0,0)} 
                        );
            }
        }


        return jet;
    }
    private static ColorMap spring = null;
    public static ColorMap createSpringColorMap() {
        if (spring == null)
            spring =  new InterpolationColorMap("Spring",
                    new double[] {0.0, 1.0},
                    new Color[] {Color.magenta , Color.yellow} 
                    );
        return spring;
    }

    private static ColorMap bone = null;
    public static ColorMap createBoneColorMap() {
        if (bone == null)
            bone = new InterpolationColorMap("Bone",
                    new double[] {0.0, 0.375, 0.75, 1.0},
                    new Color[] {new Color(0,0,1), new Color(81,81,113), new Color(166,198,198), Color.white} 
                    );
        return bone;
    }
    private static ColorMap copper = null;
    public static ColorMap createCopperColorMap() {
        if (copper == null)
            copper = new InterpolationColorMap("Copper",
                    new double[] {0.0, 0.7869, 0.8125, 1.0},
                    new Color[] {Color.black, new Color(253,158,100), new Color(255,161,103), new Color(255,199,127)} 
                    );
        return copper;
    }

    private static ColorMap summer = null;
    public static ColorMap createSummerColorMap() {
        if (summer == null)
            summer = new InterpolationColorMap("Summer",
                    new double[] {0.0, 1.0},
                    new Color[] {new Color(0,128,102), new Color(255,255,102)} 
                    );
        return summer;
    }

    private static ColorMap winter = null;
    public static ColorMap createWinterColorMap() {
        if (winter == null)
            winter = new InterpolationColorMap("Winter",
                    new double[] {0.0, 1.0},
                    new Color[] {Color.blue, new Color(0,255,128)} 
                    );
        return winter;
    }

    private static ColorMap pink = null;
    public static ColorMap createPinkColorMap() {
        if (pink == null)
            pink = new InterpolationColorMap("Pink",
                    new double[] {0.0, 1/64.0, 2/64.0, 3/64.0, 24/64.0, 48/64.0, 1.0},
                    new Color[] {new Color(30,0,0),new Color(50,26,26),new Color(64,37,37),
                    new Color(75,45,45),new Color(194,126,126),new Color(232,232,180),
                    new Color(255,255,255)} 
                    );
        return pink;
    }

    private static ColorMap greenradar = null;
    public static ColorMap createGreenRadarColorMap() {
        if (greenradar == null)
            greenradar =  new InterpolationColorMap("GreenRadar",
                    new double[] {0.0, 1.0},
                    new Color[] {Color.black , Color.green} 
                    );
        return greenradar;
    }

    private static ColorMap redYellowGreen = null;
    public static ColorMap createRedYellowGreenColorMap() {
        if (redYellowGreen == null)
            redYellowGreen =  new InterpolationColorMap("RedYellowBlue",
                    new double[] {0.0, 0.5, 1.0},
                    new Color[] {Color.red, Color.yellow, Color.green} 
                    );
        return redYellowGreen;
    }

    private static ColorMap rainbow = null;
    public static ColorMap createRainbowColormap() {
        if (rainbow == null)
            rainbow =  new InterpolationColorMap("Rainbow",
                    new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 1.0},
                    new Color[] {Color.magenta.darker().darker().darker(), Color.magenta, Color.blue, Color.cyan, Color.green, Color.yellow, Color.orange, Color.red, Color.red.darker().darker().darker()} 
                    );
        return rainbow;
    }

    private static ColorMap bronze = null;
    public static ColorMap createBronzeColormap() {
        if (bronze == null) {
            InputStreamReader isr = new InputStreamReader(ClassLoader.getSystemResourceAsStream("colormaps/bronze.colormap"));
            try {
                bronze =  new TabulatedColorMap(isr);
                ((InterpolationColorMap)bronze).setName("Bronze");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bronze;
    }
    
    private static ColorMap sscanmap = null;
    public static ColorMap createSideScanColorMap() {
        if (sscanmap == null)
            sscanmap =  new InterpolationColorMap("Sidescan",
                    new double[] {0.0, 1.0},
                    new Color[] {new Color(0xFFFFFF00, true) , new Color(0xFFFFFFFF, true)} 
                    );
        return sscanmap;
    }
    
    public static ColorMap createInvertedColorMap(InterpolationColorMap original) {

        double[] inv = new double[original.getValues().length];
        Color[] colors = new Color[inv.length];

        int j = inv.length;
        for (int i = 0; i < inv.length; i++) {
            inv[i] = original.getValues()[i];
            colors[i] = original.getColor(Math.max(0.001, original.getValues()[--j]));

            //System.out.println(i+"->"+colors[i].toString());
        }



        //for (int i = 0; i < inv.length; i++)
        //	inv[i] = 1-inv[i];

        //for (int i = 0; i < inv.length; i++)
        //	colors[i] = original.getColor(1-inv[i]);

        return new InterpolationColorMap("Inverted "+original.toString(), inv, colors);
    }

}

