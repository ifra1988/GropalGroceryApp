package Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Model.Category_model;
import gropal.in.R;
import util.UtilityHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rajesh Dabhi on 22/6/2017.
 */

public class Home_adapter extends RecyclerView.Adapter<Home_adapter.MyViewHolder> {

    private List<Category_model> modelList;
    private Context context;
    String language;
    SharedPreferences preferences;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image;
        public RelativeLayout rl_container;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tv_home_title);
            image = (ImageView) view.findViewById(R.id.iv_home_img);
            rl_container = (RelativeLayout) view.findViewById(R.id.rl_img_container);
        }
    }

    public Home_adapter(List<Category_model> modelList) {
        this.modelList = modelList;
    }

    @Override
    public Home_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_home_rv, parent, false);

        context = parent.getContext();

        return new Home_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Home_adapter.MyViewHolder holder, int position) {
        Category_model mList = modelList.get(position);

        /*Glide.with(context)
                .load(BaseURL.IMG_CATEGORY_URL + mList.getImage())
                .placeholder(R.drawable.icon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.image);*/

        setMulticolourBackground(position,holder.rl_container);
        setImageSrc(holder.image,mList.getTitle());
        preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
        language=preferences.getString("language","");
        holder.title.setText(mList.getTitle());

        Log.i("Home_adapter",mList.getTitle());

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    private void setMulticolourBackground(int position, RelativeLayout iv_container)
    {
        int[] backgroundColors = {
                R.drawable.circle_pink,
                R.drawable.circle_blue,
                R.drawable.circle_green,
                R.drawable.circle_pale_yellow,
                R.drawable.circle_purple
        };

        int index = position % backgroundColors.length;
        Drawable drawable = ContextCompat.getDrawable(context, backgroundColors[index]);
        iv_container.setBackground(drawable);
    }

    private void setImageSrc(ImageView imageView, String itemName)
    {
        int id = UtilityHelper.getCategoriesImg(context,itemName);
        if(id > 0)
            imageView.setImageResource(id);
    }

}

