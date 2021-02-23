package util;

import org.json.JSONObject;

public interface VolleyResponseListener {

    void onError(String message);

    void onResponse(Object response);
    void onResponseJsonObj(JSONObject jsonObjectResponse);
    void onResponseJsonString(String jsonObjectResponse);
}