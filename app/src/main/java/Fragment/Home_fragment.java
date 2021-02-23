package Fragment;

import android.Manifest;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
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

import Adapter.Categories_adapter;
import Adapter.Deal_OfDay_Adapter;
import Adapter.Home_adapter;
import Adapter.Home_Icon_Adapter;
import Adapter.Top_Selling_Adapter;
import Config.BaseURL;
import Model.Category_model;
import Model.Deal_Of_Day_model;
import Model.Home_Icon_model;
import Model.Product_model;
import Model.Top_Selling_model;
import gropal.in.AppController;
import gropal.in.CustomSlider;
import gropal.in.MainActivity;
import gropal.in.R;
import util.ConnectivityReceiver;
import util.Constants;
import util.CustomVolleyJsonRequest;
import util.ItemDecorationColumns;
import util.RecyclerTouchListener;
import util.UtilityHelper;


public class Home_fragment extends Fragment {
    private static String TAG = Home_fragment.class.getSimpleName();
    private SliderLayout imgSlider, banner_slider, featuredslider;
    private RecyclerView rv_items, rv_top_selling, rv_deal_of_day, rv_headre_icons,rv_grid_categ;
    private List<Category_model> category_modelList = new ArrayList<>();
    private Home_adapter adapter;
    private boolean isSubcat = false;
    LinearLayout search_layout;
    private AutoCompleteTextView atv_search ;
    String getid;
    String getcat_title;
    ScrollView scrollView;
    String whatsappnum,callnum;
    SharedPreferences sharedpreferences;
    private ImageView iv_banner1,iv_banner2;

    //Home Icons
    private Home_Icon_Adapter menu_adapter;
    private List<Home_Icon_model> menu_models = new ArrayList<>();

    //Deal O Day
    private Deal_OfDay_Adapter deal_ofDay_adapter;
    private List<Deal_Of_Day_model> deal_of_day_models = new ArrayList<>();
   // LinearLayout Deal_Linear_layout;
    FrameLayout Deal_Frame_layout, Deal_Frame_layout1;

    //Top Selling Products
    private Top_Selling_Adapter top_selling_adapter;
    private List<Product_model> products = new ArrayList<>();

    private CardView cv_deals, cv_top_selling;
    private TextView timer;
    private TextView View_all_deals, View_all_TopSell,view_all_categories;
SharedPreferences callprefrences;
SharedPreferences.Editor calleditor;

    View view;

    public Home_fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ((MainActivity) getActivity()).finish();
                    return true;
                }
                return false;
            }
        });

        callprefrences= getActivity().getSharedPreferences("calling", Context.MODE_PRIVATE);

        whatsappnum=callprefrences.getString("whatsapp","");
        callnum=callprefrences.getString("call","");

        ((MainActivity) getActivity()).setToolbarTitle("");

        //Check Internet Connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetSliderRequest();
            makeGetBannerSliderRequest();
            makeGetCategoryRequest();
            makeGetFeaturedSlider();
            make_menu_items();
            getDealsOfTheDay();
            make_top_selling();
            setBannersImage();
        }

        atv_search = (AutoCompleteTextView) view.findViewById(R.id.et_search);
        cv_top_selling = (CardView)view.findViewById(R.id.cv_top_selling);
        cv_deals = (CardView)view.findViewById(R.id.cv_deals);
        view_all_categories = (TextView) view.findViewById(R.id.view_all_categories);
        View_all_deals = (TextView) view.findViewById(R.id.view_all_deals);
        View_all_TopSell = (TextView) view.findViewById(R.id.view_all_topselling);

        //Scroll View
        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setSmoothScrollingEnabled(true);

        //Search
        search_layout = (LinearLayout) view.findViewById(R.id.search_layout);
        navigateToSearchScreen();

        //Slider
        imgSlider = (SliderLayout) view.findViewById(R.id.home_img_slider);
        banner_slider = (SliderLayout) view.findViewById(R.id.relative_banner);
        featuredslider = (SliderLayout) view.findViewById(R.id.featured_img_slider);
        iv_banner1 = (ImageView) view.findViewById(R.id.iv_banner1);
        iv_banner2 = (ImageView) view.findViewById(R.id.iv_banner2);

        //Catogary Icons
        rv_items = (RecyclerView) view.findViewById(R.id.rv_category);
        LinearLayoutManager horizontalLayout  = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false); ;
        rv_items.setLayoutManager(horizontalLayout);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setNestedScrollingEnabled(false);
        rv_items.addItemDecoration(new SpacesItemDecoration(9));

        //DealOf the Day
        rv_deal_of_day = (RecyclerView) view.findViewById(R.id.rv_deal);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 2);
        rv_deal_of_day.setLayoutManager(gridLayoutManager1);
        rv_deal_of_day.setItemAnimator(new DefaultItemAnimator());
        rv_deal_of_day.setNestedScrollingEnabled(false);
        rv_deal_of_day.addItemDecoration(new ItemDecorationColumns(1,2));

        //categories grid
        rv_grid_categ = (RecyclerView) view.findViewById(R.id.rv_catg);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rv_grid_categ.setLayoutManager(gridLayoutManager);
        rv_grid_categ.setItemAnimator(new DefaultItemAnimator());
        rv_grid_categ.setNestedScrollingEnabled(false);
        rv_grid_categ.addItemDecoration(new ItemDecorationColumns(1,2));

        //Top Selling Products
        rv_top_selling = (RecyclerView) view.findViewById(R.id.top_selling_recycler);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getActivity(), 2);
        rv_top_selling.setLayoutManager(gridLayoutManager2);
        rv_top_selling.setItemAnimator(new DefaultItemAnimator());
        rv_top_selling.setNestedScrollingEnabled(false);
        rv_top_selling.addItemDecoration(new ItemDecorationColumns(1,2));


        //make_menu_items Icons
        rv_headre_icons = (RecyclerView) view.findViewById(R.id.collapsing_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) {

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {
                    private static final float SPEED = 2000f;// Change this value (default=25f)

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_headre_icons.setLayoutManager(layoutManager);
        rv_headre_icons.setHasFixedSize(true);
        rv_headre_icons.setItemViewCacheSize(10);
        rv_headre_icons.setDrawingCacheEnabled(true);
        rv_headre_icons.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        view_all_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Fragment fm = new Category_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }
        });

        //Recycler View Shop By Catogary
        rv_items.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_items, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getid = category_modelList.get(position).getId();
                getcat_title = category_modelList.get(position).getTitle();
                Bundle args = new Bundle();
                Fragment fm = new Product_List_fragment();
                args.putString("cat_id", getid);
                args.putString("cat_title", getcat_title);
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        //Recycler View Menu Products
        rv_headre_icons.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_headre_icons, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getid = menu_models.get(position).getId();
                Bundle args = new Bundle();
                Fragment fm = new Product_List_fragment();
                args.putString("cat_id", getid);
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        View_all_deals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Fragment fm = new Product_List_fragment();
                args.putBoolean(Constants.deals_only,true);
                args.putString("cat_deal", "2");
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }
        });


        //REcyclerview Top Selling
      /*  rv_top_selling.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_top_selling, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getid = top_selling_models.get(position).getProduct_id();
                Bundle args = new Bundle();
                Fragment fm = new Product_fragment();
                args.putString("cat_top_selling", "2");
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));*/
        View_all_TopSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Fragment fm = new Product_List_fragment();
                args.putString("cat_top_selling", "2");
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }
        });

//        banner_slider.setCurrentPosition(0);
       // banner_slider.setDuration(50);
        banner_slider.stopAutoCycle();

        return view;
    }

    private void navigateToSearchScreen()
    {
        search_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fm = new Search_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }
        });

        atv_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Fragment fm = new Search_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

                return false;
            }
        });

    }

    private void makeGetSliderRequest() {
        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_SLIDER_URL,
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
                                imgSlider.addSlider(textSliderView);
                                final String sub_cat = (String) textSliderView.getBundle().get("extra");
                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
                                        //   Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
                                        Bundle args = new Bundle();
                                        Fragment fm = new Product_List_fragment();
                                        args.putString("id", sub_cat);
                                        fm.setArguments(args);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                                .addToBackStack(null).commit();
                                    }
                                });


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

    private void setBannersImage() {
        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_BANNERS_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
                            for (int i = 0; i < 2; i++) {
                                JSONObject jsonObject1 = (JSONObject) response.get(0);
                                String img_url1 = jsonObject1.getString("image");
                                UtilityHelper.setServerImage(getContext(),BaseURL.GET_NEW_BANNER_URL+img_url1,iv_banner1);

                                JSONObject jsonObject2 = (JSONObject) response.get(1);
                                String img_url2 = jsonObject2.getString("image");
                                UtilityHelper.setServerImage(getContext(),BaseURL.GET_NEW_BANNER_URL+img_url2,iv_banner2);

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
                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
                                        //   Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
                                        Bundle args = new Bundle();
                                        Fragment fm = new Product_List_fragment();
                                        args.putString("id", sub_cat);
                                        fm.setArguments(args);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                                .addToBackStack(null).commit();
                                    }
                                });

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


    private void makeGetFeaturedSlider() {
        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_FEAATURED_SLIDER_URL,
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
                                //  textSliderView.getBundle().putString("extra", name.get("slider_title"));
                                textSliderView.getBundle().putString("extra", name.get("sub_cat"));
                                featuredslider.addSlider(textSliderView);
                                final String sub_cat = (String) textSliderView.getBundle().get("extra");
                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
                                        //   Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
                                        Bundle args = new Bundle();
                                        Fragment fm = new Product_List_fragment();
                                        args.putString("id", sub_cat);
                                        fm.setArguments(args);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                                .addToBackStack(null).commit();
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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


    private void makeGetCategoryRequest() {
        String tag_json_obj = "json_category_req";
        isSubcat = false;
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");
        isSubcat = true;
       /* if (parent_id != null && parent_id != "") {
        }*/

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_CATEGORY_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                ((MainActivity) getActivity()).setToolbarTitle("");

                try {
                    if (response != null && response.length() > 0) {
                        Boolean status = response.getBoolean("responce");
                        if (status) {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Category_model>>() {
                            }.getType();
                            category_modelList = gson.fromJson(response.getString("data"), listType);
                            adapter = new Home_adapter(category_modelList);
                            rv_items.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            setCategoriesGrid(category_modelList);
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

   private void setCategoriesGrid(List<Category_model> categoryModel)
   {
       GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
       rv_grid_categ.setLayoutManager(gridLayoutManager);
       rv_grid_categ.setItemAnimator(new DefaultItemAnimator());
       rv_grid_categ.setNestedScrollingEnabled(false);
       rv_grid_categ.addItemDecoration(new ItemDecorationColumns(1,3));

       Categories_adapter categories_adapter = new Categories_adapter(categoryModel,true);
       rv_grid_categ.setAdapter(categories_adapter);
       categories_adapter.notifyDataSetChanged();
   }

    private void getDealsOfTheDay() {
        String tag_json_obj = "json_category_req";
        isSubcat = false;
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");
        isSubcat = true;
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
                            products = gson.fromJson(String.valueOf(response.getJSONArray("Deal_of_the_day")), listType);
                            deal_ofDay_adapter = new Deal_OfDay_Adapter(UtilityHelper.removedExpiredProduct(products),getContext());
                            rv_deal_of_day.setAdapter(deal_ofDay_adapter);
                            deal_ofDay_adapter.notifyDataSetChanged();
                            if (getActivity() != null) {
                                if (products.isEmpty()) {
                                    //  Toast.makeText(getActivity(), "No Deal For Day", Toast.LENGTH_SHORT).show();
                                    cv_deals.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "No Response", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    cv_deals.setVisibility(View.GONE);
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

    private void make_top_selling() {
        String tag_json_obj = "json_category_req";
        isSubcat = false;
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");
        isSubcat = true;

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_TOP_SELLING_PRODUCTS, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    if (response != null && response.length() > 0) {
                        Boolean status = response.getBoolean("responce");
                        if (status) {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Top_Selling_model>>() {
                            }.getType();
                            products = gson.fromJson(response.getString("top_selling_product"), listType);
                            top_selling_adapter = new Top_Selling_Adapter(products,getContext());
                            rv_top_selling.setAdapter(top_selling_adapter);
                            top_selling_adapter.notifyDataSetChanged();
                            if (products.isEmpty()) {
                                //  Toast.makeText(getActivity(), "No Deal For Day", Toast.LENGTH_SHORT).show();
                             //   cv_top_selling.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (products.isEmpty()) {
                        //  Toast.makeText(getActivity(), "No Deal For Day", Toast.LENGTH_SHORT).show();
                     //   cv_top_selling.setVisibility(View.GONE);
                    }
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


    private void make_menu_items() {
        String tag_json_obj = "json_category_req";
        isSubcat = false;
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");
        isSubcat = true;
       /* if (parent_id != null && parent_id != "") {
        }*/

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_CATEGORY_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    if (response != null && response.length() > 0) {
                        Boolean status = response.getBoolean("responce");
                        if (status) {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Home_Icon_model>>() {
                            }.getType();
                            menu_models = gson.fromJson(response.getString("data"), listType);
                            menu_adapter = new Home_Icon_Adapter(menu_models);
                            rv_headre_icons.setAdapter(menu_adapter);
                            menu_adapter.notifyDataSetChanged();
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


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }


        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



    public void reviewOnApp() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi friends i am using ." + " http://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + " APP");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
    public  boolean isPermissionGranted() {
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


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                    call_action();
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void call_action(){

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + callnum));
        startActivity(callIntent);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }
}
