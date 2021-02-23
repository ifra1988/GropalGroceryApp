package Fragment;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import gropal.in.MainActivity;
import gropal.in.R;

import static android.content.Context.LOCATION_SERVICE;

public class Contact_Us_Fragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private static String TAG = Contact_Us_Fragment.class.getSimpleName();
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();
    private TextView tv_ho_address, tv_bo_address;
    private MapView mapView;
    private GoogleMap map;
    private LinearLayout ll_ho,ll_bo;

    public Contact_Us_Fragment() {
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
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        ((MainActivity) getActivity()).setToolbarTitle(getResources().getString(R.string.nav_contact));

        tv_ho_address = view.findViewById(R.id.tv_ho_address);
        tv_bo_address = view.findViewById(R.id.tv_bo_address);
        ll_ho = view.findViewById(R.id.ll_ho);
        ll_bo = view.findViewById(R.id.ll_bo);

        ll_ho.setOnClickListener(this);
        ll_bo.setOnClickListener(this);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
       // map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        setMapLocation(tv_ho_address.getText().toString(),getString(R.string.head_off));

    }

    private void
    setMapLocation(String address,String placeName) {
        if (map != null) {
            LatLng latLng = getOfficeLocation(address);
            setMapZoom(latLng);
            showMapMarker(latLng,placeName);
        }

    }

    private String getHeadOfficeAddress()
    {
        return tv_ho_address.getText().toString();
    }

    private String getBranchOfficeAddress()
    {
        return tv_bo_address.getText().toString();
    }

    private void setMapZoom(LatLng latLng)
    {
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
            List addressList = geocoder.getFromLocationName(office_address,1);
            if(addressList!=null && addressList.size() > 0) {
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
        return new LatLng(lat,lng);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.ll_ho:
                setMapLocation(getHeadOfficeAddress(),getString(R.string.head_off));
                break;
            case R.id.ll_bo:
                setMapLocation(getBranchOfficeAddress(),getString(R.string.branch_off));
                break;

        }
    }
}

