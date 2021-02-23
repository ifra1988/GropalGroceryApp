package Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import Config.BaseURL;
import butterknife.BindView;
import butterknife.ButterKnife;
import gropal.in.MainActivity;
import gropal.in.R;
import util.ConnectivityReceiver;
import util.Constants;
import util.JSONParser;
import util.NameValuePair;
import util.PreferenceUtility;
import util.Session_management;

/**
 * Created by Rajesh Dabhi on 28/6/2017.
 */

public class Account_fragment extends Fragment implements View.OnClickListener {

    private static String TAG = Account_fragment.class.getSimpleName();

    @BindView(R.id.tv_location)
    TextView tv_location;

    @BindView(R.id.btn_edit)
    Button btn_edit;

    @BindView(R.id.ll_profile)
    LinearLayout ll_profile;

    @BindView(R.id.ll_delivery)
    LinearLayout ll_delivery;

    @BindView(R.id.ll_payment)
    LinearLayout ll_payment;

    @BindView(R.id.ll_orders)
    LinearLayout ll_orders;

    @BindView(R.id.ll_wallet)
    LinearLayout ll_wallet;

    @BindView(R.id.ll_reward)
    LinearLayout ll_rewards;

    public Account_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this,view);

        ((MainActivity) getActivity()).setToolbarTitle(" ");

        btn_edit.setOnClickListener(this);
        ll_profile.setOnClickListener(this);
        ll_delivery.setOnClickListener(this);
        ll_payment.setOnClickListener(this);
        ll_orders.setOnClickListener(this);
        ll_wallet.setOnClickListener(this);
        ll_rewards.setOnClickListener(this);

        String location = PreferenceUtility.getString(getContext(), Constants.selected_place,"");
        if(location == null || location.isEmpty())
        {
            location = PreferenceUtility.getString(getContext(), Constants.current_location,"");
        }
        tv_location.setText(location);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Fragment fm = null;
        if (id == R.id.btn_edit) {
            fm = new Update_Profile_Fragment();
        } else if (id == R.id.iv_pro_img) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
        }
        else if (id == R.id.ll_profile) {
            fm = new Update_Profile_Fragment();
        }
        else if (id == R.id.ll_delivery) {
            fm = new Delivery_Address_Fragment();
        }
        else if (id == R.id.ll_payment) {
            fm = new Enter_Card_fragment();
        }
        else if (id == R.id.ll_orders) {

        }
        else if (id == R.id.ll_wallet) {
            fm = new Wallet_fragment();
        }
        else if (id == R.id.ll_reward) {
            fm = new Reward_fragment();
        }

        if (fm != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();

        }
    }



}
