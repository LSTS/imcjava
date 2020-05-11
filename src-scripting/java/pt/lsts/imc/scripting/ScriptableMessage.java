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
 *  
 * $Id:: ScriptableMessage.java 333 2013-01-02 11:11:44Z zepinto               $:
 */
package pt.lsts.imc.scripting;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;

import pt.lsts.imc.Header;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCMessageType;
import pt.lsts.imc.IMCUtil;

public class ScriptableMessage extends ScriptableObject {

    private static final long serialVersionUID = -5940960911756554940L;

    private IMCMessageType msgType;
    private IMCMessage original = null;

    public ScriptableMessage() {

    }

    protected final IMCMessageType headerType = IMCDefinition.getInstance().createHeader().getMessageType();

    @Override
    public void put(String name, Scriptable start, Object value) {

        if (name.charAt(0) != '_' && msgType != null && msgType.getFieldType(name) == null
                && headerType.getFieldType(name) == null) {
            System.err.println("Invalid message field: " + msgType.getShortName() + "." + name);
        }
        super.put(name, start, value);
    }

    public ScriptableMessage(String messageType) throws Exception {
        IMCMessage message = IMCDefinition.getInstance().create(messageType);
        if (message == null) {
            throw new Exception("There's no message named '" + messageType + "'.");
        }
        putConstProperty(this, "_type", message.getAbbrev());

        setValues(message.getHeader());
        setValues(message);
        msgType = message.getMessageType();

    }

    @Override
    public String getClassName() {
        return "Message";
    }

    public void jsFunction_asHtml() throws Exception {
        IMCMessage m = getAsMessage();
        if (m == null) {
            System.out.println("null");
            return;
        }

        m.getHeader().setValue("sync", IMCDefinition.getInstance().getSyncWord());
        m.getHeader().setValue("mgid", m.getMessageType().getId());
        if (m.getTimestamp() == 0)
            m.getHeader().setValue("time", System.currentTimeMillis() / 1000.0);
        m.getHeader().setValue("size", m.getPayloadSize());
        System.out.println(IMCUtil.getAsHtml(m));
    }

    public void jsFunction_dump() throws Exception {

        IMCMessage m = getAsMessage();
        if (m == null) {
            System.out.println("null");
            return;
        }

        m.getHeader().setValue("sync", IMCDefinition.getInstance().getSyncWord());
        m.getHeader().setValue("mgid", m.getMessageType().getId());
        if (m.getTimestamp() == 0)
            m.getHeader().setValue("time", System.currentTimeMillis() / 1000.0);
        m.getHeader().setValue("size", m.getPayloadSize());
        m.dump(System.out);
    }

    protected void setValues(IMCMessage message) {
        for (String field : message.getMessageType().getFieldNames()) {

            switch (message.getMessageType().getFieldType(field)) {
                case TYPE_MESSAGE:
                    IMCMessage inline = message.getMessage(field);
                    if (inline == null)
                        continue;
                    ScriptableMessage inner = new ScriptableMessage();
                    inner.setMessage(inline);

                    putProperty(this, field, inner);
                    break;
                case TYPE_PLAINTEXT:
                    putProperty(this, field, message.getString(field));
                    break;
                case TYPE_RAWDATA:
                    byte[] rawdata = message.getRawData(field);
                    if (rawdata == null)
                        rawdata = new byte[0];
                    NativeArray na = new NativeArray(rawdata.length);
                    
                    for (byte b : rawdata)
                        na.add(b);
                    putProperty(this, field, na);
                    break;
                case TYPE_FP32:
                case TYPE_FP64:
                    putProperty(this, field, message.getDouble(field));
                default:
                    putProperty(this, field, message.getLong(field));
                    break;
            }
        }
    }

    public void setMessage(IMCMessage message) {

        msgType = message.getMessageType();
        original = message;

        putConstProperty(this, "_type", message.getAbbrev());

        putProperty(this, "time", message.getHeader().getDouble("time"));
        putProperty(this, "src", message.getHeader().getDouble("src"));
        putProperty(this, "dst", message.getHeader().getDouble("dst"));
        putProperty(this, "src_ent", message.getHeader().getDouble("src_ent"));
        putProperty(this, "dst_ent", message.getHeader().getDouble("dst_ent"));

        for (String field : message.getMessageType().getFieldNames()) {

            switch (message.getMessageType().getFieldType(field)) {
                case TYPE_MESSAGE:
                    IMCMessage inline = message.getMessage(field);
                    if (inline == null)
                        continue;
                    ScriptableMessage inner = new ScriptableMessage();
                    inner.setMessage(inline);

                    putProperty(this, field, inner);
                    break;
                case TYPE_PLAINTEXT:
                    putProperty(this, field, message.getString(field));
                    break;
                case TYPE_RAWDATA:
                    byte[] rawdata = message.getRawData(field);
                    // NativeArray na = new NativeArray(rawdata.length);
                    // na.addAll(Arrays.asList(rawdata));
                    putProperty(this, field, rawdata);
                    break;
                case TYPE_FP32:
                case TYPE_FP64:
                    putProperty(this, field, message.getDouble(field));
                default:
                    putProperty(this, field, message.getLong(field));
                    break;
            }
        }

    }

    public IMCMessage getAsMessage() {

        String type = getProperty(this, "_type").toString();
        int mgid = IMCDefinition.getInstance().getMessageId(type);
        if (mgid == -1) {
            return null;
        }

        Header header;
        if (original != null)
            header = original.getHeader();
        else
            header = IMCDefinition.getInstance().createHeader();

        for (String field : header.getFieldNames())
            if (hasProperty(this, field)) {
                header.setValue(field, getProperty(this, field));
            }

        if (header.getDouble("time") == 0)
            header.setValue("time", System.currentTimeMillis() / 1000.0);

        header.setValue("mgid", mgid);

        IMCMessage msg = new IMCMessage(header);

        for (String field : msg.getMessageType().getFieldNames()) {
            if (!hasProperty(this, field))
                continue;
            switch (msg.getMessageType().getFieldType(field)) {
                case TYPE_MESSAGE:
                    ScriptableMessage sm = (ScriptableMessage) getProperty(this, field);
                    if (sm != null) {
                        msg.setValue(field, sm.getAsMessage());
                    }
                    break;
                case TYPE_PLAINTEXT:
                    msg.setValue(field, getProperty(this, field).toString());
                case TYPE_RAWDATA:
                    try {
                        NativeArray na = (NativeArray) getProperty(this, field);
                        if (na != null)
                            msg.setValue(field, na.toArray(new Integer[0]));
                        break;
                    }
                    catch (Exception e) {

                    }
                default:
                    msg.setValue(field, getProperty(this, field));
                    break;
            }
        }
        header.setValue("size", msg.getPayloadSize());
        return msg;
    }

    public IMCMessage getOriginal() {
        return original;
    }

    public static void main(String[] args) throws Exception {
        // initialization
        Context cx = Context.enter();
        Global s = new Global();
        s.init(cx);
        cx.evaluateString(s, "importClass(java.lang.Thread);", "x", 0, null);
        ScriptableObject.defineClass(s, ScriptableMessage.class);
        // end of initialization

        cx.evaluateString(s, "m = new Message('EstimatedState');\n"
                + "spawn(function() {print('Hello!'); Thread.sleep(5000); print('Goodbye!');});\n"
                + "m.ref = 'NED_LLD';\n" + "m.x = -100.567;\n" + "m.p = (Math.PI / 180) * 10;\n" + "m.dump();",
                "inline", 1, null);
        Context.exit();
    }
}
