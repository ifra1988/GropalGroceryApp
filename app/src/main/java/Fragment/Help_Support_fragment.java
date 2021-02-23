package Fragment;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import Model.Support_info_model;
import gropal.in.AppController;
import gropal.in.MainActivity;
import gropal.in.R;
import util.ConnectivityReceiver;
import util.Constants;
import util.PreferenceUtility;
import util.UIUtility;

public class Help_Support_fragment extends Fragment implements View.OnClickListener {

    private static String TAG = Help_Support_fragment.class.getSimpleName();

    private ImageView iv_arrow1,iv_arrow2,iv_arrow3,iv_arrow4,iv_arrow5,iv_arrow6;
    private TextView ans1, ans2, ans3, ans4, ans5, ans6;
    private RelativeLayout rl_q1, rl_q2, rl_q3, rl_q4, rl_q5, rl_q6, rl_call,rl_chat, rl_email;
    SharedPreferences callprefrences;
    SharedPreferences.Editor calleditor;

    public Help_Support_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help_support, container, false);

        callprefrences= getActivity().getSharedPreferences("calling", Context.MODE_PRIVATE);

        ((MainActivity) getActivity()).setToolbarTitle(getResources().getString(R.string.nav_help));

        initializeViews(view);

        rl_q1.setOnClickListener(this);
        rl_q2.setOnClickListener(this);
        rl_q3.setOnClickListener(this);
        rl_q4.setOnClickListener(this);
        rl_q5.setOnClickListener(this);
        rl_q6.setOnClickListener(this);

        rl_call.setOnClickListener(this);
        rl_email.setOnClickListener(this);
        rl_chat.setOnClickListener(this);

       /* String geturl = getArguments().getString("url");
        //   String title = getArguments().getString("title");

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.contact));

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetInfoRequest(geturl);
        } else {
            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
        }*/

        return view;
    }

    private void initializeViews(View view)
    {
        iv_arrow1 = (ImageView) view.findViewById(R.id.iv_plus_q1);
        iv_arrow2 = (ImageView) view.findViewById(R.id.iv_plus_q2);
        iv_arrow3 = (ImageView) view.findViewById(R.id.iv_plus_q3);
        iv_arrow4 = (ImageView) view.findViewById(R.id.iv_plus_q4);
        iv_arrow5 = (ImageView) view.findViewById(R.id.iv_plus_q5);
        iv_arrow6 = (ImageView) view.findViewById(R.id.iv_plus_q6);

        ans1 = (TextView) view.findViewById(R.id.tv_ans1);
        ans2 = (TextView) view.findViewById(R.id.tv_ans2);
        ans3 = (TextView) view.findViewById(R.id.tv_ans3);
        ans4 = (TextView) view.findViewById(R.id.tv_ans4);
        ans5 = (TextView) view.findViewById(R.id.tv_ans5);
        ans6 = (TextView) view.findViewById(R.id.tv_ans6);

        rl_q1 = (RelativeLayout) view.findViewById(R.id.rl_q1);
        rl_q2 = (RelativeLayout) view.findViewById(R.id.rl_q2);
        rl_q3 = (RelativeLayout) view.findViewById(R.id.rl_q3);
        rl_q4 = (RelativeLayout) view.findViewById(R.id.rl_q4);
        rl_q5 = (RelativeLayout) view.findViewById(R.id.rl_q5);
        rl_q6 = (RelativeLayout) view.findViewById(R.id.rl_q6);

        rl_call = (RelativeLayout) view.findViewById(R.id.rl_call);
        rl_email = (RelativeLayout) view.findViewById(R.id.rl_email);
        rl_chat = (RelativeLayout) view.findViewById(R.id.rl_chat);

    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeGetInfoRequest(String url) {

        // Tag used to cancel the request
        String tag_json_obj = "json_info_req";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json array response
                    // loop through each json object

                    boolean status = response.getBoolean("responce");
                    if (status) {

                        List<Support_info_model> support_info_modelList = new ArrayList<>();

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Support_info_model>>() {
                        }.getType();

                        support_info_modelList = gson.fromJson(response.getString("data"), listType);

                        String desc = support_info_modelList.get(0).getPg_descri();
                        String title = support_info_modelList.get(0).getPg_title();


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_q1:
                if(ans1.isShown())
                {
                    ans1.setVisibility(View.GONE);
                    iv_arrow1.setImageResource(R.drawable.down_arrow);
                }
                else {
                    ans1.setVisibility(View.VISIBLE);
                    iv_arrow1.setImageResource(R.drawable.up_arrow);
                }
                break;
            case R.id.rl_q2:
                if(ans2.isShown())
                {
                    iv_arrow2.setImageResource(R.drawable.down_arrow);
                    ans2.setVisibility(View.GONE);
                }
                else {
                    ans2.setVisibility(View.VISIBLE);
                    iv_arrow2.setImageResource(R.drawable.up_arrow);

                }
                break;
            case R.id.rl_q3:
                if(ans3.isShown())
                {
                    ans3.setVisibility(View.GONE);
                    iv_arrow3.setImageResource(R.drawable.down_arrow);
                }
                else{
                    iv_arrow3.setImageResource(R.drawable.up_arrow);
                    ans3.setVisibility(View.VISIBLE);}
                break;
            case R.id.rl_q4:
                if(ans4.isShown())
                {
                    iv_arrow4.setImageResource(R.drawable.down_arrow);
                    ans4.setVisibility(View.GONE);
                }
                else{
                    ans4.setVisibility(View.VISIBLE);
                    iv_arrow4.setImageResource(R.drawable.up_arrow);
                }
                break;
            case R.id.rl_q5:
                if(ans5.isShown())
                {
                    ans5.setVisibility(View.GONE);
                    iv_arrow5.setImageResource(R.drawable.down_arrow);
                }
                else{
                    ans5.setVisibility(View.VISIBLE);
                    iv_arrow5.setImageResource(R.drawable.up_arrow);
                }
                break;
            case R.id.rl_q6:
                if(ans6.isShown())
                {
                    ans6.setVisibility(View.GONE);
                    iv_arrow6.setImageResource(R.drawable.down_arrow);
                }
                else{
                    ans6.setVisibility(View.VISIBLE);
                    iv_arrow6.setImageResource(R.drawable.down_arrow);

                }
                break;

            case R.id.rl_call:
              //  if(isPermissionGranted()){
                    UIUtility.callAction(getContext());
                //}
                break;

            case R.id.rl_chat:
                callWhatsapp();
                break;
            case R.id.rl_email:
                composeEmail();


        }
    }


    private void composeEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, Constants.email_address);
        intent.putExtra(Intent.EXTRA_SUBJECT, Constants.subject);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    private void callWhatsapp()
    {
        String smsNumber = Constants.whatsapp_number; // E164 format without '+' sign
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp");

        startActivity(sendIntent);
    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }


   /* @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_LONG).show();
                    call_action();
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

*/
}

