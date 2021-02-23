package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.Place_History_model;
import butterknife.BindView;
import butterknife.ButterKnife;
import gropal.in.R;

public class PlaceHistoryAdapter extends RecyclerView.Adapter<PlaceHistoryAdapter.PlaceViewHolder> {

    public interface OnPlaceAdapter {
        void onHistoryPlaceClicked(Place_History_model place);
    }

    private Context context;
    private List<Place_History_model> mItems;
    private OnPlaceAdapter mListener;

    public PlaceHistoryAdapter(OnPlaceAdapter mListener) {
        this.mListener = mListener;
        mItems = new ArrayList<Place_History_model>();
    }

    public void setItems(List<Place_History_model> items,Context context) {
        this.context = context;
        mItems = items;
        Collections.reverse(items);
        notifyDataSetChanged();
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_history_item, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        Place_History_model item = getItem(position);

        holder.setOnClickListener(item);
        holder.setPlaceName(item.getName());


    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private Place_History_model getItem(int position) {
        return mItems.get(position);
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.place)
        TextView place;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setPlaceName(String placeName) {
            this.place.setText(placeName);
        }


        private void setOnClickListener(Place_History_model place) {
            itemView.setTag(place);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onHistoryPlaceClicked((Place_History_model) view.getTag());
        }

    }
}

