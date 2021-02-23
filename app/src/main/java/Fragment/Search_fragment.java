package Fragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.Product_adapter;
import Adapter.SuggestionAdapter;
import Config.BaseURL;
import Model.Product_model;
import gropal.in.AppController;
import gropal.in.MainActivity;
import gropal.in.R;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonRequest;
import util.ItemDecorationColumns;
import util.UIUtility;

/**
 * Created by Rajesh Dabhi on 14/7/2017.
 */

public class Search_fragment extends Fragment {

    private static String TAG = Search_fragment.class.getSimpleName();
    //    String[] fruits = {"MIlk butter & cream", "Bread Buns & Pals", "Dals Mix Pack", "buns-pavs", "cakes", "Channa Dal", "Toor Dal", "Wheat Atta"
//            , "Beson", "Almonds", "Packaged Drinking", "Cola drinks", "Other soft drinks", "Instant Noodles", "Cup Noodles", "Salty Biscuits", "cookie", "Sanitary pads", "sanitary Aids"
//            , "Toothpaste", "Mouthwash", "Hair oil", "Shampoo", "Pure & pomace olive", "ICE cream", "Theme Egg", "Amul Milk", "AMul Milk Pack power", "kaju pista dd"};
    private AutoCompleteTextView acTextView;
    private RelativeLayout btn_search;
    private RecyclerView rv_search;

    private List<Product_model> product_modelList = new ArrayList<>();
    private Product_adapter adapter_product;

    public Search_fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

       ((MainActivity) getActivity()).setToolbarTitle(getResources().getString(R.string.search));


        acTextView = (AutoCompleteTextView) view.findViewById(R.id.et_search);
        acTextView.setThreshold(1);
        UIUtility.setFocus(acTextView,getContext());
        acTextView.setAdapter(new SuggestionAdapter(getActivity(), acTextView.getText().toString()));

        acTextView.setTextColor(getResources().getColor(R.color.green));
        rv_search = (RecyclerView) view.findViewById(R.id.rv_search);
        setListView();

        acTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ConnectivityReceiver.isConnected()) {
                    String get_search_txt = (String) parent.getItemAtPosition(position);
                    makeGetProductRequest(get_search_txt);
                } else {
                    ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
                }
            }
        });

        return view;
    }

    private void setListView()
    {
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 2);
        rv_search.setLayoutManager(gridLayoutManager1);
        rv_search.setItemAnimator(new DefaultItemAnimator());
        rv_search.addItemDecoration(new ItemDecorationColumns(1,2));

    }

    /**
     * Method to make json object request where json response starts wtih {
     */
    private void makeGetProductRequest(String search_text) {

        // Tag used to cancel the request
        String tag_json_obj = "json_product_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("search", search_text);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();

                        product_modelList = gson.fromJson(response.getString("data"), listType);

                        adapter_product = new Product_adapter(product_modelList, getActivity(),false,R.layout.row_product_rv);
                        rv_search.setAdapter(adapter_product);
                        adapter_product.notifyDataSetChanged();

                        
                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                        }

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
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}
