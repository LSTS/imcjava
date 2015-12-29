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
 * $Id:: IMCMessageType.java 372 2013-01-28 17:22:07Z zepinto                  $:
 */
package pt.lsts.imc;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * When parsing the messages definition file, an hashtable is filled with the supported message structures.
 * This class holds information about a message like its size, name and fields.
 * @author ZP
 *
 */
public class IMCMessageType {

    public static final int UNKNOWN_SIZE = -1;

    private int id = -1;
    private String fullName;
    private String shortName;
    private int size;
    private int computedLength = 0;
    private String imcVersion = "Unknown";
    private String description;
    private Vector<String> flags = new Vector<String>();
    private LinkedHashMap<String, IMCFieldType> fields = new LinkedHashMap<String, IMCFieldType>();
    private LinkedHashMap<String, String> units = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, Integer> offsets = new LinkedHashMap<String, Integer>();
    private LinkedHashMap<String, LinkedHashMap<Long, String>> fieldPossibleValues = new LinkedHashMap<String, LinkedHashMap<Long, String>>();
    private LinkedHashMap<String, LinkedHashMap<String, Long>> fieldPossibleValuesInverse = new LinkedHashMap<String, LinkedHashMap<String, Long>>();
    private LinkedHashMap<String, String> fieldPrefixes = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, Object> defaultValues = new LinkedHashMap<String, Object>();
    private LinkedHashMap<String, String> descriptions = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> fullnames = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> subtypes = new LinkedHashMap<String, String>();
    private IMCMessageType superType = null;
    
    public IMCMessageType() {
    	
    }
    
    public IMCMessageType(IMCMessageType superType) {
    	fields.putAll(superType.fields);
    	units.putAll(superType.units);
    	offsets.putAll(superType.offsets);
    	fieldPossibleValues.putAll(superType.fieldPossibleValues);
    	fieldPossibleValuesInverse.putAll(superType.fieldPossibleValuesInverse);
    	fieldPrefixes.putAll(superType.fieldPrefixes);
    	defaultValues.putAll(superType.defaultValues);
    	descriptions.putAll(superType.descriptions);
    	fullnames.putAll(superType.fullnames);
    	subtypes.putAll(superType.subtypes);
    }
    
    public Vector<String> getFlags() {
        return flags;
    }
    
    public boolean hasFlag(String flag) {
    	return getFlags().contains(flag);
    }
    
    public Object getDefaultValue(String field) {
        return defaultValues.get(field);
    }

    public int getOffsetOf(String field) {
        Integer offset = offsets.get(field);
        return (offset == null)? -1 : offset;
    }

    public static boolean isNumericType(String type) {
        String t = type.toLowerCase();
        if (t.startsWith("int"))
            return true;
        if (t.startsWith("uint"))
            return true;
        if (t.startsWith("fp"))
            return true;
        if (t.equals("enumerated"))
            return true;
        if (t.equals("bitmask"))
            return true;

        return false;
    }

    public static boolean isInitializable(String type) {
        String t = type.toLowerCase();
        if (!t.startsWith("message"))
            return true;

        return false;
    }

    public void addField(String abbrv, String fieldType, String unit, String min, String max) {		
        fields.put(abbrv, IMCFieldType.getType(fieldType.toLowerCase()));
        if (computedLength != IMCMessageType.UNKNOWN_SIZE) {
            offsets.put(abbrv, computedLength);
            if (IMCUtil.types.get(fieldType) == null) {
                System.err.println("Field type not recognized: "+fieldType);
                return;
            }
            int incSize = IMCUtil.types.get(fieldType).getSizeInBytes();
            if (incSize != IMCMessageType.UNKNOWN_SIZE)				
                computedLength += incSize;
            else
                computedLength = IMCMessageType.UNKNOWN_SIZE;
        }
        else {
            offsets.put(abbrv, IMCMessageType.UNKNOWN_SIZE);			
        }
        if (unit != null)
            units.put(abbrv, unit.toLowerCase());


        if (fieldType.toLowerCase().startsWith("uint") ||
                fieldType.toLowerCase().startsWith("int")) {

            Object defaultValue = 0;

            try {
                long minVal = 0;
                long maxVal = 0;

                if (min != null) {
                    if (min.startsWith("0x") || min.startsWith("0X")) {
                        min = min.substring(2);
                        minVal = Long.parseLong(min, 16);						
                    }
                    else 
                        minVal = Long.parseLong(min);
                }

                if (max != null) {
                    if (max.startsWith("0x") || max.startsWith("0X")) {
                        max = max.substring(2);
                        maxVal = Long.parseLong(max, 16);						
                    }
                    else 
                        maxVal = Long.parseLong(max);
                }

                if (minVal > 0)
                    defaultValue = minVal;
                if (maxVal < 0)
                    defaultValue = maxVal;

                defaultValues.put(abbrv, defaultValue);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (fieldType.toLowerCase().startsWith("fp")) {
            Object defaultValue = 0;

            double minVal = (min != null)? Double.parseDouble(min) : 0;			
            double maxVal = (max != null)? Double.parseDouble(max) : 0;

            if (minVal > 0)
                defaultValue = minVal;
            if (maxVal < 0)
                defaultValue = maxVal;

            defaultValues.put(abbrv, defaultValue);			
        }

        if (fieldType.toLowerCase().equals("plaintext"))
            defaultValues.put(abbrv, "");
    }

    public void setFieldPossibleValues(String abbrev, LinkedHashMap<Long, String> possibleValues) {
        fieldPossibleValues.put(abbrev, possibleValues);
        LinkedHashMap<String, Long> inverse = new LinkedHashMap<String, Long>();

        for (Long key : possibleValues.keySet())
            inverse.put(possibleValues.get(key),key);
        fieldPossibleValuesInverse.put(abbrev, inverse);

        if (possibleValues.keySet().iterator().hasNext())
            defaultValues.put(abbrev, possibleValues.keySet().iterator().next());
    }
    
    public void setFieldPrefix(String field, String prefix) {
        fieldPrefixes.put(field, prefix);
    }
    
    public String getFieldPrefix(String field) {
        if (fieldPrefixes.containsKey(field))
            return fieldPrefixes.get(field);
        else
            return field.toUpperCase();
    }

    public Collection<String> getFieldNames() {
        return fields.keySet();
    }

    public Collection<IMCFieldType> getFieldIMCTypes() {
        return fields.values();
    }

    public IMCFieldType getFieldType(String field) {
        return fields.get(field);
    }
    public String getFieldUnits(String field) {
        return units.get(field);
    }	

    public LinkedHashMap<Long, String> getFieldPossibleValues(String abbrev) {
        return fieldPossibleValues.get(abbrev);
    }

    public LinkedHashMap<String, Long> getFieldMeanings(String abbrev) {
        return fieldPossibleValuesInverse.get(abbrev);
    }
    
    public boolean isAbstract() {
    	return getId() == -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComputedLength() {
        return computedLength;
    }

    public void setComputedLength(int computedLength) {
        this.computedLength = computedLength;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getImcVersion() {
        return imcVersion;
    }

    public void setImcVersion(String imcVersion) {
        this.imcVersion = imcVersion;
    }

    public void setFieldDescription(String field, String description) {
        descriptions.put(field, description);
    }

    public void setFieldName(String field, String fullname) {
        fullnames.put(field, fullname);
    }

    public String getFieldDescription(String field) {
        return descriptions.get(field);
    }

    public String getFullFieldName(String field) {
        if (!fullnames.containsKey(field))
            return field;
        else
            return fullnames.get(field);   
    }
    
    public String getMessageDescription() {
        return description;
    }
    
    public void setMessageDescription(String description) {
        this.description = description;
    }

    /**
     * @return the defaultValues
     */
    public LinkedHashMap<String, Object> getDefaultValues() {
        return defaultValues;
    }
    
    public void setFieldSubtype(String abbrev, String subtype) {
        subtypes.put(abbrev, subtype);
    }
    
    public String getFieldSubtype(String abbrev) {
        return subtypes.get(abbrev);
    }
    
    public IMCMessageType getSupertype() {
        return superType;
    }
    
    public void setSuperType(IMCMessageType msgType) throws Exception {
    	this.superType = msgType;
    }
    
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("type (").append(getShortName()).append(") {\n");
        for (Entry<String, IMCFieldType> field : fields.entrySet()) {
            ret.append("\t").append(field.getKey()).append(":");
            ret.append("\t").append(field.getValue().toString());
            if (getFieldSubtype(field.getKey()) != null)
                ret.append("<"+getFieldSubtype(field.getKey())+">");
            ret.append("\n");                
        }
        ret.append("}\n");
        return ret.toString();
    }
}
