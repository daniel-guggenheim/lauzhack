package ch.google.thomas.breakingwall.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ch.google.thomas.breakingwall.JSONRequest;

/**
 * Created by Thomas on 19.11.2016.
 */
public class DataBaseManager {
    //TODO this class must be thread safe especially the database
    private final static DataBaseManager db = new DataBaseManager();
    private final String uri = "http://128.179.142.164:8081";
    private Context context;
    private String token = null;
    private User userID = new User("1234","Thomasss");

    private RequestQueue mRequestQueue = null;

    /**
     * @return the unique instance of the dataBase
     */
    public static DataBaseManager getInstance() {
        return db;
    }

    /**
     * Retrieve information about the currently logged user by asking the server.
     * ...
     */
    public void test() {
        Log.i("DataBaseAccess", "getInformationAboutConnectedUser");
        final Map<String, String> loginParams = new HashMap<String, String>();
        //loginParams.put("hello", "test");
        //loginParams.put("test", "56");
        JSONRequest req = new JSONRequest(Request.Method.GET, uri+"/test", loginParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String response = "";
                try {
                    response = jsonObject.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(context,response , Toast.LENGTH_LONG).show();
                Log.i("DataBaseAccess", "response");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DataBaseAccess", error.toString());
            }
        });
        mRequestQueue.add(req);
    }

    public void getLastMessage() {

        final Map<String, String> loginParams = new HashMap<String, String>();
        JSONRequest req = new JSONRequest(Request.Method.GET, uri + "/user/" + userID.getID() + "/last_message", loginParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String message="";
                try {
                    message = jsonObject.getString("lastMessage");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(context, message,Toast.LENGTH_LONG).show();
            }

        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DataBaseAccess", error.toString());
            }
        });

        mRequestQueue.add(req);

    }

    /**
     * To be call before all as it will set up important dependence for other methods to work
     * properly
     *
     * @param context of the Application
     */
    public void init(Context context) {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
        //sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public void sendRegistrationToServer(String refreshedToken) {
        //TODO
    }
}
