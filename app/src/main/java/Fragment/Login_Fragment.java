package Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Config.BaseURL;
import fcm.MyFirebaseRegister;
import gropal.in.AppController;
import gropal.in.MainActivity;
import gropal.in.R;
import gropal.in.RegisterActivity;
import util.CustomVolleyJsonRequest;
import util.JsonParserData;
import util.Session_management;
import util.UIUtility;
import util.VolleyRequest;
import util.VolleyResponseListener;

public class Login_Fragment extends Fragment implements View.OnClickListener {

    private static String TAG = Login_Fragment.class.getSimpleName();
    private Button btn_continue;
    private LinearLayout ll_otp;
    private EditText et_password, et_email, et_phone, et_otp;
    private TextView tv_password, tv_email, tv_otp_msg, tv_phone, tvTimeRemaining, tvTimer, tvSec, tv_resend_otp;
    private Session_management sessionManagement;
    private String phone, otp, isExists;
    private CountDownTimer countDownTimer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login_otp, container, false);

        et_phone = (EditText) view.findViewById(R.id.et_reg_phone);
        ll_otp = view.findViewById(R.id.ll_otp);
        et_otp = (EditText) view.findViewById(R.id.et_otp);
        et_password = (EditText) view.findViewById(R.id.et_login_pass);
        et_email = (EditText) view.findViewById(R.id.et_login_email);
        tv_password = (TextView) view.findViewById(R.id.tv_login_password);
        tv_email = (TextView) view.findViewById(R.id.tv_login_email);
        btn_continue = (Button) view.findViewById(R.id.btn_continue);
        tv_otp_msg = (TextView) view.findViewById(R.id.tv_msg);
        tvTimeRemaining = (TextView) view.findViewById(R.id.tvTimeRemaining);
        tvTimer = (TextView) view.findViewById(R.id.tvTimer);
        tvSec = (TextView) view.findViewById(R.id.tvSec);
        tv_resend_otp = (TextView) view.findViewById(R.id.tv_resend);

        UIUtility.showSoftKeyboard(et_phone, getContext());

        tv_resend_otp.setOnClickListener(this);
        btn_continue.setOnClickListener(this);

        return view;

    }


    private void timer()
    {
        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvTimeRemaining.setText(""+ millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                tv_resend_otp.setClickable(true);
                tvTimer.setText("OTP not received? ");
                tvSec.setText("");
                tvTimeRemaining.setText("");
                tv_resend_otp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }

        };//.start();
        countDownTimer.start();
    }

    private void showOTPLayout()
    {
        ll_otp.setVisibility(View.VISIBLE);
        UIUtility.setFocus(et_otp,getContext());
        btn_continue.setText(getString(R.string.verify));
        tv_otp_msg.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_continue) {
            attemptLogin();
        }
        else if (id == R.id.btnResendOtp) {
            tv_resend_otp.setClickable(false);
            //timer();
            tv_resend_otp.setTextColor(getResources().getColor(R.color.dark_gray));

            tvTimer.setText(R.string.enter_otp_within);
            tvSec.setText(R.string.sec);
           // tvTimeRemaining.setText("");
        }
    }

    private void attemptLogin() {
        String getphone = et_phone.getText().toString();
        phone = et_phone.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(getphone)) {
            focusView = et_phone;
            cancel = true;
            UIUtility.showAlert(getString(R.string.app_name),getString(R.string.phone_error_msg),getContext());
        } else if (!isPhoneValid(getphone)) {
            // tv_phone.setText(getResources().getString(R.string.phone_too_short));
            // tv_phone.setTextColor(getResources().getColor(R.color.black));
            focusView = et_phone;
            cancel = true;
            UIUtility.showAlert(getString(R.string.app_name),getString(R.string.phone_error_msg),getContext());
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            // perform the user login attempt.
            showOTPLayout();
           /* if (ConnectivityReceiver.isConnected()) {
                if (ll_otp.getVisibility() == View.VISIBLE) {
                    otp = et_otp.getText().toString();
                    if(countDownTimer!=null)
                        countDownTimer.cancel();

                    sendVerifyOtp();
                   // verifyOtp(getphone, otp, isExists);
                } else {
                    tv_resend_otp.setClickable(false);
                    timer();
                   // sendVerifyOtp();
                    sendOtp();
                   // makeLoginRequest(getphone);
                }

            }*/
        }

    }


    private void sendOtp() {
        JSONObject jsonobject_one = new JSONObject();
        try {
            jsonobject_one.put("mobile_number", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog dialog = ProgressDialog.show(getContext(), null, "Please Wait..");

        VolleyRequest.POST_METHOD_JSON_OBJECT(getContext(), BaseURL.SEND_OTP, jsonobject_one, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.e("ASAD onError",message);

                System.out.println("Error" + message);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }


            }

            @Override
            public void onResponse(Object response) {

                System.out.println("SUCCESS" + response);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onResponseJsonObj(JSONObject response) {
                Log.e("ASAD string",response.toString());

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                JsonParserData parser = new Gson().fromJson(response.toString(), JsonParserData.class);
                String logMessage=BaseURL.SEND_OTP + response.toString();
                Log.e("ASAD",logMessage);
                //JSONObject response=parser.data;
                //JSONObject response=parser.data;
                Log.e("JSONObject",response.toString());

                try {
                    String statusStr = response.getString("responce");
                    Log.e("ASAD statusStr",statusStr);

                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        ll_otp.setVisibility(View.VISIBLE);
                        btn_continue.setText("Verify OTP");
                        boolean is_exist = response.getBoolean("is_exist");
                        if (is_exist) {
                            isExists = "true";
                           /* JSONObject obj = response.getJSONObject("data");
                            String user_id = obj.getString("user_id");
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                            MyFirebaseRegister myFirebaseRegister = new MyFirebaseRegister(LoginActivity.this);
                            myFirebaseRegister.RegisterUser(user_id);*/
                        } else {
                            isExists = "false";

                        }

                    } else {
                        btn_continue.setText(R.string.continue_bt);

                        ll_otp.setVisibility(View.GONE);

                       // String error = response.getString("error");
                        btn_continue.setEnabled(true);

                       // Toast.makeText(LoginActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();


                }
            }

            @Override
            public void onResponseJsonString(String jsonObjectResponse) {
                Log.e("onResponseJsonString",""+jsonObjectResponse);
            }
        });
    }
    private void sendVerifyOtp() {
        JSONObject jsonobject_one = new JSONObject();
        try {
            jsonobject_one.put("mobile_number",phone);
            jsonobject_one.put("is_exist",isExists);
            jsonobject_one.put("otp",otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog dialog = ProgressDialog.show(getContext(), null, "Please Wait..");

        VolleyRequest.POST_METHOD_JSON_OBJECT(getContext(), BaseURL.VERIFY_OTP, jsonobject_one, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                System.out.println("Error" + message);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onResponse(Object response) {

                System.out.println("SUCCESS" + response);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onResponseJsonObj(JSONObject response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                   // JsonParserData parser = new Gson().fromJson(jsonObjectResponse.toString(), JsonParserData.class);
                  //  String logMessage=BaseURL.SEND_OTP + jsonObjectResponse.toString();
                 //   Log.e(TAG,logMessage);
                 //   JSONObject response=parser.data;

                    boolean status = response.getBoolean("responce");
                    if (status) {
                       // boolean is_exist = response.getBoolean("is_exist");
                        if(isExists.equals("true"))
                        {
                            JSONObject obj = response.getJSONObject("data");
                            String user_id = obj.getString("user_id");

                            MyFirebaseRegister myFirebaseRegister=new MyFirebaseRegister(getActivity());
                            myFirebaseRegister.RegisterUser(user_id);
                           // sessionManagement.createLoginSession();

                            String user_fullname = obj.getString("user_fullname");
                            String user_email = obj.getString("user_email");
                            String user_phone = obj.getString("user_phone");
                            String user_image = obj.getString("user_image");
                            String wallet_ammount = obj.getString("wallet");
                            String reward_points = obj.getString("rewards");


                            Session_management sessionManagement = new Session_management(getContext());
                            sessionManagement.createLoginSession(user_id, user_email, user_fullname, user_phone,
                                    user_image, wallet_ammount, reward_points, "", "", "",
                                    "", "");

                            Intent i = new Intent(getContext(), MainActivity.class);
                            startActivity(i);
                        }else {
                            Intent i = new Intent(getContext(), RegisterActivity.class);
                            i.putExtra("phone", phone);
                            startActivity(i);

                          //  btn_continue.setEnabled(false);
                        }

                    } else {
                       // String error = response.getString("error");
                        btn_continue.setEnabled(true);

                       // Toast.makeText(LoginActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseJsonString(String jsonObjectResponse) {
                Log.e("onResponseJsonString",""+jsonObjectResponse);
            }
        });
    }


    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeLoginRequest(final String phone) {

        // Tag used to cancel the request
        String tag_json_obj = "json_login_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile_number", phone);
       // params.put("Content-Type", "application/json; charset=utf-8");

        // params.put("password", password);
        Log.d(TAG, params.toString());

        JSONObject jsonobject_one = new JSONObject();
        try {
            jsonobject_one.put("mobile_number",phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, jsonobject_one.toString());


        //JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, API.ADD_PAYMENT, new JSONObject(params), new Response.Listener<JSONObject>() {


            CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest("",Request.Method.POST,
                BaseURL.SEND_OTP, jsonobject_one, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        ll_otp.setVisibility(View.VISIBLE);
                        btn_continue.setText("Verify OTP");
                        Boolean is_exist = response.getBoolean("is_exist");
                        if(is_exist)
                        {
                            isExists="true";
                                    JSONObject obj = response.getJSONObject("data");
                            String user_id = obj.getString("user_id");
                            Intent i = new Intent(getContext(), MainActivity.class);
                            startActivity(i);

                            MyFirebaseRegister myFirebaseRegister=new MyFirebaseRegister(getActivity());
                            myFirebaseRegister.RegisterUser(user_id);
                        }else {
                            isExists="false";
                            Intent i = new Intent(getContext(), RegisterActivity.class);
                            i.putExtra("phone",phone);
                            startActivity(i);

                            btn_continue.setEnabled(false);
                        }

                    } else {
                        btn_continue.setText(R.string.continue_bt);

                        ll_otp.setVisibility(View.GONE);

//                        String error = response.getString("error");
                        btn_continue.setEnabled(true);

//                        Toast.makeText(LoginActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getContext(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        }

                    );

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void verifyOtp(String phone,String otp,String isExists) {

        // Tag used to cancel the request
        String tag_json_obj = "json_login_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile_number", phone);
        params.put("is_exist", isExists);
        params.put("otp", otp);
        Log.d(TAG, params.toString());

        // params.put("password", password);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.VERIFY_OTP, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.d(TAG, response.toString());
                try {
                    boolean status = response.getBoolean("responce");
                    if (status) {
                        boolean is_exist = response.getBoolean("is_exist");
                        if(is_exist)
                        {
                            JSONObject obj = response.getJSONObject("data");
                            String user_id = obj.getString("user_id");
                            Intent i = new Intent(getContext(), MainActivity.class);
                            startActivity(i);

                            MyFirebaseRegister myFirebaseRegister=new MyFirebaseRegister(getActivity());
                            myFirebaseRegister.RegisterUser(user_id);
                        }else {
                            Intent i = new Intent(getContext(), RegisterActivity.class);
                            startActivity(i);

                            btn_continue.setEnabled(false);
                        }

                    } else {
                        String error = response.getString("error");
                        btn_continue.setEnabled(true);

                        Toast.makeText(getContext(), "" + error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getContext(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private boolean isPhoneValid(String phoneno) {
        //TODO: Replace this with your own logic
        return phoneno.length() > 9;
    }


}
