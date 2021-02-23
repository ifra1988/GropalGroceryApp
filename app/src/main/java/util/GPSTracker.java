package util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GPSTracker implements LocationListener {
	private final Context mContext;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	Location location = null;
	double latitude;
	double longitude;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.mContext = context;
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(Context.LOCATION_SERVICE);

			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
				mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				
			} else {
			    if(!hasPermissions())
                {
                    requestPermisssion();
                }

					this.canGetLocation = true;
					if (isNetworkEnabled) {
						locationManager.requestLocationUpdates(
								LocationManager.NETWORK_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("Network", "Network Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
							if (location != null) {
							    saveLocation();
							}
						}
					}
					if (isGPSEnabled) {
						if (location == null) {
							locationManager.requestLocationUpdates(
									LocationManager.GPS_PROVIDER,
									MIN_TIME_BW_UPDATES,
									MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
							Log.d("GPS", "GPS Enabled");
							if (locationManager != null) {
								location = locationManager
										.getLastKnownLocation(LocationManager.GPS_PROVIDER);
								if (location != null) {
								    saveLocation();
								}
							}
						}
					}
				}

		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return location;
	}

	private void saveLocation()
    {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            PreferenceUtility.saveObjectToSharedPreference(mContext, Constants.current_location,location);
    }

	public String getAddress()
	{
		/*location = PreferenceUtility.getSavedObjectFromPreference(mContext,"location",Location.class);
		Log.i("location", "location: " + location);*/

		if(location == null)
			location = getLocation();
		String address = "";
		if(location != null) {
			Geocoder geocoder;
			List<Address> addresses = null;
			geocoder = new Geocoder(mContext, Locale.getDefault());

			try {
				addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (addresses != null) {
				address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
				String city = addresses.get(0).getLocality();
				String state = addresses.get(0).getAdminArea();
				String country = addresses.get(0).getCountryName();
				String postalCode = addresses.get(0).getPostalCode();

				Log.i("address", "getAddress: " + address);
				PreferenceUtility.setString(mContext,Constants.current_location,address);

			}
		}
		return address;
	}


	public String getAddress(Location location)
	{
		/*location = PreferenceUtility.getSavedObjectFromPreference(mContext,"location",Location.class);
		Log.i("location", "location: " + location);*/

		String address = "";
		if(location != null) {
			Geocoder geocoder;
			List<Address> addresses = null;
			geocoder = new Geocoder(mContext, Locale.getDefault());

			try {
				addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (addresses != null && !addresses.isEmpty()) {
				address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
				String city = addresses.get(0).getLocality();
				String state = addresses.get(0).getAdminArea();
				String country = addresses.get(0).getCountryName();
				String postalCode = addresses.get(0).getPostalCode();

				Log.i("address", "getAddress: " + address);
				PreferenceUtility.setString(mContext,Constants.current_location,address);

			}
		}
		return address;
	}

	private void requestPermisssion()
    {
        ActivityCompat.requestPermissions((Activity)mContext, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION },1);
    }

	private boolean hasPermissions()
	{
		if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
		//	UIUtility.showShortToast("First enable LOCATION ACCESS in settings.",mContext);
			return false;
		}

		return true;
	}

	public void stopUsingGPS() {
		if (locationManager != null) {
		//	locationManager.removeUpdates(com.places.findapizza.utilities.GPSTracker.this);
		}
	}

	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}
		return latitude;
	}

	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}
		return longitude;
	}

	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		latitude = arg0.getLatitude();
		longitude = arg0.getLongitude();

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
}