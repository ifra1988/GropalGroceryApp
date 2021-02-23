package util;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.android.volley.BuildConfig;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import Config.BaseURL;
import Model.Deal_Of_Day_model;
import Model.Product_model;
import gropal.in.R;

public class UtilityHelper {

    public static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";

    public static void shareProduct(Context context)
    {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getApplicationInfo().loadLabel(context.getPackageManager()).toString());
            String shareMessage= "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=gropal.in"+"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    public static int getCategoriesImg(Context context, String itemName)
    {
        String[] arrOfStr = itemName.split(",");
        String imgName = arrOfStr[0].toLowerCase();
        int id = context.getResources().getIdentifier(imgName,"drawable", context.getPackageName());
        return id;
    }

    public static List<Product_model> filterSimilarProds(List<Product_model> items, String id)
    {
        ListIterator<Product_model> iter = items.listIterator();

        while(iter.hasNext()){
            Product_model item = iter.next();
            if(item.getProduct_id().equalsIgnoreCase(id)){
                iter.remove();
            }
        }

        return items;

    }

    /*status 0 = expired
          status 2 = deal is running
          status 1 = deal not started
         */
    public static List<Deal_Of_Day_model> removedExpiredDeals(List<Deal_Of_Day_model> items)
    {
        ListIterator<Deal_Of_Day_model> iter = items.listIterator();

        while(iter.hasNext()){
            Deal_Of_Day_model item = iter.next();
            if(item.getStatus().equals("0") || item.getStatus().equals("1")){
                iter.remove();
            }
        }

        //  setDealViewSize(items.size());
        return items;
    }


    /*status 0 = expired
          status 2 = deal is running
          status 1 = deal not starteddd
         */
    public static List<Product_model> removedExpiredProduct(List<Product_model> items)
    {
        ListIterator<Product_model> iter = items.listIterator();
        while(iter.hasNext()){
            Product_model item = iter.next();
            if(item.getStatus().equals("0") || item.getStatus().equals("1")){
                iter.remove();
            }
        }
        return items;
    }

    public static void setServerImage(Context context, String img_url, ImageView iv)
    {
        Glide.with(context)
                .load(img_url)
                 .placeholder(R.drawable.icon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(iv);

    }

    public static String getImageFromDb(String imgArr)
    {
        String[] arrOfStr = imgArr.split(",");
        String img = arrOfStr[0];
        img = arrOfStr[0].replace("[","");
        if(img.contains("]"))
            img = arrOfStr[0].replace("]","");

        return BaseURL.IMG_PRODUCT_URL + img;
    }

    public static ArrayList<String> getImagesFromDb(String imgArr)
    {
        ArrayList<String> images = new ArrayList<>();
        String[] arrOfStr = imgArr.split(",");
        for(int i = 0; i < arrOfStr.length; i++)
        {
            String img = arrOfStr[i];
            if(img.contains("]"))
                img = arrOfStr[i].replace("]","");
            else if(img.contains("["))
                img = arrOfStr[i].replace("[","");

            images.add(img.replace(" ",""));
        }

        return images;
    }


    public static HashMap<String,String> convertListToMap(Product_model modelList )
   {
       HashMap<String, String> map = new HashMap<>();

       map.put("product_id", modelList.getProduct_id());
       map.put("product_name", modelList.getProduct_name());
       map.put("category_id", "1");
       map.put("product_description", modelList.getProduct_description());
       map.put("deal_price", modelList.getDeal_price());
       map.put("start_date", modelList.getStart_date());
       map.put("start_time", modelList.getStart_time());
       map.put("end_date", modelList.getEnd_date());
       map.put("end_time", modelList.getEnd_time());
       map.put("price", modelList.getPrice());
       map.put("product_image", String.valueOf(modelList.getProduct_image()));
       map.put("status", modelList.getStatus());
       map.put("in_stock", modelList.getIn_stock());
       map.put("unit_value", modelList.getUnit_value());
       map.put("unit", modelList.getUnit());
       map.put("increament", modelList.getIncreament());
       map.put("rewards", modelList.getRewards());
       map.put("stock", "1");
       map.put("title", modelList.getTitle());
       map.put("mrp", modelList.getMrp());

       return map;

   }

    public static HashMap<String,String> convertListToMap(Deal_Of_Day_model modelList )
    {
        HashMap<String, String> map = new HashMap<>();

        map.put("product_id", modelList.getProduct_id());
        map.put("product_name", modelList.getProduct_name());
        map.put("category_id", modelList.getCategory_id());
        map.put("product_description", modelList.getProduct_description());
        map.put("deal_price", modelList.getDeal_price());
        map.put("start_date", modelList.getStart_date());
        map.put("start_time", modelList.getStart_time());
        map.put("end_date", modelList.getEnd_date());
        map.put("end_time", modelList.getEnd_time());
        map.put("price", modelList.getPrice());
        map.put("product_image", String.valueOf(modelList.getProduct_image()));
        map.put("status", modelList.getStatus());
        map.put("in_stock", modelList.getIn_stock());
        map.put("unit_value", modelList.getUnit_value());
        map.put("unit", modelList.getUnit());
        map.put("increament", modelList.getIncreament());
        map.put("rewards", modelList.getRewards());
        map.put("stock", modelList.getStock());
        map.put("title", modelList.getTitle());

        return map;
    }

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    public static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }

    /**
     * Returns the {@code location} object as a human readable string.
     * @param location  The {@link Location}.
     */
    public static String getLocationText(Location location) {
        return location == null ? "Unknown location" :
                "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }

    /*static String getLocationTitle(Context context) {
        return context.getString(R.string.location_updated,
                DateFormat.getDateTimeInstance().format(new Date()));
    }*/
}
