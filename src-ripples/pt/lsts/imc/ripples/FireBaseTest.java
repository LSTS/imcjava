package pt.lsts.imc.ripples;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class FireBaseTest {

	public static void main(String[] args) throws Exception {
		Firebase ref = new Firebase("https://neptus.firebaseio-demo.com/neptus");
		
		
		ref.addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot arg0) {
				System.out.println("data changed to "+arg0.getValue());
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		ref.setValue("hello!");
		ref.setValue("hello1!");
		ref.setValue("hello2!");
		ref.setValue("hello3!");
		Thread.sleep(1000);
	}
	
	
}
