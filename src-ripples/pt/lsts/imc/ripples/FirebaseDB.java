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
