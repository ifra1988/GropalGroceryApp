package Fragment;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.Product_adapter;
import Adapter.SliderAdapter;
import Config.BaseURL;
import Model.Deal_Of_Day_model;
import Model.Product_model;
import Model.SliderItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import gropal.in.AppController;
import gropal.in.MainActivity;
import gropal.in.R;
import util.Constants;
import util.CustomVolleyJsonRequest;
import util.DatabaseHandler;
import util.ItemDecorationColumns;
import util.UtilityHelper;

public class Product_Detail_Fragment extends Fragment {
    private static String TAG = Product_Detail_Fragment.class.getSimpleName();
    private TextView discount_tv;
    private TextView mrp;
    private TextView sellingPrice;
    private TextView productName;
    private TextView quantity_tv;
    private TextView descr_tv;
    private String inrSymbol = "₹";

    private Button add_product_btn;
    private ImageView add_product_iv;
    private ImageView rmv_product_iv;
    private ImageView iv_share;

    private LinearLayout add_rmv_ll;
    private LinearLayout mrp_ll;

    private Product_model product;
    private List<Product_model> product_modelList;
    private DatabaseHandler dbcart;

    private int dbQuantity = 0;
    private int userQty = 0;
    private HashMap<String, String> productMap;

    private SliderView sliderView;
    private SliderAdapter adapter;
    private boolean isDealProduct;
    private Product_adapter adapter_product;
    private View view;

    @BindView(R.id.tv_sp_view_all)
    TextView tv_sp_view_all;

    @BindView(R.id.rv_similar_prods)
    RecyclerView rv_similar_prods;

    @BindView(R.id.tv_simil_prods)
    TextView tv_simil_prods;

    @BindView(R.id.tv_unit_val)
    TextView tv_unit_val;

    @BindView(R.id.tv_total)
    TextView tv_total;

    @BindView(R.id.rg_unit)
    RadioGroup rg_unit;

    public Product_Detail_Fragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        ButterKnife.bind(this, view);

        dbcart = new DatabaseHandler(getContext());

        descr_tv = (TextView) view.findViewById(R.id.tv_desc);
        quantity_tv = (TextView) view.findViewById(R.id.tv_quantity);
        discount_tv = (TextView) view.findViewById(R.id.discount_percent);
        mrp = (TextView) view.findViewById(R.id.mrp_text);
        sellingPrice = (TextView) view.findViewById(R.id.selling_text);
        productName = (TextView) view.findViewById(R.id.product_name);
        add_product_btn = (Button) view.findViewById(R.id.add_btn);
        add_product_iv = (ImageView) view.findViewById(R.id.iv_add);
        rmv_product_iv = (ImageView) view.findViewById(R.id.iv_remove);
        iv_share = (ImageView) view.findViewById(R.id.iv_share);
        add_rmv_ll = (LinearLayout) view.findViewById(R.id.add_rmv_layout);
        mrp_ll = (LinearLayout) view.findViewById(R.id.mrp_layout);

        product = (Product_model) getArguments().getParcelable(Constants.product_model);

        isDealProduct = getArguments().getBoolean(Constants.deals_only);

        ((MainActivity) getActivity()).setToolbarTitle(product.getProduct_name());

        productMap = UtilityHelper.convertListToMap(product);

        setProductDetails();
        setImageSlider();
        getSimilarProducts(product.getProduct_name());

        if (product.getStock() != null && Integer.parseInt(product.getStock()) <= 0) {
            add_product_btn.setText(getString(R.string.tv_out));
            add_product_btn.setClickable(false);
            rg_unit.setVisibility(View.GONE);
        } else {
            add_product_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddRmvProdLayout();
                    //user has selected 0 items till yet
                    userQty = 0;
                    dbQuantity = Integer.parseInt(dbcart.getInCartItemQty(product.getProduct_id()));

                    setQuantity(userQty);
                    setTotalAmount();
                }
            });
        }
        add_product_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userQty++;
                addProduct();
                ((MainActivity) getContext()).setCartCounter("" + dbcart.getCartCount());
                setTotalAmount();
            }
        });

        rmv_product_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  removeProduct();
                ((MainActivity) getContext()).setCartCounter("" + dbcart.getCartCount());
                setTotalAmount();

            }
        });

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityHelper.shareProduct(getContext());
            }
        });

        getRelatedItems(product.getProduct_id());
        seeAllSimilarItems();
        return view;
    }


    private void seeAllSimilarItems()
    {
        tv_sp_view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Fragment fm = new Product_List_fragment();
                args.putString(Constants.isSimilar, getSearchString(product.getProduct_name()));
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }
        });
    }


    public void setQuantity(int userQuantity) {
        int totalQty = dbQuantity +userQuantity;
        quantity_tv.setText(String.valueOf(totalQty));
    }

    private void addProduct() {
      //  quantity = quantity + 1;
        setQuantity(userQty);
        dbcart.setCart(productMap, (float) 1.0);
    }

    private void removeProduct() {
        if (dbQuantity > 0 || userQty > 0) {
            if(userQty == 0)
                { userQty = dbQuantity;
                dbQuantity --;}

            userQty--;
            int totalQty = userQty + dbQuantity;
            quantity_tv.setText(totalQty+"");

            if (totalQty == 0) {
                dbcart.removeItemFromCart(product.getProduct_id());
            }
            else
                dbcart.rmvFromCart(productMap, (float) 1);

        }
    }

    private void showAddRmvProdLayout() {
        add_product_btn.setVisibility(View.GONE);
        add_product_iv.setVisibility(View.VISIBLE);
        rmv_product_iv.setVisibility(View.VISIBLE);
        quantity_tv.setVisibility(View.VISIBLE);
        tv_total.setVisibility(View.VISIBLE);

    }

    private void setImageSlider() {
        ArrayList<SliderItem> sliderItemList = new ArrayList<>();
        int no_of_imgs = product.getProduct_image().size();
        sliderView = view.findViewById(R.id.product_img_slider);

        adapter = new SliderAdapter(getContext());
        sliderView.setSliderAdapter(adapter);

        if (no_of_imgs > 1) {
            sliderView.setIndicatorAnimation(IndicatorAnimationType.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
            sliderView.setScrollTimeInSec(3);
            sliderView.setScrollTimeInSec(3);
            sliderView.setAutoCycle(true);
            sliderView.startAutoCycle();
        }


        for (int i = 0; i < no_of_imgs; i++) {
            SliderItem sliderItem = new SliderItem();
            sliderItem.setName(product.getProduct_name());
            sliderItem.setImageUrl(BaseURL.IMG_PRODUCT_URL + product.getProduct_image().get(i));
            Log.i("images,", BaseURL.IMG_PRODUCT_URL + product.getProduct_image().get(i));

            sliderItemList.add(sliderItem);
        }
        adapter.renewItems(sliderItemList);
    }

    private void setTotalAmount()
    {
        int quantity = Integer.parseInt(quantity_tv.getText().toString());
        int price = Integer.parseInt(product.getPrice());
        if(isDealProduct && !product.getDeal_price().isEmpty())
        {
            price = Integer.parseInt(product.getDeal_price());
        }

        int total = quantity * price;
        tv_total.setText(getString(R.string.total) + " " + inrSymbol +  total);
    }


    private void setProductDetails() {
        if (product != null) {
            productName.setText(product.getProduct_name());
            descr_tv.setText(product.getProduct_description());
            tv_unit_val.setText(product.getUnit_value() + product.getUnit());
            setPrices();
        }
    }

    private void setPrices() {
        float mrpVal = Float.parseFloat(product.getMrp());
        float priceVal = Float.parseFloat(product.getPrice());
        if (isDealProduct) {
            String dealPrice = product.getDeal_price();
            if(!dealPrice.isEmpty())
                priceVal = Float.parseFloat(dealPrice);

            String price = product.getPrice();
            if(!price.isEmpty())
                mrpVal = Float.parseFloat(product.getPrice());
        }

        mrp.setText(inrSymbol + mrpVal);
        mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        sellingPrice.setText(inrSymbol + priceVal);

        setDiscountView(mrpVal, priceVal);

    }

    private void setDiscountView(float mrpVal, float priceVal) {
        int discount = getDiscountPercent(mrpVal, priceVal);
        if (discount > 0) {
            discount_tv.setText(discount + "% OFF");

        } else {
            discount_tv.setVisibility(View.GONE);
            mrp_ll.setVisibility(View.GONE);
        }
    }

    private int getDiscountPercent(float preDiscountPrice, float postDiscountPrice) {
        float difference = preDiscountPrice - postDiscountPrice;
        float val = difference / preDiscountPrice;
        int percent = (int) (val * 100);
        return percent;
    }

    private String getSearchString(String productName) {
        String[] arrOfStr = productName.split(" ");
        String searchStr = arrOfStr[arrOfStr.length - 1];
        return searchStr;
    }

    private void getSimilarProducts(String search_text) {

        // Tag used to cancel the request
        String tag_json_obj = "json_product_req";

        String searchTxt = getSearchString(search_text);
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
                        product_modelList = UtilityHelper.filterSimilarProds(product_modelList,product.getProduct_id());
                        if (product_modelList == null || product_modelList.isEmpty()) {
                            hideSimilarProdsLayout();
                        } else {
                            adapter_product = new Product_adapter(product_modelList, getActivity(), false, R.layout.row_sliding_items);
                            rv_similar_prods.setAdapter(adapter_product);
                            setSimilarItemsList();
                        }

                    } else {
                        hideSimilarProdsLayout();
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

    private void hideSimilarProdsLayout() {
        rv_similar_prods.setVisibility(View.GONE);
        tv_simil_prods.setVisibility(View.GONE);
        tv_sp_view_all.setVisibility(View.GONE);
    }

    private void setSimilarItemsList() {
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        ;
        rv_similar_prods.setLayoutManager(horizontalLayout);
        rv_similar_prods.setItemAnimator(new DefaultItemAnimator());
        // rv_similar_prods.setNestedScrollingEnabled(false);
        // rv_similar_prods.addItemDecoration(new Fragment.Home_fragment.SpacesItemDecoration(5));
        adapter_product.notifyDataSetChanged();
        rv_similar_prods.addItemDecoration(new ItemDecorationColumns(20, product_modelList.size()));


    }


    private void getRelatedItems(String product_id)
    {

        // Tag used to cancel the request
        String tag_json_obj = "json_product_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("productId", product_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_URL, params, new Response.Listener<JSONObject>() {

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

                        if (product_modelList != null || !product_modelList.isEmpty()) {
                            addRadioButtons(product_modelList);
                        }
                        else {

                        }

                    } else {
                        hideSimilarProdsLayout();
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


    //////////////////spinner listner////////////////////
    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        private Product_adapter.MyViewHolder viewHolder;
        private boolean isUserSelection;
        private int row_position;
        private int user_sel_quantity;
        private ArrayList<Product_model> mProducts;
        private Product_adapter product_adapter;
        private Context context;
        private TextView tv_mrp , tv_price, tv_discount;
        private int product_quantity;
        private Deal_Of_Day_model deal_of_day_model;

        public CustomOnItemSelectedListener( ArrayList<Product_model> mProducts,Context context)
        {
            this.mProducts = mProducts;
            product_adapter = new Product_adapter();
            this.context = context;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            if(!isUserSelection)
            {
                isUserSelection = true;
                return;
            }

            //spinnerMap.put(row_position, pos);
            //on initial install, quantity will always be zero in cart
            if(product_quantity == 0)
            {
                product_quantity = 1;
            }

            product = mProducts.get(pos);
            setProductDetails();
            setImageSlider();
            setTotalAmount();
            /* String userChoice = parent.getItemAtPosition(pos).toString();
            String[] splited = userChoice.split("\\s+");
            user_sel_quantity = Integer.parseInt(splited[0]);

            int previous_mrp = 0;
            int new_mrp = 0;
            int previous_selling =0;

            if(mProduct != null) {
                previous_mrp = Integer.parseInt(mProduct.getMrp());
                previous_selling = Integer.parseInt(mProduct.getPrice());

            }

            new_mrp = previous_mrp * user_sel_quantity * product_quantity;
            tv_mrp.setText(context.getString(R.string.currency) + new_mrp);

            int new_sp = previous_selling * user_sel_quantity * product_quantity;
            tv_price.setText(context.getString(R.string.currency) + new_sp);

            int new_discount = product_adapter.getDiscountPercent(new_mrp,new_sp);
            tv_discount.setText(new_discount + "% OFF");*/
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }


    }

    private void addRadioButtons(List<Product_model> items) {
        rg_unit.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < items.size(); i++) {
            Product_model product = items.get(i);
            RadioButton rdBtn = new RadioButton(getContext());
            rdBtn.setText(product.getUnit_value() + " " + product.getUnit());
            setRadioBtnLayout(rdBtn);
            setRadioBtnClick(rdBtn,product);
            rg_unit.addView(rdBtn);
        }
    }

    private void setRadioBtnClick(RadioButton rdBtn, Product_model product_model)
    {
        rdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product = product_model;
                setProductDetails();
                setImageSlider();
                setTotalAmount();
            }
        });
    }

    private void setRadioBtnLayout(RadioButton rdBtn)
    {
        rdBtn.setBackground(getResources().getDrawable(R.drawable.unit_rg_selector));
        rdBtn.setButtonDrawable(android.R.color.transparent);
        rdBtn.setWidth(85);
        rdBtn.setHeight(85);
        rdBtn.setId(View.generateViewId());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 15, 0);
        rdBtn.setGravity(Gravity.CENTER);
        rdBtn.setLayoutParams(params);
    }



}

