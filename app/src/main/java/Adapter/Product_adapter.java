package Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Fragment.Product_Detail_Fragment;

import Config.BaseURL;
import Model.Product_model;

import gropal.in.MainActivity;
import gropal.in.R;
import util.Constants;
import util.DatabaseHandler;
import util.UtilityHelper;


public class Product_adapter extends RecyclerView.Adapter<Product_adapter.MyViewHolder> {

    private List<Product_model> modelList;
    private Context context;
    private DatabaseHandler dbcart;
    private HashMap<String, String> productMap = new HashMap<>();
    private Product_model mProduct;
    private int user_sel_quantity;
    private boolean isDeal;
    private int resource;
    Map<Integer, Integer> spinnerMap = new HashMap<Integer, Integer>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_title, tv_price, tv_quantity, tv_mrp, tv_discount, tv_unit, tv_total;
        private ImageView iv_logo, iv_plus, iv_remove;
        private LinearLayout  ll_add_rm_prod;
        private Button btn_add;
       // public Spinner sp_unit;

        public MyViewHolder(View view) {

            super(view);

            ll_add_rm_prod = (LinearLayout) view.findViewById(R.id.ll_add_rmv_item);
            tv_total = (TextView) view.findViewById(R.id.tv_total);
            tv_unit = (TextView) view.findViewById(R.id.tv_unit);
            tv_title = (TextView) view.findViewById(R.id.product_name);
            tv_price = (TextView) view.findViewById(R.id.tv_subcat_price);
            tv_mrp = (TextView) view.findViewById(R.id.tv_mrp_price);
            tv_discount = (TextView) view.findViewById(R.id.tv_discount);
            tv_quantity = (TextView) view.findViewById(R.id.tv_quantity);
            btn_add = (Button) view.findViewById(R.id.add_btn);
            iv_logo = (ImageView) view.findViewById(R.id.iv_icon);
            iv_plus = (ImageView) view.findViewById(R.id.iv_add);
            iv_remove = (ImageView) view.findViewById(R.id.iv_remove);
      //      sp_unit = (Spinner)view.findViewById(R.id.sp_unit);

        }

        public void setTotal(String total)
        {
            this.tv_total.setText(total);
        }

        public void setProductName(String productName)
        {
            this.tv_title.setText(productName);
        }

        public void setProductQuantity(String quantity)
        {
            this.tv_quantity.setText(quantity);
        }

        public int getQuantity()
        {
            return Integer.parseInt(this.tv_quantity.getText().toString());
        }

        public int getPrice()
        {
            String priceText = this.tv_price.getText().toString();
            String array[]= priceText.split(context.getString(R.string.currency));
            float price = Float.parseFloat(array[1]);

            return (int)price;
        }

    }

    private void addProduct(MyViewHolder viewHolder, int position)
    {
        mProduct = modelList.get(position);
        ObjectMapper oMapper = new ObjectMapper();
        //productMap = oMapper.convertValue(mProduct, HashMap.class);
        productMap = UtilityHelper.convertListToMap(modelList.get(position));

        int quantity = Integer.parseInt(dbcart.getInCartItemQty(mProduct.getProduct_id()));
        quantity = quantity + 1;
        viewHolder.setProductQuantity(""+quantity);
        dbcart.setCart(productMap,Float.valueOf(quantity));
        ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());

        addToTotal(viewHolder);
    }

    private void removeProduct(MyViewHolder viewHolder, int position)
    {
        mProduct = modelList.get(position);

        productMap = UtilityHelper.convertListToMap(mProduct);

        int quantity = viewHolder.getQuantity();
        if (quantity > 0) {
            quantity = quantity - 1;
            viewHolder.setProductQuantity(""+quantity);

            if(quantity == 0)
                dbcart.removeItemFromCart(mProduct.getProduct_id());
            else
                dbcart.setCart(productMap,Float.valueOf(quantity));

            addToTotal(viewHolder);
        }

    }

    public int getDiscountPercent(float preDiscountPrice, float postDiscountPrice)
    {
        float difference =  preDiscountPrice - postDiscountPrice;
        float val = difference/preDiscountPrice;
        int percent = (int) (val * 100);
        return percent;
    }

    private void openProductDetails(int position){
        mProduct = modelList.get(position);
        Bundle args = new Bundle();
        args.putBoolean(Constants.deals_only,isDeal);
        args.putParcelable(Constants
                .product_model,mProduct);
        Fragment fm = new Product_Detail_Fragment();
        fm.setArguments(args);
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                .addToBackStack(null).commit();

    }

    public Product_adapter()
    {}

    public Product_adapter(List<Product_model> modelList, Context context,boolean isDeal,int resource) {
        this.modelList = modelList;
        dbcart = new DatabaseHandler(context);
        this.isDeal = isDeal;
        this.resource = resource;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        mProduct = modelList.get(position);
        productMap = UtilityHelper.convertListToMap(mProduct);

        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + mProduct.getProduct_image().get(0))
                // .placeholder(R.drawable.icon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_logo);

        holder.setProductName(mProduct.getProduct_name());
        holder.tv_unit.setText(mProduct.getUnit_value() + mProduct.getUnit());
        Log.i("image url", BaseURL.IMG_PRODUCT_URL + mProduct.getProduct_image().get(0));
        if(holder.tv_quantity != null) {
            holder.setProductQuantity(dbcart.getInCartItemQty(mProduct.getProduct_id()));
        }

        //  setQuantitySpinner(holder,position);

        holder.tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductDetails(position);
            }
        });

        holder.iv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductDetails(position);
            }
        });

        float mrp = Float.parseFloat(mProduct.getMrp());
        float sellPrice = Float.parseFloat(mProduct.getPrice());
        if (isDeal) {
            sellPrice = Float.parseFloat(mProduct.getDeal_price());
            mrp = Float.parseFloat(mProduct.getPrice());
        }

        if (holder.iv_plus != null) {
            holder.iv_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addProduct(holder, position);
                }
            });
        }

        if (holder.btn_add != null){

            if (Integer.valueOf(modelList.get(position).getIn_stock())<=0){
                holder.btn_add.setText("Out of Stock");
                hideAddProdLayout(holder);
            }
            else {
                holder.btn_add.setText(context.getResources().getString(R.string.tv_pro_add));
            }


            holder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddProdLayout(holder);

                    //add 1 product on clicking add button
                    addProduct(holder, position);
                }
            });
    }

        if(holder.iv_remove != null) {
            holder.iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeProduct(holder, position);
                    ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());
                }
            });
        }

        setPrices(holder,mrp,sellPrice);

       /* if (Integer.valueOf(modelList.get(position).getIn_stock())<=0){
            holder.btn_add.setText("Out of Stock");
            hideAddProdLayout(holder);
        }
       /* else if (dbcart.isInCart(mList.getProduct_id())) {
            holder.btn_add.setText(context.getResources().getString(R.string.tv_pro_update));
            holder.tv_quantity.setText(dbcart.getInCartItemQty(mList.getProduct_id()));
        } else {
            holder.btn_add.setText(context.getResources().getString(R.string.tv_pro_add));
        }*/

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


 /*   private void setQuantitySpinner(MyViewHolder viewHolder, int row_position)
    {
        CustomOnItemSelectedListener selectedListener = new CustomOnItemSelectedListener(mProduct,context,viewHolder.tv_mrp,viewHolder.tv_price,viewHolder.tv_discount, viewHolder.getQuantity());
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, selectedListener.getSpinnerArray());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.sp_unit.setAdapter(adapter);
        viewHolder.sp_unit.setOnItemSelectedListener(selectedListener);
    }
*/
    private void setDiscountView(float mrpVal, float priceVal,MyViewHolder holder)
    {
        int discount = getDiscountPercent(mrpVal,priceVal);
        if(discount > 0)
        {
            holder.tv_discount.setText(discount + "% OFF");

        }
        else {
            holder.tv_discount.setVisibility(View.GONE);
            holder.tv_mrp.setVisibility(View.GONE);
        }
    }

    private void addToTotal(MyViewHolder holder)
    {
        holder.tv_total.setVisibility(View.VISIBLE);
        //set total price
        int total = (int) (holder.getPrice() * holder.getQuantity());
        holder.setTotal("Total: " + context.getString(R.string.currency) + total);
    }

    private void setPrices(MyViewHolder holder,float mrpVal, float priceVal)
    {
        holder.tv_mrp.setText(context.getString(R.string.currency) + mrpVal);
        holder.tv_mrp.setPaintFlags(holder.tv_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.tv_price.setText(context.getString(R.string.currency) + priceVal);

        setDiscountView(mrpVal,priceVal,holder);

    }

    private void hideAddProdLayout(MyViewHolder holder)
    {
        holder.btn_add.setVisibility(View.VISIBLE);
        holder.btn_add.setClickable(false);
        holder.ll_add_rm_prod.setVisibility(View.GONE);
    }


    private void showAddProdLayout(MyViewHolder holder)
    {
        holder.btn_add.setVisibility(View.GONE);
        holder.ll_add_rm_prod.setVisibility(View.VISIBLE);
        holder.tv_total.setVisibility(View.VISIBLE);
    }



}