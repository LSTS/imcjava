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
 * $Id:: KrakenGlobal.java 333 2013-01-02 11:11:44Z zepinto                    $:
 */
package pt.lsts.imc.scripting;

import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;

public class KrakenGlobal extends Global {

    private static final long serialVersionUID = 1L;
    private PriorityBlockingQueue<ScheduledFunction> functions = new PriorityBlockingQueue<KrakenGlobal.ScheduledFunction>();
    protected Thread workerThread = null;

    @Override
    public void put(String arg0, Scriptable arg1, Object arg2) {
        super.put(arg0, arg1, arg2);
    }
    
    @Override
    public void init(Context cx) {
        super.init(cx);        
        defineFunctionProperties(new String[] {"setInterval", "setTimeout", "clearInterval"}, getClass(), ScriptableObject.DONTENUM);
    }
    
    public void setTimeout(Function func, Object millis) {
        long per = -1;
        if (millis instanceof Number)
            per = ((Number)millis).longValue();
        
        if (per <= 0)
            return;
        
        ScheduledFunction sf = new ScheduledFunction(func, Context.getCurrentContext(), System.currentTimeMillis()
                + per, 0);
        synchronized (functions) {
            functions.add(sf);
            getWorkerThread();
        }
    }

    public void setInterval(Function func, Object period) {
        long per = -1;
        if (period instanceof Number)
            per = ((Number)period).longValue();
        
        if (per <= 0)
            return;
        ScheduledFunction sf = new ScheduledFunction(func, Context.getCurrentContext(), System.currentTimeMillis()
                + per, per);
        synchronized (functions) {
            functions.add(sf);
            getWorkerThread();
        }
    }

    public void clearInterval(Function func) {
        Vector<ScheduledFunction> funcs = new Vector<KrakenGlobal.ScheduledFunction>();
        synchronized (functions) {
            funcs.addAll(functions);
            for (ScheduledFunction f : funcs) {
                if (f.func == func) {
                    functions.remove(f);
                    return;
                }
            }
        }
    }

    class ScheduledFunction implements Comparable<ScheduledFunction> {
        protected Function func;
        protected long nextExecutionMillis;
        protected long period = 0;
        protected Context context;

        public ScheduledFunction(Function func, Context cx, long firstRun, long period) {
            this.func = func;
            this.context = cx;
            this.nextExecutionMillis = firstRun;
            this.period = period;
        }

        @Override
        public int compareTo(ScheduledFunction o) {
            return (int) (nextExecutionMillis - o.nextExecutionMillis);
        }
    }

    public Thread getWorkerThread() {
        
        if (workerThread != null)
            return workerThread;
        
        workerThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (!functions.isEmpty()) {
                    ScheduledFunction f;
                    synchronized (functions) {
                        f = functions.poll();
                    }
                    if (f == null)
                        break;

                    long time = f.nextExecutionMillis - System.currentTimeMillis();
                    if (time > 0) {
                        try {
                            Thread.sleep(time);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            // interrupted
                            synchronized (functions) {
                                functions.add(f);
                            }
                            break;
                        }
                    }
                    f.func.call(f.context, KrakenGlobal.this, null, null);
                    if (f.period > 0) {
                        f.nextExecutionMillis = System.currentTimeMillis()+f.period;
                        synchronized (functions) {
                            functions.add(f);
                        }
                    }
                }
                System.out.println("worker thread stoped");
                workerThread = null;
            }
        });
        workerThread.start();
        
        return workerThread;
    }
}
