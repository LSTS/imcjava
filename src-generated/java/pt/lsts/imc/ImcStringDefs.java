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
 */
package pt.lsts.imc;

public class ImcStringDefs {

	public static final String IMC_SHA = "b51f3dd340b7a22d875f9420d7edd786d2b2e49c";
	public static final String IMC_BRANCH = "b51f3dd";
	public static final String IMC_COMMIT = "Paulo Dias (paulo.sousa.dias@gmail.com), 2025-06-16T14:36:33Z, https://github.com/LSTS/imc/commit/b51f3dd340b7a22d875f9420d7edd786d2b2e49c, Change current U and V to North ans East (because in meteorology U is East and V is North).";

	public static java.util.Map<String, Integer> IMC_ADDRESSES = new java.util.LinkedHashMap<String, Integer>();

	static {
		IMC_ADDRESSES.put("*", 65535);
		IMC_ADDRESSES.put("announce", 0);
		IMC_ADDRESSES.put("isurus", 1);
		IMC_ADDRESSES.put("nauv", 2);
		IMC_ADDRESSES.put("lauv-blue", 16);
		IMC_ADDRESSES.put("lauv-green", 18);
		IMC_ADDRESSES.put("lauv-seacon-1", 21);
		IMC_ADDRESSES.put("lauv-seacon-2", 22);
		IMC_ADDRESSES.put("lauv-seacon-3", 23);
		IMC_ADDRESSES.put("lauv-xtreme-2", 24);
		IMC_ADDRESSES.put("lauv-arpao", 25);
		IMC_ADDRESSES.put("lauv-noptilus-1", 26);
		IMC_ADDRESSES.put("lauv-noptilus-2", 27);
		IMC_ADDRESSES.put("lauv-noptilus-3", 28);
		IMC_ADDRESSES.put("lauv-lsts-1", 29);
		IMC_ADDRESSES.put("lauv-xplore-1", 30);
		IMC_ADDRESSES.put("lauv-xplore-2", 31);
		IMC_ADDRESSES.put("lauv-xplore-3", 32);
		IMC_ADDRESSES.put("lauv-xplore-4", 33);
		IMC_ADDRESSES.put("lauv-xplore-5", 34);
		IMC_ADDRESSES.put("lauv-nemo-1", 35);
		IMC_ADDRESSES.put("lauv-simulator-1", 209);
		IMC_ADDRESSES.put("apdr-1", 210);
		IMC_ADDRESSES.put("rov-ies", 1025);
		IMC_ADDRESSES.put("adamastor", 1026);
		IMC_ADDRESSES.put("squirtle", 1027);
		IMC_ADDRESSES.put("swordfish", 2049);
		IMC_ADDRESSES.put("caravela", 2050);
		IMC_ADDRESSES.put("otter", 2051);
		IMC_ADDRESSES.put("caravel", 2052);
		IMC_ADDRESSES.put("hermes", 2241);
		IMC_ADDRESSES.put("hermes-desired", 2242);
		IMC_ADDRESSES.put("hermes-target", 2243);
		IMC_ADDRESSES.put("tex-wg", 2244);
		IMC_ADDRESSES.put("pixhawk-testbed", 3072);
		IMC_ADDRESSES.put("lusitania", 3073);
		IMC_ADDRESSES.put("sedona", 3074);
		IMC_ADDRESSES.put("aero-01", 3075);
		IMC_ADDRESSES.put("x8-00", 3082);
		IMC_ADDRESSES.put("x8-01", 3083);
		IMC_ADDRESSES.put("x8-02", 3084);
		IMC_ADDRESSES.put("x8-03", 3085);
		IMC_ADDRESSES.put("x8-04", 3086);
		IMC_ADDRESSES.put("x8-05", 3087);
		IMC_ADDRESSES.put("x8-06", 3088);
		IMC_ADDRESSES.put("x8-07", 3089);
		IMC_ADDRESSES.put("x8-08", 3090);
		IMC_ADDRESSES.put("mariner-01", 3104);
		IMC_ADDRESSES.put("mariner-02", 3105);
		IMC_ADDRESSES.put("x2o-01", 3106);
		IMC_ADDRESSES.put("x2o-02", 3107);
		IMC_ADDRESSES.put("firefly", 3108);
		IMC_ADDRESSES.put("titan", 3109);
		IMC_ADDRESSES.put("vtol-01", 3114);
		IMC_ADDRESSES.put("vtol-02", 3115);
		IMC_ADDRESSES.put("vtol-03", 3116);
		IMC_ADDRESSES.put("hexa-00", 3120);
		IMC_ADDRESSES.put("alfa-02", 3122);
		IMC_ADDRESSES.put("alfa-03", 3123);
		IMC_ADDRESSES.put("alfa-04", 3124);
		IMC_ADDRESSES.put("alfa-05", 3125);
		IMC_ADDRESSES.put("alfa-06", 3126);
		IMC_ADDRESSES.put("alfa-07", 3127);
		IMC_ADDRESSES.put("alfa-08", 3128);
		IMC_ADDRESSES.put("alfa-09", 3129);
		IMC_ADDRESSES.put("piccolo-testbed", 3136);
		IMC_ADDRESSES.put("cularis-03", 3155);
		IMC_ADDRESSES.put("cularis-04", 3156);
		IMC_ADDRESSES.put("cularis-05", 3157);
		IMC_ADDRESSES.put("cularis-06", 3158);
		IMC_ADDRESSES.put("cularis-07", 3159);
		IMC_ADDRESSES.put("cularis-08", 3160);
		IMC_ADDRESSES.put("pilatus-03", 3187);
		IMC_ADDRESSES.put("pilatus-04", 3188);
		IMC_ADDRESSES.put("pilatus-05", 3189);
		IMC_ADDRESSES.put("pilatus-06", 3190);
		IMC_ADDRESSES.put("extended", 3216);
		IMC_ADDRESSES.put("form-leader-01", 3313);
		IMC_ADDRESSES.put("form-leader-02", 3314);
		IMC_ADDRESSES.put("form-leader-03", 3315);
		IMC_ADDRESSES.put("form-leader-04", 3316);
		IMC_ADDRESSES.put("form-leader-05", 3317);
		IMC_ADDRESSES.put("form-leader-06", 3318);
		IMC_ADDRESSES.put("turbot", 8192);
		IMC_ADDRESSES.put("sparus2", 8193);
		IMC_ADDRESSES.put("eqe", 8194);
		IMC_ADDRESSES.put("lauv-harald", 8205);
		IMC_ADDRESSES.put("lrauv-sim", 8960);
		IMC_ADDRESSES.put("buv-petinga-1", 9216);
		IMC_ADDRESSES.put("ntnu-hexa-testbed", 11264);
		IMC_ADDRESSES.put("ntnu-hexa-001", 11265);
		IMC_ADDRESSES.put("ntnu-hexa-002", 11266);
		IMC_ADDRESSES.put("ntnu-hexa-003", 11267);
		IMC_ADDRESSES.put("ntnu-hexa-004", 11268);
		IMC_ADDRESSES.put("ntnu-octo-001", 11281);
		IMC_ADDRESSES.put("ntnu-octo-002", 11282);
		IMC_ADDRESSES.put("ntnu-octo-003", 11283);
		IMC_ADDRESSES.put("ntnu-x8-001", 11297);
		IMC_ADDRESSES.put("ntnu-x8-002", 11298);
		IMC_ADDRESSES.put("ntnu-x8-003", 11299);
		IMC_ADDRESSES.put("ntnu-x8-004", 11300);
		IMC_ADDRESSES.put("ntnu-x8-005", 11301);
		IMC_ADDRESSES.put("ntnu-x8-006", 11302);
		IMC_ADDRESSES.put("ntnu-x8-007", 11303);
		IMC_ADDRESSES.put("ntnu-x8-008", 11304);
		IMC_ADDRESSES.put("ntnu-x8-009", 11305);
		IMC_ADDRESSES.put("ntnu-x8-010", 11312);
		IMC_ADDRESSES.put("ntnu-penguin-001", 11329);
		IMC_ADDRESSES.put("ntnu-penguin-002", 11330);
		IMC_ADDRESSES.put("ntnu-penguin-003", 11331);
		IMC_ADDRESSES.put("ntnu-penguin-004", 11332);
		IMC_ADDRESSES.put("ntnu-bixler-001", 11338);
		IMC_ADDRESSES.put("ntnu-bixler-002", 11339);
		IMC_ADDRESSES.put("ntnu-bixler-003", 11340);
		IMC_ADDRESSES.put("ntnu-cruisermini-001", 11345);
		IMC_ADDRESSES.put("ntnu-cruisermini-002", 11346);
		IMC_ADDRESSES.put("ntnu-cruisermini-003", 11347);
		IMC_ADDRESSES.put("ntnu-cruisermini-004", 11348);
		IMC_ADDRESSES.put("ntnu-cruisermini-005", 11349);
		IMC_ADDRESSES.put("ntnu-cruisermini-006", 11350);
		IMC_ADDRESSES.put("wg-sv3-127", 10241);
		IMC_ADDRESSES.put("ccu-lsts-1-1", 16641);
		IMC_ADDRESSES.put("ccu-lsts-1-14", 16654);
		IMC_ADDRESSES.put("ccu-lsts-1-20", 16660);
		IMC_ADDRESSES.put("ccu-zp-1-106", 16746);
		IMC_ADDRESSES.put("ccu-pitvant-laptop-01-1-10", 16650);
		IMC_ADDRESSES.put("ccu-lsts-0-108", 16492);
		IMC_ADDRESSES.put("europtus", 24575);
		IMC_ADDRESSES.put("doam", 24576);
		IMC_ADDRESSES.put("lauv-seacon-1-aux", 24578);
		IMC_ADDRESSES.put("lauv-noptilus-3-aux", 24579);
		IMC_ADDRESSES.put("lauv-xtreme-2-aux", 24580);
		IMC_ADDRESSES.put("caravela-aux", 24581);
		IMC_ADDRESSES.put("lauv-xtreme-2-cpu-cam", 24582);
		IMC_ADDRESSES.put("x8-06-aux", 24583);
		IMC_ADDRESSES.put("lauv-noptilus-2-cpu-cam", 24584);
		IMC_ADDRESSES.put("lauv-noptilus-3-cpu-cam", 24585);
		IMC_ADDRESSES.put("lauv-seacon-3-aux", 24592);
		IMC_ADDRESSES.put("lauv-seacon-3-cpu-cam", 24593);
		IMC_ADDRESSES.put("lauv-seacon-2-aux", 24594);
		IMC_ADDRESSES.put("lauv-seacon-2-cpu-cam", 24595);
		IMC_ADDRESSES.put("otter-aux", 24596);
		IMC_ADDRESSES.put("star", 32768);
		IMC_ADDRESSES.put("benthos-mgateway", 32784);
		IMC_ADDRESSES.put("manta-1", 32786);
		IMC_ADDRESSES.put("manta-2", 32787);
		IMC_ADDRESSES.put("manta-3", 32788);
		IMC_ADDRESSES.put("manta-4", 32789);
		IMC_ADDRESSES.put("manta-5", 32790);
		IMC_ADDRESSES.put("manta-dmsmw-01", 32794);
		IMC_ADDRESSES.put("manta-11", 32796);
		IMC_ADDRESSES.put("manta-12", 32797);
		IMC_ADDRESSES.put("manta-15", 32800);
		IMC_ADDRESSES.put("manta-16", 32801);
		IMC_ADDRESSES.put("manta-21", 32806);
		IMC_ADDRESSES.put("manta-dmsmw-02", 32807);
		IMC_ADDRESSES.put("manta-dmsmw-03", 32808);
		IMC_ADDRESSES.put("manta-rugged", 32809);
		IMC_ADDRESSES.put("manta-rugged-2", 32810);
		IMC_ADDRESSES.put("manta-uk-01", 32811);
		IMC_ADDRESSES.put("manta-uk-02", 32812);
		IMC_ADDRESSES.put("nest-1", 32816);
		IMC_ADDRESSES.put("piccolo-gs1", 32832);
		IMC_ADDRESSES.put("piccolo-gs2", 32833);
		IMC_ADDRESSES.put("piccolo-gs3", 32834);
		IMC_ADDRESSES.put("ais-1", 32880);
		IMC_ADDRESSES.put("ais-2", 32881);
		IMC_ADDRESSES.put("bridgebox-1", 32897);
		IMC_ADDRESSES.put("bridgebox-2", 32898);
		IMC_ADDRESSES.put("spot-01", 33793);
		IMC_ADDRESSES.put("spot-02", 33794);
		IMC_ADDRESSES.put("spot-03", 33795);
		IMC_ADDRESSES.put("spot-04", 33796);
		IMC_ADDRESSES.put("spot-05", 33797);
		IMC_ADDRESSES.put("spot-06", 33798);
		IMC_ADDRESSES.put("spot-07", 33799);
		IMC_ADDRESSES.put("spot-08", 33800);
		IMC_ADDRESSES.put("spot-09", 33801);
		IMC_ADDRESSES.put("spot-10", 33802);
		IMC_ADDRESSES.put("spot-11", 33803);
		IMC_ADDRESSES.put("spot-12", 33804);
		IMC_ADDRESSES.put("spot-13", 33805);
		IMC_ADDRESSES.put("spot-14", 33806);
		IMC_ADDRESSES.put("spot-15", 33807);
		IMC_ADDRESSES.put("spot-16", 33808);
		IMC_ADDRESSES.put("spot-17", 33809);
		IMC_ADDRESSES.put("spot-18", 33810);
		IMC_ADDRESSES.put("spot-19", 33811);
		IMC_ADDRESSES.put("spot-20", 33812);
		IMC_ADDRESSES.put("spot-21", 33813);
		IMC_ADDRESSES.put("spot-22", 33814);
		IMC_ADDRESSES.put("spot-23", 33815);
		IMC_ADDRESSES.put("spot-24", 33816);
		IMC_ADDRESSES.put("spot-25", 33817);
		IMC_ADDRESSES.put("spot-26", 33818);
		IMC_ADDRESSES.put("tracer", 33819);
		IMC_ADDRESSES.put("carson", 33957);
		IMC_ADDRESSES.put("paragon", 33958);
		IMC_ADDRESSES.put("dorado", 33959);
		IMC_ADDRESSES.put("esp-mack", 33960);
		IMC_ADDRESSES.put("esp-bruce", 33961);
		IMC_ADDRESSES.put("wavy-0", 34048);
		IMC_ADDRESSES.put("wavy-1", 34049);
		IMC_ADDRESSES.put("wavy-2", 34050);
		IMC_ADDRESSES.put("ntnu-autonaut", 34819);
		IMC_ADDRESSES.put("ntnu-autonaut-L3", 34820);
		IMC_ADDRESSES.put("ntnu-nest-01", 36865);
		IMC_ADDRESSES.put("ntnu-nest-02", 36866);
		IMC_ADDRESSES.put("manta-sabuvis", 38145);
		IMC_ADDRESSES.put("broadcast", 65520);
		IMC_ADDRESSES.put("null", 65535);

		IMC_ADDRESSES = java.util.Collections.unmodifiableMap(IMC_ADDRESSES);
	}

	public static String getDefinitions() {

		java.io.InputStream xmlStream = ImcStringDefs.class.getResourceAsStream("/xml/IMC.xml");
		java.io.InputStreamReader isreader = new java.io.InputStreamReader(xmlStream);
		java.io.BufferedReader reader = new java.io.BufferedReader(isreader);
		java.lang.StringBuilder builder = new java.lang.StringBuilder();
		String line = null;

		try {
			while ((line = reader.readLine()) != null)
				builder.append(line+"\n");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
			return null;
		}

		return builder.toString();
	}
}
