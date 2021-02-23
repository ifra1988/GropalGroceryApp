package Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

import util.DatabaseHandler;
import util.UtilityHelper;

/**
 * Created by Rajesh Dabhi on 26/6/2017.
 */

public class Product_model implements Parcelable {

    String product_id;
    String product_name;
    String category_id;
    String product_description;
    String deal_price;
    String start_date;
    String start_time;
    String end_date;
    String end_time;
    String price;
    String mrp;
    ArrayList<String> product_image;
    String product_name_arb;
    String product_description_arb;
    String status;
    String in_stock;
    String unit_value;
    String unit;
    String increament;
    String rewards;
    String stock;
    String title;

    public Product_model(HashMap<String, String> map) {
        this.setDeal_price(map.get(DatabaseHandler.COLUMN_DEAL_PRICE));
        this.setPrice(map.get(DatabaseHandler.COLUMN_PRICE));
        this.setMrp(map.get(DatabaseHandler.COLUMN_MRP));
        this.setProduct_id(map.get(DatabaseHandler.COLUMN_ID));
        this.setProduct_name(map.get(DatabaseHandler.COLUMN_NAME));
        this.setProduct_image(UtilityHelper.getImagesFromDb(map.get(DatabaseHandler.COLUMN_IMAGE)));
        this.setUnit(map.get(DatabaseHandler.COLUMN_UNIT));
        this.setUnit_value(map.get(DatabaseHandler.COLUMN_UNIT_VALUE));
        this.setProduct_description(map.get(DatabaseHandler.COLUMN_DESCRIPTION));
    }

    protected Product_model(Parcel in) {
        product_id = in.readString();
        product_name = in.readString();
        category_id = in.readString();
        product_description = in.readString();
        deal_price = in.readString();
        start_date = in.readString();
        start_time = in.readString();
        end_date = in.readString();
        end_time = in.readString();
        price = in.readString();
        mrp = in.readString();
        product_image = in.readArrayList(null);
        product_name_arb = in.readString();
        product_description_arb = in.readString();
        status = in.readString();
        in_stock = in.readString();
        unit_value = in.readString();
        unit = in.readString();
        increament = in.readString();
        rewards = in.readString();
        stock = in.readString();
        title = in.readString();
    }

    public static final Creator<Product_model> CREATOR = new Creator<Product_model>() {
        @Override
        public Product_model createFromParcel(Parcel in) {
            return new Product_model(in);
        }

        @Override
        public Product_model[] newArray(int size) {
            return new Product_model[size];
        }
    };

    public Product_model() {

    }

    public String  getProduct_id() {
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

    public String getProduct_description() {
        return product_description;
    }

    public String getProduct_name_arb() {
        return product_name_arb;
    }

    public void setProduct_name_arb(String product_name_arb) {
        this.product_name_arb = product_name_arb;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getProduct_description_arb() {
        return product_description_arb;
    }

    public void setProduct_description_arb(String product_description_arb) {
        this.product_description_arb = product_description_arb;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ArrayList<String> getProduct_image() {
        return product_image;
    }

    public void setProduct_image(ArrayList<String> product_image) {
        this.product_image = product_image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
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

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
    public String getIn_stock() {
        return in_stock;
    }

    public void setIn_stock(String in_stock) {
        this.in_stock = in_stock;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.product_name);
        dest.writeString(this.product_id);
        dest.writeString(this.price);
        dest.writeString(this.mrp);
        dest.writeList(this.product_image);
        dest.writeString(this.stock);
        dest.writeString(this.status);
        dest.writeString(this.product_description);
        dest.writeString(this.product_description_arb);
        dest.writeString(this.in_stock);
        dest.writeString(this.category_id);
        dest.writeString(this.title);
        dest.writeString(this.rewards);
        dest.writeString(this.increament);
        dest.writeString(this.unit);
        dest.writeString(this.end_date);
        dest.writeString(this.end_time);
        dest.writeString(this.start_date);
        dest.writeString(this.start_time);
        dest.writeString(this.deal_price);
        dest.writeString(this.unit_value);

    }
}
