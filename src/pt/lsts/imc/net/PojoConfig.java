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
 */
package pt.lsts.imc.net;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import pt.lsts.imc.state.Parameter;

/**
 * @author zp
 *
 */
public class PojoConfig {

	@Parameter
	double test = 50.1;
	
	@Parameter
	double test3 = 50.4;
	
	@Parameter
	String[] tests = {"a", "b"};
	
	private static final String[] validTypes = new String[] { "boolean", "Boolean", "byte", "Byte", "short", "Short",
			"int", "Integer", "long", "Long", "float", "Float", "double", "Double", "String", "String[]", "byte[]" };	
	
	public static void cliParams(Object pojo, String[] arguments) throws Exception {
		setProperties(pojo, asProperties(arguments));
	}
	
	private static Properties asProperties(String[] arguments) throws Exception {
		Properties p = new Properties();
		for (String arg : arguments) {
			if (!arg.startsWith("--"))
				throw new Exception("Unrecognized argument: "+arg);

			arg = arg.substring(2);
			if (!arg.contains("=")) {
				p.put(arg, "true");
			}
			else {
				p.put(arg.substring(0, arg.indexOf("=")), arg.substring(arg.indexOf("=")+1));
			}
		}
		return p;
	}
	
	public static <T> T create(Class<T> pojoClass, Properties props) throws Exception {
		T pojo = pojoClass.newInstance();
		setProperties(pojo, props);
		return pojo;
	}
	
	public static <T> T create(Class<T> pojoClass, String[] args) throws Exception {
		T pojo = pojoClass.newInstance();
		setProperties(pojo, asProperties(args));
		return pojo;
	}
	
	public static void setProperties(Object pojo, Properties props) throws Exception {
		validate(pojo);
		ArrayList<Field> fields = loadFields(pojo);
		
		for (Field f : fields) {
			String value = props.getProperty(f.getName());
			try {
				if (value != null)
					setValue(pojo, value, f);
			}
			catch (Exception e) {
				throw new Exception("Value for '"+f.getName()+"' ("+f.getType().getSimpleName()+") is invalid: "+value); 
			}
		}		
	}
	
	public static Properties getProperties(Object pojo) throws Exception {
		validate(pojo);
		ArrayList<Field> fields = loadFields(pojo);
		Properties props = new Properties();
		for (Field f : fields) {
			String key = f.getName();
			Object value = f.get(pojo);
			
			if (value == null)
				continue;
			
			if (value instanceof byte[]) {
				value = DatatypeConverter.printHexBinary((byte[])value);
			}
			else if (value instanceof String[]) {
				// value = String.join(", ", (String[])value); Not Java 7!
			    StringBuilder tmpValue = new StringBuilder();
			    for (String field : (String[]) value) {
                    if (tmpValue.length() != 0)
                        tmpValue.append(", ");
                    
                    tmpValue.append(field);
                }
			}
			
			props.setProperty(key, String.valueOf(value));
		}
		return props;
	}
	
	private static void validate(Object pojo) throws Exception {
		ArrayList<Field> fields = loadFields(pojo);
		List<String> valid = Arrays.asList(validTypes);

		for (Field f : fields) {
			if (!valid.contains(f.getType().getSimpleName()))
				throw new Exception(
						"Type of parameter '" + f.getName() + "' (" + f.getType().getSimpleName() + ") is not valid.");
		}
	}
	
	private static void setValue(Object pojo, String value, Field f) throws Exception {
		switch (f.getType().getSimpleName()) {
		case "Double":
		case "double":
			f.setDouble(pojo, Double.parseDouble(value));
			break;
		case "Integer":
		case "int":
			f.setInt(pojo, Integer.parseInt(value));
			break;
		case "Float":
		case "float":
			f.setFloat(pojo, Float.parseFloat(value));
			break;
		case "Short":
		case "short":
			f.setShort(pojo, Short.parseShort(value));
			break;
		case "Byte":
		case "byte":
			f.setByte(pojo, Byte.parseByte(value));
		case "Long":
		case "long":
			f.setLong(pojo, Long.parseLong(value));
			break;
		case "Boolean":
		case "boolean":
			f.setBoolean(pojo, Boolean.parseBoolean(value));
			break;
		case "String":
			f.set(pojo, value);
			break;
		case "String[]":
			f.set(pojo, value.split("[, ]+"));
			break;
		case "byte[]":
			f.set(pojo, DatatypeConverter.parseHexBinary(value));
			break;
		default:
			throw new Exception("Invalid parameter type: '"+f.getType().getSimpleName()+"'");
		}
	}
	
	private static ArrayList<Field> loadFields(Object pojo) {
		ArrayList<Field> result = new ArrayList<Field>();
		
		for (Field f : pojo.getClass().getDeclaredFields()) {
			if (f.getAnnotation(Parameter.class) != null)
				result.add(f);
		}
		
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		PojoConfig pojo = PojoConfig.create(PojoConfig.class, args);
		System.out.println(pojo.test);
	}
	
}
