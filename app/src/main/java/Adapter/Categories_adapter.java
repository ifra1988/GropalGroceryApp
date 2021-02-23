package Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import Config.BaseURL;
import Model.Category_model;
import gropal.in.R;
import util.UtilityHelper;
import Fragment.Product_List_fragment;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rajesh Dabhi on 22/6/2017.
 */

public class Categories_adapter extends RecyclerView.Adapter<Categories_adapter.MyViewHolder> {

    private int noOfItems = 9;
    private boolean doLimit;
    private List<Category_model> category_modelList;
    private Context context;
    String language;
    SharedPreferences preferences;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public ImageView image;
        private RelativeLayout rl_container;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tv_home_title);
            image = (ImageView) view.findViewById(R.id.iv_home_img);
            rl_container = (RelativeLayout) view.findViewById(R.id.rl_container);

        }

    }

    public Categories_adapter(List<Category_model> modelList, boolean doLimit) {
        this.category_modelList = modelList;
        this.doLimit = doLimit;
    }

    @Override
    public Categories_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_category_rv, parent, false);

        context = parent.getContext();

        return new Categories_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Categories_adapter.MyViewHolder holder, int position) {
        Category_model category = category_modelList.get(position);

        preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
        language=preferences.getString("language","");
        holder.title.setText(category.getTitle());

        UtilityHelper.setServerImage(context,BaseURL.IMG_PRODUCT_URL + category.getImage(),holder.image);

        holder.rl_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getid = category.getId();
                String getcat_title = category.getTitle();
                Bundle args = new Bundle();
                Fragment fm = new Product_List_fragment();
                args.putString("cat_id", getid);
                args.putString("cat_title", getcat_title);
                fm.setArguments(args);
                FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }
        });


    }

    private void setImageSrc(ImageView imageView, String itemName)
    {
        int id = UtilityHelper.getCategoriesImg(context,itemName);
        if(id > 0)
            imageView.setImageResource(id);
    }

    @Override
    public int getItemCount() {
        if(doLimit && category_modelList.size() > noOfItems)
        {
            return noOfItems;
        }
        return category_modelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

}

