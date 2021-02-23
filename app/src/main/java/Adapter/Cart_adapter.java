package Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import Fragment.Product_Detail_Fragment;

import Config.BaseURL;
import Model.Product_model;
import gropal.in.R;
import util.Constants;
import util.DatabaseHandler;
import util.UtilityHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rajesh Dabhi on 26/6/2017.
 */

public class Cart_adapter extends RecyclerView.Adapter<Cart_adapter.ProductHolder> {
    ArrayList<HashMap<String, String>> list;
    Activity activity;
    String Reward;
    SharedPreferences preferences;
    String language;

    int lastpostion;
    DatabaseHandler dbHandler;

    public Cart_adapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        this.list = list;
        this.activity = activity;

        dbHandler = new DatabaseHandler(activity);
        /*common = new CommonClass(activity);
        File cacheDir = StorageUtils.getCacheDirectory(activity);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        imgconfig = new ImageLoaderConfiguration.Builder(activity)
                .build();
        ImageLoader.getInstance().init(imgconfig);*/
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_rv, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, final int position) {
        final HashMap<String, String> map = list.get(position);
        Glide.with(activity)
                .load(UtilityHelper.getImageFromDb(map.get("product_image")))
                .placeholder(R.drawable.icon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_logo);
        preferences = activity.getSharedPreferences("lan", MODE_PRIVATE);
        language=preferences.getString("language","");
            holder.tv_title.setText(map.get("product_name"));

       // holder.tv_reward.setText(map.get("rewards"));
        holder.tv_price.setText(activity.getResources().getString(R.string.tv_pro_price) + map.get("unit_value") + " " +
                map.get("unit")  + " " + map.get("price")+activity.getResources().getString(R.string.currency));
        holder.tv_contetiy.setText(map.get("qty"));
        Double items = Double.parseDouble(dbHandler.getInCartItemQty(map.get("product_id")));
        Double price = Double.parseDouble(map.get("price"));
        holder.tv_total.setText("" + price * items);
      //  holder.tv_reward.setText("" + reward * items);
        holder.iv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductDetails(map);
            }
        });


        holder.iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = 0;
                if (!holder.tv_contetiy.getText().toString().equalsIgnoreCase(""))
                    qty = Integer.valueOf(holder.tv_contetiy.getText().toString());

                if (qty > 0) {
                    qty = qty - 1;
                    holder.tv_contetiy.setText(String.valueOf(qty));
                }
                updateCart(map,holder);

                if (holder.tv_contetiy.getText().toString().equalsIgnoreCase("0")) {
                    dbHandler.removeItemFromCart(map.get("product_id"));
                    list.remove(position);
                    notifyDataSetChanged();

                    updateintent();
                }
            }
        });

        holder.iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int qty = Integer.valueOf(holder.tv_contetiy.getText().toString());
                qty = qty + 1;

                holder.tv_contetiy.setText(String.valueOf(qty));
                updateCart(map,holder);
            }
        });

       /* holder.tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbHandler.setCart(map, Float.valueOf(holder.tv_contetiy.getText().toString()));

                Double items = Double.parseDouble(dbHandler.getInCartItemQty(map.get("product_id")));
                Double price = Double.parseDouble(map.get("price"));
                Double reward = Double.parseDouble(map.get("rewards"));
                holder.tv_total.setText("" + price * items);
                holder.tv_reward.setText("" + reward * items);
             //   holder.tv_total.setText(activity.getResources().getString(R.string.tv_cart_total) + price * items + " " + activity.getResources().getString(R.string.currency));
                updateintent();
            }
        });*/

        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHandler.removeItemFromCart(map.get("product_id"));
                list.remove(position);
                notifyDataSetChanged();

                updateintent();
            }
        });

    }

    private void openProductDetails(HashMap<String, String> map){

        Product_model mProduct = new Product_model(map);
        Bundle args = new Bundle();
        //args.putBoolean(Constants.deals_only,isDeal);
        args.putParcelable(Constants
                .product_model,mProduct);
        Fragment fm = new Product_Detail_Fragment();
        fm.setArguments(args);
        FragmentManager fragmentManager = ((FragmentActivity)activity).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                .addToBackStack(null).commit();

    }



    private void updateCart(HashMap<String,String> map, ProductHolder holder)
    {
        dbHandler.setCart(map, Float.valueOf(holder.tv_contetiy.getText().toString()));

        Double items = Double.parseDouble(dbHandler.getInCartItemQty(map.get("product_id")));
        Double price = Double.parseDouble(map.get("price"));
        holder.tv_total.setText("" + price * items);
        updateintent();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        public TextView tv_title, tv_price, tv_reward, tv_total, tv_contetiy, tv_add,
                tv_unit, tv_unit_value;
        public ImageView iv_logo, iv_plus, iv_minus, iv_remove;

        public ProductHolder(View view) {
            super(view);

            tv_title = (TextView) view.findViewById(R.id.tv_subcat_title);
            tv_price = (TextView) view.findViewById(R.id.tv_subcat_price);
            tv_total = (TextView) view.findViewById(R.id.tv_subcat_total);
           // tv_reward = (TextView) view.findViewById(R.id.tv_reward_point);
            tv_contetiy = (TextView) view.findViewById(R.id.tv_subcat_quantity);
          //  tv_add = (TextView) view.findViewById(R.id.tv_subcat_add);
            iv_logo = (ImageView) view.findViewById(R.id.iv_subcat_img);
            iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
            iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
            iv_remove = (ImageView) view.findViewById(R.id.iv_subcat_remove);

           // tv_add.setText(R.string.tv_pro_update);

        }
    }

    private void updateintent() {
        Intent updates = new Intent("Grocery_cart");
        updates.putExtra("type", "update");
        activity.sendBroadcast(updates);
    }

}

