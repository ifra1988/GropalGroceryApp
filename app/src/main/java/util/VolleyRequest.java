package util;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static Config.BaseURL.BASE_URL;

public class VolleyRequest {
    private static int maxTime=10000;
    private static int maximumNumberRetries=2;



    public static void POST_METHOD_JSON_OBJECT(final Context context, String url, final JSONObject jsonObject, final VolleyResponseListener listener) {
    if(jsonObject!=null) {
        Log.d(url, "jsonObject== " + jsonObject.toString());
       // CommonUtils.showProgress();
    }

//    dialogProgress.setCancelable(false);
    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,  url, jsonObject , new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObjectResponse) {
            try {
                listener.onResponseJsonObj(jsonObjectResponse);
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            listener.onError(error.toString());
            String json;
            NetworkResponse response = error.networkResponse;
            //CommonUtils.showLogMesage("Error >>>", "====" + response);
            if(response!=null)
            if(response.data != null){
                if(response.statusCode!=200){
                    json = new String(response.data);
                    json = trimMessage(json);
                    if(json != null) displayMessage(json);
                }
                //Additional cases
            }
        }
        String trimMessage(String json){
            String trimmedString;

            try{
                JSONObject obj = new JSONObject(json);
                trimmedString = obj.getString("msg");
            } catch(JSONException e){
                e.printStackTrace();
                return null;
            }

            return trimmedString;
        }
        //Somewhere that has access to a context
        void displayMessage(String toastString){
           // CommonUtils.showToast(context,toastString);
            Log.e("Error volley",toastString);
        }

    });
        //AppController.getInstance().addToRequestQueue(request);

        Volley.newRequestQueue(context).add(request);

}
    public static void POST_METHOD_JSON_OBJECT_NO_CACHE(final Context context, String url, final JSONObject jsonObject, final VolleyResponseListener listener) {
    if(jsonObject!=null) {
        Log.d("url", "jsonObject== " + jsonObject.toString());
       // CommonUtils.showProgress();
    }

    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL + url, jsonObject , new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObjectResponse) {

            try {
                listener.onResponseJsonObj(jsonObjectResponse);
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            listener.onError(error.toString());
            String json;
            NetworkResponse response = error.networkResponse;
            Log.e("Error >>>", "====" + response);
            if(response!= null){
            try {
                if(response.statusCode!=200){
                    if(response.data != null) {

                            json = new String(response.data);
                            json = trimMessage(json);
                            if (json != null) displayMessage(json);
                        }
                    }
                }
            catch (Exception e)
            {e.printStackTrace();}
                //Additional cases
            }
        }
        String trimMessage(String json){
            String trimmedString;

            try{
                JSONObject obj = new JSONObject(json);
                trimmedString = obj.getString("msg");
            } catch(JSONException e){
                e.printStackTrace();
                return null;
            }

            return trimmedString;
        }
        //Somewhere that has access to a context
        void displayMessage(String toastString){
           // CommonUtils.showToast(context,toastString);
            Log.e("Error volley",toastString);
        }

    });
        request.setRetryPolicy(new DefaultRetryPolicy(maxTime,
                maximumNumberRetries,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    Volley.newRequestQueue(context).add(request);

}
}