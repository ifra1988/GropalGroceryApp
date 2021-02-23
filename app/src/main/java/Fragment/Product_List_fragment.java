package Fragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.Deal_OfDay_Adapter;
import Adapter.Product_adapter;
import Config.BaseURL;
import Model.Category_model;
import Model.Product_model;
import Model.Slider_subcat_model;
import gropal.in.AppController;
import gropal.in.CustomSlider;
import gropal.in.MainActivity;
import gropal.in.R;
import util.ConnectivityReceiver;
import util.Constants;
import util.CustomVolleyJsonRequest;
import util.ItemDecorationColumns;
import util.UtilityHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rajesh Dabhi on 26/6/2017.
 */

public class Product_List_fragment extends Fragment {
    private static String TAG = Product_List_fragment.class.getSimpleName();
    private RecyclerView rv_cat;
    private TabLayout tab_categories;

    private List<Category_model> category_modelList = new ArrayList<>();
    private List<Slider_subcat_model> slider_subcat_models = new ArrayList<>();
    private List<String> cat_menu_id = new ArrayList<>();
    private List<Product_model> product_modelList = new ArrayList<>();
    private Product_adapter adapter_product;
    private SliderLayout  banner_slider;
    private boolean isDeals;
    private String similar_item;
    String language;
    SharedPreferences preferences;
    public Product_List_fragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        tab_categories = (TabLayout) view.findViewById(R.id.tab_cat);
        banner_slider = (SliderLayout) view.findViewById(R.id.relative_banner);
        rv_cat = (RecyclerView) view.findViewById(R.id.rv_subcategory);
        rv_cat.setLayoutManager(new LinearLayoutManager(getActivity()));
        String getcat_id = getArguments().getString("cat_id");
        String id = getArguments().getString("id");
        String get_deal_id = getArguments().getString("cat_deal");
        String get_top_sale_id = getArguments().getString("cat_top_selling");
        String getcat_title = getArguments().getString("cat_title");
        isDeals = getArguments().getBoolean(Constants.deals_only);
        similar_item = getArguments().getString(Constants.isSimilar);

        setCategoryTabs();

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {

            if(isDeals)
            {
                LinearLayout ll_banner = (LinearLayout) view.findViewById(R.id.ll_banner);
                ll_banner.setVisibility(View.GONE);

                //Deal Of The Day Product
                ((MainActivity) getActivity()).setToolbarTitle("Deals of the day");
                getDealsOfTheDay();
            }
            else if(similar_item != null && !similar_item.isEmpty())
            {
                ((MainActivity) getActivity()).setToolbarTitle(similar_item);
                getSimilarProducts(similar_item);
            }
            else
            {
                //Shop by Catogary
                if(getcat_id != null){
                    ((MainActivity) getActivity()).setToolbarTitle(getcat_title);
                    makeGetCategoryRequest(getcat_id);
                }

                if(get_top_sale_id != null) {
                    //Top Sale Products
                    ((MainActivity) getActivity()).setToolbarTitle("Top sale products");
                    maketopsaleProductRequest(get_top_sale_id);
                }
                if(id != null) {
                    makeGetSliderCategoryRequest(id);
                }
            }

            //Slider
            makeGetBannerSliderRequest();

        }

        return view;
    }

    private void setCategoryTabs()
    {
        tab_categories.setVisibility(View.GONE);
        tab_categories.setSelectedTabIndicatorColor(getActivity().getResources().getColor(R.color.green));

        tab_categories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String getcat_id = cat_menu_id.get(tab.getPosition());
                if (ConnectivityReceiver.isConnected()) {
                    //Shop By Catogary Products
                    makeGetProductRequest(getcat_id);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }


    private void makeGetCategoryRequest(final String parent_id) {

        String tag_json_obj = "json_category_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", parent_id);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_CATEGORY_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Category_model>>() {}.getType();
                        category_modelList = gson.fromJson(response.getString("data"), listType);
                        if (!category_modelList.isEmpty()) {
                            tab_categories.setVisibility(View.VISIBLE);
                            cat_menu_id.clear();
                            for (int i = 0; i < category_modelList.size(); i++) {
                                cat_menu_id.add(category_modelList.get(i).getId());
                                preferences = getActivity().getSharedPreferences("lan", MODE_PRIVATE);

                                language=preferences.getString("language","");
                                if (language.contains("english")) {
                                    tab_categories.addTab(tab_categories.newTab().setText(category_modelList.get(i).getTitle()));
                                }
                                else {
                                    tab_categories.addTab(tab_categories.newTab().setText(category_modelList.get(i).getArb_title()));

                                }
                            }
                        } else {
                            makeGetProductRequest(parent_id);
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

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    //Get Shop By Catogary Products
    private void makeGetProductRequest(String cat_id) {

        String tag_json_obj = "json_product_req";

        Map<String, String> params = new HashMap<String, String>();

        params.put("cat_id", cat_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    boolean status = response.getBoolean("responce");
                    boolean isData = response.has("data");
                    if (status && isData) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();

                        product_modelList = gson.fromJson(response.getString("data"), listType);
                        adapter_product = new Product_adapter(product_modelList, getActivity(),isDeals,R.layout.row_product_rv);
                        setListView();
                    }
                    else
                    {
                        clearList();
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void clearList()
    {
        if(product_modelList != null && adapter_product != null) {
            product_modelList.clear();
            adapter_product.notifyDataSetChanged();
        }
    }

    //Get Shop By Catogary
    private void makeGetSliderCategoryRequest(final String sub_cat_id) {
        String tag_json_obj = "json_category_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("sub_cat", sub_cat_id);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_SLIDER_CATEGORY_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Slider_subcat_model>>() {}.getType();
                        slider_subcat_models = gson.fromJson(response.getString("subcat"), listType);
                        if (!slider_subcat_models.isEmpty()) {
                          //  tab_cat.setVisibility(View.VISIBLE);
                            cat_menu_id.clear();
                            for (int i = 0; i < slider_subcat_models.size(); i++) {
                                cat_menu_id.add(slider_subcat_models.get(i).getId());
                                preferences = getActivity().getSharedPreferences("lan", MODE_PRIVATE);

                              /*  language=preferences.getString("language","");
                                if (language.contains("english")) {
                                    tab_cat.addTab(tab_cat.newTab().setText(slider_subcat_models.get(i).getTitle()));
                                }
                                else {
                                    tab_cat.addTab(tab_cat.newTab().setText(slider_subcat_models.get(i).getArb_title()));

                                }*/
                            }
                        } else {
                          //  makeGetProductRequest(parent_id);
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

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    private void getSimilarProducts(String searchTxt) {

        // Tag used to cancel the request
        String tag_json_obj = "json_product_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("search", searchTxt);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_SIMILAR_PRODUCTS, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Gson gson = new Gson();

                    Boolean status = response.getBoolean("responce");
                    if (status && response.has("data")) {

                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();

                        product_modelList = gson.fromJson(response.getString("data"), listType);
                        adapter_product = new Product_adapter(product_modelList, getActivity(),isDeals,R.layout.row_product_rv);
                        setListView();



                    }                 } catch (JSONException e) {
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

    private void getDealsOfTheDay() {
        String tag_json_obj = "json_category_req";
        //isSubcat = false;
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");
        //isSubcat = true;
       /* if (parent_id != null && parent_id != "") {
        }*/

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.GET,
                BaseURL.GET_DEAL_OF_DAY_PRODUCTS, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    if (response != null && response.length() > 0) {
                        Boolean status = response.getBoolean("responce");
                        Gson gson = new Gson();
                        if (status) {
                            Type listType = new TypeToken<List<Product_model>>() {
                            }.getType();
                            List<Product_model> deal_of_day_models = gson.fromJson(String.valueOf(response.getJSONArray("Deal_of_the_day")), listType);
                            Deal_OfDay_Adapter deal_ofDay_adapter = new Deal_OfDay_Adapter(UtilityHelper.removedExpiredProduct(deal_of_day_models),getContext());
                            setDealsListView(deal_ofDay_adapter);
                            deal_ofDay_adapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getActivity(), "No Response", Toast.LENGTH_SHORT).show();
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


    private void setDealsListView(Deal_OfDay_Adapter adapter)
    {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rv_cat.setLayoutManager(gridLayoutManager);
        rv_cat.setAdapter(adapter);
        rv_cat.addItemDecoration(new ItemDecorationColumns(1,2));
        adapter.notifyDataSetChanged();
    }


    private void setListView()
    {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rv_cat.setLayoutManager(gridLayoutManager);
        rv_cat.setAdapter(adapter_product);
        rv_cat.addItemDecoration(new ItemDecorationColumns(1,2));
        adapter_product.notifyDataSetChanged();
    }

    ////Get Top Sale Products
    private void maketopsaleProductRequest(String cat_id) {
        String tag_json_obj = "json_product_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("top_selling_product", cat_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_ALL_TOP_SELLING_PRODUCTS, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
                        product_modelList = gson.fromJson(response.getString("top_selling_product"), listType);
                        adapter_product = new Product_adapter(product_modelList, getActivity(),isDeals,R.layout.row_product_rv);
                        setListView();
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

    private void makeGetBannerSliderRequest() {
        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_BANNER_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = (JSONObject) response.get(i);
                                HashMap<String, String> url_maps = new HashMap<String, String>();
                                url_maps.put("slider_title", jsonObject.getString("slider_title"));
                                url_maps.put("sub_cat", jsonObject.getString("sub_cat"));
                                url_maps.put("slider_image", BaseURL.IMG_SLIDER_URL + jsonObject.getString("slider_image"));
                                listarray.add(url_maps);
                            }
                            for (HashMap<String, String> name : listarray) {
                                CustomSlider textSliderView = new CustomSlider(getActivity());
                                textSliderView.description(name.get("")).image(name.get("slider_image")).setScaleType(BaseSliderView.ScaleType.Fit);
                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle().putString("extra", name.get("slider_title"));
                                textSliderView.getBundle().putString("extra", name.get("sub_cat"));
                                banner_slider.addSlider(textSliderView);
                                final String sub_cat = (String) textSliderView.getBundle().get("extra");
//                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
//                                    @Override
//                                    public void onSliderClick(BaseSliderView slider) {
//                                        //   Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
//                                        Bundle args = new Bundle();
//                                        Fragment fm = new Product_fragment();
//                                        args.putString("id", sub_cat);
//                                        fm.setArguments(args);
//                                        FragmentManager fragmentManager = getFragmentManager();
//                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                                                .addToBackStack(null).commit();
//                                    }
//                                });

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
        AppController.getInstance().addToRequestQueue(req);

    }



}



