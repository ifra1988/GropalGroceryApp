package Fragment;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Adapter.AutoPlaceAdapter;
import Adapter.PlaceArrayAdapter;
import Adapter.PlaceHistoryAdapter;
import Model.PlaceAutocomplete;
import Model.Place_History_model;
import gropal.in.MainActivity;
import gropal.in.R;
import util.Constants;
import util.PreferenceUtility;
import util.RecyclerTouchListener;
import util.UIUtility;


public class Location_fragment extends Fragment implements PlaceHistoryAdapter.OnPlaceAdapter{

    private static String TAG = Location_fragment.class.getSimpleName();
    private AutoCompleteTextView acTextView;
    private TextView currentLocTextView;
    private TextView clearTextView;
    private RelativeLayout rl_container;
    private String selectedPlace = "";
    List<Place_History_model> placeHistoryList;
    private AutoCompleteTextView autoCompleteTextView;
    private final int AUTOCOMPLETE_REQUEST_CODE = 1;
    View view ;
    private CardView cardResults;
    private PlacesClient placesClient;
    private RecyclerView rv_places, rv_results;
    private PlaceHistoryAdapter placeHistoryAdapter;
    private boolean isUserInput;
    private AutoPlaceAdapter autoAdapter;
    private ArrayList<PlaceAutocomplete> resPlaceAutocompletes = new ArrayList<>();

    public Location_fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_location, container, false);

        rl_container = (RelativeLayout) view.findViewById(R.id.rl_container);
        cardResults = (CardView) view.findViewById(R.id.card_search_view);

        rv_places = (RecyclerView) view.findViewById(R.id.rv_places);
        rv_places.addItemDecoration(new DividerItemDecoration(rv_places.getContext(), DividerItemDecoration.VERTICAL));

        rv_results = (RecyclerView) view.findViewById(R.id.rv_result);
        rv_results.addItemDecoration(new DividerItemDecoration(rv_results.getContext(), DividerItemDecoration.VERTICAL));
        rv_results.setLayoutManager(new LinearLayoutManager(getContext()));

        initializePlacesClient();

        autoAdapter = new AutoPlaceAdapter();

        setPlaceHistoryAdapter(placeHistoryList);
        loadSavedHistory();

        ((MainActivity) getActivity()).setToolbarTitle("");

        autoCompleteTextView = ((MainActivity) getActivity()).getLocationEditText();
        autoCompleteTextView.setThreshold(1);
        UIUtility.setFocus(autoCompleteTextView,getContext());

        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCursorVisibility();            }
        });
        setSearchView();

        clearTextView = (TextView)view.findViewById(R.id.clear);
        clearHistory();

        currentLocTextView = (TextView) view.findViewById(R.id.loc_text);
        currentLocTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentLocation();
               // ((MainActivity) getActivity()).onBackPressed();
                ((MainActivity) getActivity()).setToolbarTitle("");
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        PlaceArrayAdapter mPlaceArrayAdapter = new PlaceArrayAdapter(getContext(), R.layout.place_item);
      //  autoCompleteTextView.setAdapter(mPlaceArrayAdapter);
       // setAutoAdapter();

        return view;
    }

    private void setCursorVisibility()
    {
        int length =  autoCompleteTextView.getText().length();
        autoCompleteTextView.setCursorVisible(true);
        autoCompleteTextView.setSelection(length);

    }

    private void initializePlacesClient()
    {

        if (!Places.isInitialized()) {
            Places.initialize(getContext(), getString(R.string.places_api_key));
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(getContext());

    }

    private void setSearchView()
    {
        //Add a text change listener to implement autocomplete functionality
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(isUserInput == false){
                    //textWatcher is recursive. When editText value is changed from code textWatcher callback gets called. So this variable acts as a flag which tells whether change is user generated or not..Possibly buggy code..:(
                    isUserInput = true;
                    autoCompleteTextView.setSelection(autoCompleteTextView.getText().length());
                    return;
                }
                // optimised way is to start searching for laction after user has typed minimum 3 chars
              //  if (autoCompleteTextView.getText().length() > 2) {

                    resPlaceAutocompletes = new ArrayList<>();
                    rl_container.setVisibility(View.GONE);
                    cardResults.setVisibility(View.VISIBLE);

                    AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                    // Create a RectangularBounds object.
                    // Use the builder to create a FindAutocompletePredictionsRequest.
                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            // Call either setLocationBias() OR setLocationRestriction().
                            //.setLocationRestriction(bounds)
                            .setCountry("in")
                            .setTypeFilter(TypeFilter.REGIONS)
                            .setSessionToken(token)
                            .setQuery(autoCompleteTextView.getText().toString())
                            .build();

                    placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                           // mResult.append(" ").append(prediction.getFullText(null) + "\n");
                            PlaceAutocomplete place = new PlaceAutocomplete(prediction.getPlaceId(),prediction.getPrimaryText(null));
                            resPlaceAutocompletes.add(place);
                            Log.i(TAG, prediction.getPrimaryText(null).toString());
                        }
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        }
                    });
                    isUserInput = false;
                    setAutoAdapter();
                //}
            }

            @Override
            public void afterTextChanged(Editable s) {

                setAutoAdapter();
            }

        });
    }

    private void showSelectedHistory()
    {
        rl_container.setVisibility(View.VISIBLE);
        cardResults.setVisibility(View.GONE);

    }

    private void onPlaceSelected(String selectedPlace)
    {
        autoCompleteTextView.setText(selectedPlace);
        PreferenceUtility.setString(getContext(), Constants.selected_place,selectedPlace);

        setPlaceHistoryList(selectedPlace);
        placeHistoryAdapter.setItems(placeHistoryList, getContext());


        ((MainActivity) getActivity()).onBackPressed();
        //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //fragmentManager.popBackStack();

    }

/*    private void searchPlace()
    {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);
        //autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input).setVisibility(View.GONE);


        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

*/
    private void onSearchCalled() {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields).setCountry("IN") //NIGERIA
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

  /*  @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                //Toast.makeText(getActivity(), "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = place.getAddress();
                onPlaceSelected(place.getName());



            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getActivity(), "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
*/
    private void setAutoAdapter()
    {
        rv_results.setAdapter(autoAdapter);
        autoAdapter.setItems(resPlaceAutocompletes,getContext());

        rv_results.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_results, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getContext(),"ahshdjdjdj",Toast.LENGTH_LONG);
                String selectedPlace = resPlaceAutocompletes.get(position).getName();
                onPlaceSelected(selectedPlace);

                showSelectedHistory();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }


    private void setCurrentLocation()
    {
        String address = ((MainActivity) getActivity()).getLastLocation();
        autoCompleteTextView.setText(address);

        PreferenceUtility.setString(getContext(),Constants.selected_place,address);

    }

    private void clearHistory()
    {
        clearTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtility.removeData(getContext(),Constants.history_list);
            //    PreferenceUtility.removeData(getContext(),PreferenceUtility.selected_item);

                if(placeHistoryList != null) {
                    placeHistoryList.clear();
                    placeHistoryAdapter.setItems(placeHistoryList, getContext());
                }
            }
        });
    }

    private void loadSavedHistory()
    {
        placeHistoryList = getListFromPreferences(Constants.history_list);
        if(placeHistoryList != null && placeHistoryList.size() > 0)
        {
            placeHistoryAdapter.setItems(placeHistoryList, getContext());
        }
    }

    private void setPlaceHistoryList(String selectedPlace) {

        //get saved list
        placeHistoryList = getListFromPreferences(Constants.history_list);
        if (placeHistoryList == null) {

            placeHistoryList = new ArrayList<>();
        }

        Place_History_model place = new Place_History_model();
        place.setName(selectedPlace);

        placeHistoryList.add(place);

        //save list in prefercnces
        PreferenceUtility.saveObjectToSharedPreference(getContext(), Constants.history_list, placeHistoryList);


    }

    private ArrayList<Place_History_model> getListFromPreferences(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Place_History_model>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    private void setPlaceHistoryAdapter(List<Place_History_model> places) {
        placeHistoryAdapter = new PlaceHistoryAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_places.setLayoutManager(layoutManager);
        rv_places.setAdapter(placeHistoryAdapter);
    }

    @Override
    public void onHistoryPlaceClicked(Place_History_model place) {
        if(place != null)
        {
            String selectedPlace = place.getName();
            autoCompleteTextView.setText(selectedPlace);
            PreferenceUtility.setString(getContext(),Constants.selected_place,selectedPlace);

           // ((MainActivity) getActivity()).onBackPressed();
            ((MainActivity) getActivity()).setToolbarTitle("");
            ((MainActivity) getActivity()).onBackPressed();


        }
    }
}
