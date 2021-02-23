package Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import Model.PlaceAutocomplete;
import gropal.in.R;

public class RecyclerBaseAdapter<VH extends RecyclerView.ViewHolder>
        extends BaseAdapter implements Filterable {

    private final AutoPlaceAdapter mAdapter;
    private static final String TAG = "PlaceArrayAdapter";
    private final PlacesClient placesClient;
    //private RectangularBounds mBounds;
    private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();
    public Context context;


    public RecyclerBaseAdapter(AutoPlaceAdapter adapter,Context context) {
        mAdapter = adapter;
        Places.initialize(context, context.getString(R.string.places_api_key));
        placesClient = Places.createClient(context);

    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public int getCount() {
        return mAdapter.getItemCount();
    }

    @Override
    public Object getItem(int position) {
        // not supported
        return null;
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH holder;
        if (convertView == null) {
            holder = (VH) mAdapter.createViewHolder(parent, getItemViewType(position));
            convertView = holder.itemView;
            convertView.setTag(holder);
        } else {
            holder = (VH) convertView.getTag();
        }
        mAdapter.bindViewHolder((AutoPlaceAdapter.PlaceViewHolder) holder, position);
        return holder.itemView;
    }

    private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {

        final ArrayList<PlaceAutocomplete> resultList = new ArrayList<>();

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                // .setLocationBias(bounds)
                //.setLocationBias(mBounds)
                .setCountry("in")
                //   .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build();

        Task<FindAutocompletePredictionsResponse> autocompletePredictions = placesClient.findAutocompletePredictions(request);

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        if (autocompletePredictions.isSuccessful()) {
            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictions.getResult();
            if (findAutocompletePredictionsResponse != null)
                for (com.google.android.libraries.places.api.model.AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                    Log.i(TAG, prediction.getPlaceId());
                    Log.i(TAG, prediction.getPrimaryText(null).toString());

                    resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getFullText(null).toString()));

                }

            return resultList;
        } else {
            return resultList;
        }

    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    // Query the autocomplete API for the entered constraint
                    Log.d(TAG, "Before Prediction");
                    mResultList = getPredictions(constraint);
                    Log.d(TAG, "After Prediction");
                    if (mResultList != null) {
                        // Results
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    //notifyDataSetChanged();
                    AutoPlaceAdapter adapter = new AutoPlaceAdapter();
                    adapter.setItems(mResultList,context);
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;

    }



}