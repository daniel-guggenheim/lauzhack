package ch.google.thomas.breakingwall.firebaseMessage;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.i("receivedMessage",remoteMessage.getData().get("type"));
        Commande cmd;
        Map<String, String> data = remoteMessage.getData();
        if(data != null) {
            switch (data.get("type")) {
                case "new_message":
                    cmd = new NewMessageCommande();
                    String message = data.get("content");
                    cmd.execute(getApplicationContext(),message);
                case "feedback":
                    Log.i("dataType", "feedback");

                default:
                    Log.i("dataType", data.get("type"));
            }
        }
        //cmd.execute(getApplicationContext(), null);

    }
}
