package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.PlaceAutocomplete;
import Model.Place_History_model;
import butterknife.BindView;
import butterknife.ButterKnife;
import gropal.in.R;

public class AutoPlaceAdapter extends RecyclerView.Adapter<AutoPlaceAdapter.PlaceViewHolder> {

    public interface OnPlaceAdapter {
        void onPlaceClicked(PlaceAutocomplete place);
    }

    private Context context;
    private List<PlaceAutocomplete> mItems;
    private OnPlaceAdapter mListener;

    public AutoPlaceAdapter() {
     //   this.mListener = mListener;
        mItems = new ArrayList<PlaceAutocomplete>();
    }

    public void setItems(ArrayList<PlaceAutocomplete> items, Context context) {
        this.context = context;
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_history_item, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        PlaceAutocomplete item = getItem(position);

        holder.setPlaceName(item.getName());

    }

    @Override
    public int getItemCount() {

        return mItems.size();
    }

    private PlaceAutocomplete getItem(int position) {
        return mItems.get(position);
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.place)
        TextView place;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setPlaceName(String placeName) {
            this.place.setText(placeName);
        }


        private void setOnClickListener(PlaceAutocomplete place) {
            itemView.setTag(place);
            Toast.makeText(context,"ahshdjdjdj",Toast.LENGTH_LONG);

        }


    }
}

