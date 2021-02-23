package Fragment;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gropal.in.MainActivity;
import gropal.in.R;
import util.Constants;
import util.PreferenceUtility;

import static android.content.Context.LOCATION_SERVICE;

public class Delivery_Address_Fragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private static String TAG = Delivery_Address_Fragment.class.getSimpleName();
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();
    private int enabledButton;
    ;
    private GoogleMap map;
    private View view;

    @BindView(R.id.mapView)
    MapView mapView;

    @BindView(R.id.ll_btns)
    LinearLayout ll_btns;

    @BindView(R.id.tv_location)
    TextView tv_location;

    @BindView(R.id.btn_change)
    Button btn_change;

    @BindView(R.id.btn_home)
    Button btn_home;

    @BindView(R.id.btn_office)
    Button btn_office;

    @BindView(R.id.btn_shop)
    Button btn_shop;

    @BindView(R.id.btn_others)
    Button btn_others;

    public Delivery_Address_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_delivery_address, container, false);

        ButterKnife.bind(this, view);

        ((MainActivity) getActivity()).setToolbarTitle(getResources().getString(R.string.delivery_add));

        btn_change.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        btn_office.setOnClickListener(this);
        btn_shop.setOnClickListener(this);
        btn_others.setOnClickListener(this);

        setSelectButtonState(btn_home);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        String location = PreferenceUtility.getString(getContext(), Constants.selected_place, "");
        if (location == null || location.isEmpty()) {
            location = PreferenceUtility.getString(getContext(), Constants.current_location, "");
        }
        tv_location.setText(location);
        setMapLocation(location, getString(R.string.your_order_delivery));

    }

    private void
    setMapLocation(String address, String placeName) {
        if (map != null) {
            LatLng latLng = getOfficeLocation(address);
            setMapZoom(latLng);
            showMapMarker(latLng, placeName);
        }

    }

    private void setMapZoom(LatLng latLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
    }

    private Marker showMapMarker(LatLng placeLatLong, String placeName) {

        Marker marker = map.addMarker(new MarkerOptions()
                .position(placeLatLong)
                .title(placeName));

        builder.include(placeLatLong);

        return marker;
    }


    private LatLng getOfficeLocation(String office_address) {
        Geocoder geocoder = new Geocoder(getContext());
        double lat = 0;
        double lng = 0;

        try {
            List addressList = geocoder.getFromLocationName(office_address, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = (Address) addressList.get(0);
                LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);

                lat = address.getLatitude();
                lng = address.getLongitude();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new LatLng(lat, lng);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_change:
                Fragment fm = new Location_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

                break;
            case R.id.btn_home:
                deselectButtons(btn_home);
                setSelectButtonState(btn_home);
                break;
            case R.id.btn_office:
                deselectButtons(btn_office);
                setSelectButtonState(btn_office);
                break;
            case R.id.btn_shop:
                deselectButtons(btn_shop);
                setSelectButtonState(btn_shop);
                break;
            case R.id.btn_others:
                deselectButtons(btn_others);
                setSelectButtonState(btn_others);
                break;


        }
    }

    private void setSelectButtonState(Button button) {
        button.setBackgroundColor(getResources().getColor(R.color.green));
        button.setTextColor(getResources().getColor(R.color.white));
    }

    private void deselectButtons(Button sel_button) {
        View v = null;
        for (int i = 0; i < ll_btns.getChildCount(); i++) {
            v = ll_btns.getChildAt(i);
                Button button = (Button) v;
                if (button != null && button.getId() != sel_button.getId()) {
                    button.setSelected(false);
                    button.setBackground(getResources().getDrawable(R.drawable.btn_background));
                    button.setTextColor(getResources().getColor(R.color.dark_gray));



            }
        }

    }
}

