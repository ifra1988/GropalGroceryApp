package Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Config.BaseURL;
import Fragment.Product_Detail_Fragment;
import Fragment.Product_List_fragment;
import Model.Product_model;
import gropal.in.MainActivity;
import gropal.in.R;
import util.Constants;
import util.DatabaseHandler;
import util.UtilityHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rajesh Dabhi on 22/6/2017.
 */

public class Deal_OfDay_Adapter extends RecyclerView.Adapter<Deal_OfDay_Adapter.MyViewHolder> {

    private List<Product_model> modelList;
    private Context context;
    private DatabaseHandler dbCart;
    SharedPreferences preferences;
    private int no_of_items = 4;
    private CountDownTimer cTimer = null;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_nmae, selling_price, offer_product_prize, end_time,tv_discount, tv_unit;
        public Button btn_add;
        public ImageView image, iv_cross;
      RelativeLayout showproduct;
        public MyViewHolder(View view) {
            super(view);

            btn_add = (Button) view.findViewById(R.id.btn_add);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
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

    public Deal_OfDay_Adapter(List<Product_model> modelList, Context context) {
        this.context = context;
        this.modelList = modelList;
        dbCart = new DatabaseHandler(this.context);
    }

    @Override
    public Deal_OfDay_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_deal_of_the_day, parent, false);

        return new Deal_OfDay_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final Deal_OfDay_Adapter.MyViewHolder holder, int position) {
        Product_model mList = modelList.get(position);
        preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
        String language=preferences.getString("language","");
        /*
        status=0 ==>expired
        status=1 ==>deals's not started
        status=2 ==>deal is runnung
         */
         if (mList.getStatus().equals("2")) {
            //String timeLeft = getTimeLeft(mList.getStart_time(),mList.getEnd_time());
             //String timeLeft = getDealDurationLeft(mList.getEnd_time(),mList.getEnd_date());
            // holder.end_time.setText(timeLeft);
             startDealTimer(getTimeLeft(mList.getEnd_time(),mList.getEnd_date()),holder.end_time);

        }  if (mList.getStatus().equals("0")) {
           /* holder.btn_add.setVisibility(View.GONE);
            holder.end_time.setBackgroundColor(Color.TRANSPARENT);
            holder.end_time.setText("Expired");
            holder.end_time.setTextColor(context.getResources().getColor(R.color.color_3));*/

        }
        holder.tv_unit.setText(mList.getUnit_value() + mList.getUnit());
        holder.offer_product_prize.setText(context.getResources().getString(R.string.currency) + mList.getDeal_price());

        String discount = getDiscountPercent(Float.parseFloat(mList.getPrice()), Float.parseFloat(mList.getDeal_price())) + " %";
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
                openProductDetails(position);
            }
        });

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btn_add.setVisibility(View.GONE);
                modelList.get(position).setCategory_id("1");
                modelList.get(position).setStock("1");
                ObjectMapper oMapper = new ObjectMapper();
               // HashMap<String,String> productMap = oMapper.convertValue(modelList.get(position), HashMap.class);
                HashMap<String, String> productMap = UtilityHelper.convertListToMap(modelList.get(position));

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

    private void openProductDetails(int position){
        Bundle args = new Bundle();
        args.putBoolean(Constants.deals_only,true);
        args.putParcelable(Constants
                .product_model,modelList.get(position));
        Fragment fm = new Product_Detail_Fragment();
        fm.setArguments(args);
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                .addToBackStack(null).commit();
    }

    private void openProductsList()
    {
        Bundle args = new Bundle();
        Fragment fm = new Product_List_fragment();
        args.putBoolean(Constants.deals_only,true);
        args.putString("cat_deal", "2");
        fm.setArguments(args);
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                .addToBackStack(null).commit();

    }

    @Override
    public int getItemCount() {
        if(modelList.size() > no_of_items){
            return no_of_items;
        }
        else
        {
            return modelList.size();
        }    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private long getTimeLeft(String endTime, String endDate)
    {
        String time_left = "";
        long different = 0;
        long millisToGo = 0;
        try
        {
            SimpleDateFormat sdf_datetime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdf_date = new SimpleDateFormat("dd/MM/yyyy");

            long milliSecondFromTime  = sdf_time.parse(endTime).getTime();
            long milliSecondFromDate = sdf_date.parse(endDate).getTime();
            long milliSecondFromDateTime = sdf_datetime.parse(endDate+" "+endTime).getTime();


            long totalmillisecond = milliSecondFromDate + milliSecondFromTime;

            long currentmilliseconds  = Calendar.getInstance().getTime().getTime();

            long seconds;
            long minutes;
            long hours;
            long days;

            String dataSot = sdf_datetime.format(new Date());
            Date CurrentDate = sdf_datetime.parse(dataSot);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSecondFromDateTime);
            String agoformater = sdf_datetime.format(calendar.getTime());
            Date expDate = sdf_datetime.parse(agoformater);


             different =Math.abs( CurrentDate.getTime() - expDate.getTime()) ;
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            millisToGo = elapsedSeconds*1000+elapsedMinutes*1000*60+elapsedHours*1000*60*60+elapsedDays*1000*60*60*24;

            /*if (elapsedDays == 0) {
                if (elapsedHours == 0) {
                    if (elapsedMinutes == 0) {
                        if (elapsedSeconds < 0) {
                            time_left = "0" + " s";
                        } else {
                            if (elapsedSeconds > 0 && elapsedSeconds < 59) {
                                time_left = "now";
                            }
                        }
                    } else {
                        if(elapsedSeconds > 0)
                            time_left = String.valueOf(elapsedMinutes) + ":" + elapsedSeconds + " left";
                        else
                            time_left = String.valueOf(elapsedMinutes) + " mins left";
                    }
                } else {
                    if(elapsedMinutes > 0)
                        time_left = String.valueOf(elapsedHours) + ":" + String.valueOf(elapsedMinutes) +" left" ;
                    else
                        time_left = String.valueOf(elapsedHours) + " hrs left";
                }

            }
            else {
                if(elapsedHours > 0)
                    time_left = String.valueOf(elapsedDays) + "days " + String.valueOf(elapsedHours) + "hrs left";
                else
                    time_left = String.valueOf(elapsedDays) + "day left";
            }*/

        }
        catch (Exception j){
            j.printStackTrace();
        }

        return millisToGo;

    }



    private long diff;
    private void startDealTimer(long different, TextView tv)
    {

        cTimer = new CountDownTimer(different,1000) {

            @Override
            public void onTick(long millis) {

                int seconds = (int) (millis / 1000) % 60 ;
                int minutes = (int) ((millis / (1000*60)) % 60);
                int hours   = (int) ((millis / (1000*60*60)) % 24);
                int days = (int) ((millis / (1000*60*60*24)) % 7);

                String displayTimeStr = "";
                if(days == 0)
                {
                    if(hours == 0)
                    {
                        if(minutes == 0)
                        {
                            displayTimeStr = String.format("%02dS",seconds);
                        }
                        else
                            displayTimeStr = String.format("%02dM %02dS",minutes,seconds);

                    }
                    else
                        displayTimeStr = String.format("%02dH %02dM %02dS",hours,minutes,seconds);
                }
                else
                {
                    displayTimeStr = String.format("%02dD %02dH %02dM %02dS",days, hours,minutes,seconds);
                }

               // String text = String.format("%02d d %02d h %02d m %02d s",days, hours,minutes,seconds);
                tv.setText(displayTimeStr);
            }

            @Override
            public void onFinish() {
                tv.setText(R.string.expired);
                tv.setBackgroundColor(context.getResources().getColor(R.color.color_3));
            }
        }.start();

    }

    private void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    private int getDiscountPercent(float preDiscountPrice, float postDiscountPrice)
    {
        float difference =  preDiscountPrice - postDiscountPrice;
        float val = difference/preDiscountPrice;
        int percent = (int) (val * 100);
        return percent;
    }

    }




