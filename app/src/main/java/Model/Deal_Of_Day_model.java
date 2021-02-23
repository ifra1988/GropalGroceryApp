package Model;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajesh Dabhi on 24/6/2017.
 */

public class Deal_Of_Day_model implements Parcelable {

    String deal_price;
    String product_id;
    String product_name;
    String product_name_arb;
    String start_date;
    String start_time;
    String end_date;
    String end_time;
    ArrayList<String> product_image;
    String price;
    String status;
    String in_stock;
    String unit_value;
    String unit;
    String increament;
    String rewards;
    String title;
    String product_description;

    protected Deal_Of_Day_model(Parcel in) {
        super();
        deal_price = in.readString();
        product_id = in.readString();
        product_description = in.readString();
        product_name = in.readString();
        product_name_arb = in.readString();
        start_date = in.readString();
        start_time = in.readString();
        end_date = in.readString();
        end_time = in.readString();
        product_image = in.readArrayList(null);
        price = in.readString();
        status = in.readString();
        in_stock = in.readString();
        unit_value = in.readString();
        unit = in.readString();
        increament = in.readString();
        rewards = in.readString();
        title = in.readString();
        stock = in.readString();
        category_id = in.readString();
    }

    public static final Creator<Deal_Of_Day_model> CREATOR = new Creator<Deal_Of_Day_model>() {
        @Override
        public Deal_Of_Day_model createFromParcel(Parcel in) {
            return new Deal_Of_Day_model(in);
        }

        @Override
        public Deal_Of_Day_model[] newArray(int size) {
            return new Deal_Of_Day_model[size];
        }
    };

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    String stock;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    String category_id;


    @SerializedName("sub_cat")
    ArrayList<Category_subcat_model> category_sub_datas;

    public Deal_Of_Day_model(List<Deal_Of_Day_model> deal_of_day_models, Activity activity) {
    }

    public String getProduct_name_arb() {
        return product_name_arb;
    }

    public void setProduct_name_arb(String product_name_arb) {
        this.product_name_arb = product_name_arb;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getDeal_price() {
        return deal_price;
    }

    public void setDeal_price(String deal_price) {
        this.deal_price = deal_price;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public ArrayList<String> getProduct_image() {
        return product_image;
    }

    public void setProduct_image(ArrayList<String> product_image) {
        this.product_image = product_image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIn_stock() {
        return in_stock;
    }

    public void setIn_stock(String in_stock) {
        this.in_stock = in_stock;
    }

    public String getUnit_value() {
        return unit_value;
    }

    public void setUnit_value(String unit_value) {
        this.unit_value = unit_value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIncreament() {
        return increament;
    }

    public void setIncreament(String increament) {
        this.increament = increament;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public ArrayList<Category_subcat_model> getCategory_sub_datas() {
        return category_sub_datas;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(product_id);
        dest.writeString(product_description);
        dest.writeString(product_name);
        dest.writeString(product_name_arb);
        dest.writeString(deal_price);
        dest.writeString(start_date);
        dest.writeString(start_time);
        dest.writeString(end_date);
        dest.writeString(end_time);
        dest.writeList(product_image);
        dest.writeString(price);
        dest.writeString(status);
        dest.writeString(in_stock);
        dest.writeString(unit_value);
        dest.writeString(unit);
        dest.writeString(increament);
        dest.writeString(rewards);
        dest.writeString(title);
        dest.writeString(stock);
        dest.writeString(category_id);
    }
}
