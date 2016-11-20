package ch.google.thomas.breakingwall.data;

import android.content.Context;
import android.content.SharedPreferences;
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
    private final String uri = "http://breakingwall.scapp.io";
    private Context context;
    private static final String PREFS_NAME = "tokenFile";
    private SharedPreferences sharedPreferences;
    private String token = null;
    private User myUser = new LogedUser();
    private User userID = new User("1234","Thomasss");
    private String googleToken = null;
    private String myUsername;

    private RequestQueue mRequestQueue = null;
    private String firebaseToken;


    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }

    public String getGoogleToken() {
        return this.googleToken;
    }
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

    public void signup (final String googleToken, final String username, final Response.Listener<Boolean> listener) {
        Log.i("DataBaseAccess", "signup");
        Map<String, String> loginRequestParams = new HashMap<String, String>();
        loginRequestParams.put("google_token", googleToken);
        loginRequestParams.put("username", username);
        loginRequestParams.put("firebase_token", firebaseToken);
        JSONRequest signupReq = new JSONRequest(Request.Method.POST, uri + "/signup", loginRequestParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token = response.getString("token");
                    setMyUsername(username);
                    setToken(token);
                    listener.onResponse(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onResponse(false);
                }
                listener.onResponse(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HTTPCode", error.networkResponse != null ? String.valueOf(error.networkResponse.statusCode) : "unkown");
                listener.onResponse(false);
            }
        });

        mRequestQueue.add(signupReq);
    }
    public void login(final String googleToken, final Response.Listener<Boolean> listener) {
        Log.i("DataBaseAccess", "login");
        Map<String, String> loginRequestParams = new HashMap<String, String>();
        loginRequestParams.put("google_token", googleToken);
        loginRequestParams.put("firebase_token", firebaseToken);
        JSONRequest loginRequest = new JSONRequest(Request.Method.POST, uri + "/login", loginRequestParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token = response.getString("token");
                    String myUsername = response.getString("username");
                    setMyUsername(myUsername);
                    setToken(token);
                    listener.onResponse(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onResponse(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(error.networkResponse != null) {
                    int HTTPCode = error.networkResponse.statusCode;
                    if (HTTPCode == 404) {
                        listener.onResponse(false);
                    } else if (HTTPCode == 401) {
                        listener.onResponse(false);
                    } else {
                        listener.onResponse(false);
                    }
                } else {
                    Log.e("unexpected","pff");
                    Toast.makeText(context,"Unexpected error", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mRequestQueue.add(loginRequest);

    }

    private Map<String, String> baseParam() {
        Map<String, String> map = new HashMap<>();
        map.put("token", getToken());
        return map;
    }

    public void updateFirebaseID(final String firebaseID, final Response.Listener<Boolean> listener) {
        Map<String, String> param = baseParam();

    }

    public void sendMessage(final Message msg, final Response.Listener<Boolean> listener) {
        Map<String, String> param = baseParam();
        param.put("message_content", msg.getContent());
        JSONRequest req = new JSONRequest(Request.Method.PUT, uri + "/users/" + msg.getReceiverUsername() + "/wall", param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onResponse(false);
            }
        });

        mRequestQueue.add(req);

    }

    public void getLastMessage(final Response.Listener<String> listener) {

        final Map<String, String> loginParams = baseParam();
        JSONRequest req = new JSONRequest(Request.Method.GET, uri + "/users/" + getMyUsername() + "/wall", loginParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String message="";
                try {
                    message = jsonObject.getString("content");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(context, message,Toast.LENGTH_LONG).show();
                listener.onResponse(message);
            }

        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DataBaseAccess", error.toString());
            }
        });

        mRequestQueue.add(req);

    }

    public boolean isLogged() {
        //check if we have a sessionID currently set
        return getToken() != "";
    }

    public String getMyUsername() {
        if(myUsername == null) {
            return sharedPreferences.getString("username", "");
        } else {
            return myUsername;
        }
    }

    /**
     * Retrieved the token saved on the device by {@link #setToken(String)}
     *
     * @return token use for server authentication or an error message
     */
    public String getToken() {
        if(token == null) {
            return sharedPreferences.getString("token", "");
        } else {
            return token;
        }
    }
    public void setMyUsername(String username) {
        if (username != null) {
            this.myUsername = username;
            if (!sharedPreferences.edit().putString("username", username).commit()) {
                Log.e("Login", "Could not commit to preference the username");
            }
        }
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    private void setToken(String token) {
        if (token != null) {
            this.token = token;

        if (!sharedPreferences.edit().putString("token", token).commit()) {
                Log.e("Login", "Could not commit to preference the token");
            }
        }
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
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public void sendRegistrationToServer(String refreshedToken) {
        //TODO
    }


}
