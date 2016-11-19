package ch.google.thomas.breakingwall;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by charles on 28/10/14.
 */
public class JSONRequest extends Request<JSONObject> {

    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;

    public JSONRequest(String url, Map<String, String> params,
                       Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    public JSONRequest(int method, String url, Map<String, String> params,
                       Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        Log.i("NetworkRequest", params.toString());
        this.listener = reponseListener;
        this.params = params;
    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

        try {

            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Log.i("NetworkResponse", jsonString);
            //Workaround to deal with  ï»¿ int the first char of the response
            if (!jsonString.startsWith("{")) {
                int start = jsonString.indexOf("{");
                jsonString = jsonString.substring(start);
            }
            Log.i("NetworkResponse", "After " + jsonString);
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            Log.e("DataBaseAccess", "error1");
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            Log.e("DataBaseAccess", "error2");
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        if (listener != null) {
            listener.onResponse(response);
        } else {
            Log.w("JsonRequest", "No listener specified");
        }
    }
}