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
 */
package pt.lsts.imc.ripples;
import java.util.Hashtable;
import java.util.concurrent.TimeoutException;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthListener;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class FirebaseDB {

	private DataSnapshot root = null;
	private static FirebaseDB instance = null;
	private final static String rootPath = "https://neptus.firebaseio-demo.com/";
	private final static String authKey = "";
	
	private static FirebaseDB instance() {
		synchronized (FirebaseDB.class) {
			if (instance == null)
				instance = new FirebaseDB();		
			return instance;
		}		
	}
	
	private static void connect(long timeoutMillis) throws Exception {
		long start = System.currentTimeMillis();
		while(System.currentTimeMillis() - start < timeoutMillis) {
			if (instance().root == null)
				Thread.sleep(100);
			else
				return;
		}
		throw new TimeoutException("Timed out while trying to connect");		
	}
	
	public static void addListener(ValueEventListener listener, String path) {
		new Firebase(rootPath+path).addValueEventListener(listener);
	}
	
	public static void removeListener(ValueEventListener listener, String path) {
		new Firebase(rootPath+path).removeEventListener(listener);
	}
	
	public static DataSnapshot get(String path) {
		if (instance().root == null)
			return null;
		
		return instance().root.child(path);
	}
	
	public static void addValue(String path, Object obj) {
		new Firebase(rootPath+path).push().setValue(obj);
	}
	
	public static void setValue(String path, Object obj) {
		new Firebase(rootPath+path).setValue(obj);
	}
	
	private FirebaseDB() {
		new Firebase(rootPath).addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot arg0) {
				root = arg0;				
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				System.out.println("Cancelled: "+arg0.getMessage());
			}
		});
		if (!authKey.isEmpty()) {
			new Firebase(rootPath).auth(authKey, new AuthListener() {
				
				@Override
				public void onAuthSuccess(Object arg0) {
					System.out.println("Connected.");				
				}
				
				@Override
				public void onAuthRevoked(FirebaseError arg0) {
					System.out.println(arg0.getCode()+": "+arg0.getMessage());
				}
				
				@Override
				public void onAuthError(FirebaseError arg0) {
					System.out.println(arg0.getCode()+": "+arg0.getMessage());
				}
			});
		}
	}
	
	public static void main(String[] args) throws Exception{
		FirebaseDB.connect(10000);
		System.out.println(FirebaseDB.get("sample").getValue());
		while(true) {
			FirebaseDB.setValue("sample/test2", new Hashtable<String, Object>());
			Thread.sleep(100);
			System.out.println(FirebaseDB.get("sample").getValue());
		}
	}
}
