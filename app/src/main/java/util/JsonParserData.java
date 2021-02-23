package util;

import org.json.JSONObject;

import java.io.Serializable;

public class JsonParserData implements Serializable {

    private static final long serialVersionUID = 1L;
    public String responseCode;
    public String height;
    public String status;
    public String user_id;
    public String msg;
    public String total;
    public JSONObject data;
}
