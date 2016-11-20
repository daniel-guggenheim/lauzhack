package ch.google.thomas.breakingwall.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.firebase.iid.FirebaseInstanceId;

import ch.google.thomas.breakingwall.R;
import ch.google.thomas.breakingwall.data.DataBaseManager;
import ch.google.thomas.breakingwall.firebaseMessage.MyFirebaseMessagingService;
import ch.google.thomas.breakingwall.firebaseMessage.NewMessageCommande;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataBaseManager.getInstance().init(getApplicationContext());


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(NewMessageCommande.COPA_MESSAGE);
                ((TextView)findViewById(R.id.wall)).setText(s);
                Log.i("test","hello");
            }
        };

        String firebase_token = FirebaseInstanceId.getInstance().getToken();
        if(firebase_token != null)
            Log.i("token",firebase_token);
            DataBaseManager.getInstance().setFirebaseToken(firebase_token);

        if(!DataBaseManager.getInstance().isLogged()) {
            //start login&signup activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        findViewById(R.id.new_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SendActivity.class));
            }
        });

        DataBaseManager.getInstance().getLastMessage(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response !=null) {
                    ((TextView)findViewById(R.id.wall)).setText(response);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(NewMessageCommande.COPA_RESULT)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }


}
