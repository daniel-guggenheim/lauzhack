package ch.google.thomas.breakingwall.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

import ch.google.thomas.breakingwall.R;
import ch.google.thomas.breakingwall.data.DataBaseManager;
import ch.google.thomas.breakingwall.data.Message;

public class SendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        findViewById(R.id.send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText destination = ((EditText) findViewById(R.id.destination));
                final EditText msg = ((EditText) findViewById(R.id.message));
                Message message = new Message(destination.getText().toString(), msg.getText().toString());
                DataBaseManager.getInstance().sendMessage(message, new Response.Listener<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        if(response) {
                            destination.setText("");
                            msg.setText("");
                        } else {
                            Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
