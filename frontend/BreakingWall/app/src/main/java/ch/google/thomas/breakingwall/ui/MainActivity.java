package ch.google.thomas.breakingwall.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import ch.google.thomas.breakingwall.R;
import ch.google.thomas.breakingwall.data.DataBaseManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataBaseManager.getInstance().init(getApplicationContext());
        DataBaseManager.getInstance().getLastMessage();

        String firebase_token = FirebaseInstanceId.getInstance().getToken();

        Log.i("token",firebase_token);
    }
}
