package Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;

import Config.BaseURL;
import Model.SliderItem;
import butterknife.BindView;
import gropal.in.R;

import static android.content.Context.MODE_PRIVATE;

public class ImagesListViewAdapter extends RecyclerView.Adapter<ImagesListViewAdapter.ImageHolder> {
    Context context;
    ArrayList<SliderItem> items;
    LayoutInflater inflater;

    public ImagesListViewAdapter(Context context, ArrayList<SliderItem> item) {
        this.context = context;
        this.items = item;
        inflater = ((Activity) context).getLayoutInflater();
    }


    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageHolder holder, final int position) {

        String url = items.get(position).getImageUrl();

        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.icon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.iv_icon);

        }

    }

}


