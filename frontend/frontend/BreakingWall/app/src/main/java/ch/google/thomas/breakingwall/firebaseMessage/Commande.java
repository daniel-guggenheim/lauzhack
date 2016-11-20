package ch.google.thomas.breakingwall.firebaseMessage;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by charles on 20.11.16.
 */

public interface Commande {

    public void execute(Context context, String object);
}
