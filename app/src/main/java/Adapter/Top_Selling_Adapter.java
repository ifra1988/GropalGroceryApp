package Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Config.BaseURL;
import Model.Product_model;
import gropal.in.MainActivity;
import gropal.in.R;
import util.DatabaseHandler;
import Fragment.Product_List_fragment;
import util.UtilityHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rajesh Dabhi on 22/6/2017.
 */

public class Top_Selling_Adapter extends RecyclerView.Adapter<Top_Selling_Adapter.MyViewHolder> {

    private List<Product_model> modelList;
    private Context context;
    private DatabaseHandler dbCart;
    SharedPreferences preferences;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_nmae, selling_price, offer_product_prize, end_time,tv_discount;
        public Button btn_add;
        public ImageView image, iv_cross;
        RelativeLayout showproduct;
        public MyViewHolder(View view) {
            super(view);

            btn_add = (Button) view.findViewById(R.id.btn_add);
            product_nmae = (TextView) view.findViewById(R.id.product_name);
            selling_price = (TextView) view.findViewById(R.id.product_prize);
            offer_product_prize = (TextView) view.findViewById(R.id.offer_product_prize);
            end_time = (TextView) view.findViewById(R.id.end_time);
            tv_discount = (TextView) view.findViewById(R.id.tv_discount);
            showproduct= view.findViewById(R.id.showproduct);
            selling_price.setPaintFlags(selling_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            image = (ImageView) view.findViewById(R.id.iv_icon);
            iv_cross = (ImageView) view.findViewById(R.id.iv_cross);

        }
    }

    public Top_Selling_Adapter(List<Product_model> modelList,Context context) {
        this.context = context;
        this.modelList = modelList;
        dbCart = new DatabaseHandler(this.context);
    }

    @Override
    public Top_Selling_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_deal_of_the_day, parent, false);

        return new Top_Selling_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final Top_Selling_Adapter.MyViewHolder holder, int position) {
        Product_model mList = modelList.get(position);
        preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
        String language=preferences.getString("language","");

        holder.end_time.setVisibility(View.GONE);
        holder.offer_product_prize.setText(context.getResources().getString(R.string.currency) + mList.getMrp());

        String discount = context.getString(R.string.currency) + getDiscountPercent(Float.parseFloat(mList.getPrice()), Float.parseFloat(mList.getDeal_price())) + " %";
        holder.tv_discount.setText(discount);

        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + mList.getProduct_image().get(0))
                .placeholder(R.drawable.icon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.image);
        holder.selling_price.setText(context.getResources().getString(R.string.currency) + mList.getPrice());
        if (language.contains("english")) {
            holder.product_nmae.setText(mList.getProduct_name());
            holder.selling_price.setText( context.getResources().getString(R.string.currency) + mList.getPrice());
        }
        else {
            holder.product_nmae.setText(mList.getProduct_name_arb());
            holder.selling_price.setText(context.getResources().getString(R.string.currency) + mList.getPrice());

        }

        holder.iv_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.iv_cross.setVisibility(View.GONE);
                holder.btn_add.setVisibility(View.VISIBLE);
                removeProduct(position);
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductsList();
            }
        });

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btn_add.setVisibility(View.GONE);
                modelList.get(position).setCategory_id("1");
                modelList.get(position).setStock("1");
                HashMap<String,String> productMap = UtilityHelper.convertListToMap(modelList.get(position));
                holder.iv_cross.setVisibility(View.VISIBLE);
                dbCart.setCart(productMap, (float) 1.0);
                ((MainActivity) context).setCartCounter("" + dbCart.getCartCount());

            }
        });

    }

    private void removeProduct(int position)
    {
        modelList.get(position).setCategory_id("1");
        modelList.get(position).setStock("1");

        dbCart.removeItemFromCart(modelList.get(position).getProduct_id());
        ((MainActivity) context).setCartCounter("" + dbCart.getCartCount());
    }

    private void openProductsList()
    {
        Bundle args = new Bundle();
        Fragment fm = new Product_List_fragment();
        args.putString("cat_deal", "2");
        fm.setArguments(args);
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                .addToBackStack(null).commit();

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    private String getTimeLeft(String startTime, String endTime)  {
        String time_left = "";
        long difference = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = simpleDateFormat.parse(startTime);
            endDate = simpleDateFormat.parse(endTime);

            difference = endDate.getTime() - startDate.getTime();
            if(difference<0)
            {
                Date dateMax = simpleDateFormat.parse("24:00");
                Date dateMin = simpleDateFormat.parse("00:00");
                difference=(dateMax.getTime() -startDate.getTime() )+(endDate.getTime()-dateMin.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int days = (int) (difference / (1000*60*60*24));
        int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);

        time_left = "Ends in " + hours+ "h" + ":" + min + "m";
        return time_left;
    }



    private String getTimeLefth(String endTime, String endDate)
    {
        String time_left = "";
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date future = sdf.parse(endDate + " " +endTime);
            Date now = Calendar.getInstance().getTime();

            long seconds= TimeUnit.MILLISECONDS.toSeconds(future.getTime() - now.getTime());
            long minutes=TimeUnit.MILLISECONDS.toMinutes( future.getTime() - now.getTime());
            long hours=TimeUnit.MILLISECONDS.toHours(future.getTime() - now.getTime());
            long days=TimeUnit.MILLISECONDS.toDays( future.getTime() - now.getTime());

            Log.i("time ",TimeUnit.MILLISECONDS.toDays(now.getTime() - future.getTime()) + " days ago");
            Log.i("time ",TimeUnit.MILLISECONDS.toHours(now.getTime() - future.getTime()) + " hours ago");
            if(seconds<60)
            {
                time_left = seconds + " secs left";
            }
            else if(minutes<60)
            {
                time_left = minutes + " mins left";
            }
            else if(hours<24)
            {
                time_left = hours + " hrs left";
            }
            else
            {
                time_left = days + " days left";
            }
        }
        catch (Exception j){
            j.printStackTrace();
        }

        return time_left;

    }

    private int getDiscountPercent(float preDiscountPrice, float postDiscountPrice)
    {
        float difference =  preDiscountPrice - postDiscountPrice;
        float val = difference/preDiscountPrice;
        int percent = (int) (val * 100);
        return percent;
    }

}




