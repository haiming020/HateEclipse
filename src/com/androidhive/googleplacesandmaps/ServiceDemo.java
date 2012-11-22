package com.androidhive.googleplacesandmaps;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import com.google.android.maps.*;

import android.location.*;

public class ServiceDemo extends Service {
	LocationManager locationManager;
	private static final String TAG = "Test";
	LocationListener locationListener = null;
	boolean stop = false;
	Thread thread = null;

	@Override
	public void onCreate() {
		Log.i(TAG, "Service onCreate--->");

	}

	
	// constracta
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "Service onStart--->");
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if (location != null) {
					Log.i("superMap", "locationchanged");
				}
				// TODO Auto-generated method stub

			}

			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}
		};

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
		/*Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		GeoPoint gp = getGeoByLocation(location);
		Address mAddr = getAddressByGeoPoint(ServiceDemo.this, gp);
		
		double latitude = location.getLatitude();
		double longtitude = location.getLongitude();*/
		

		thread = new Thread(new Runnable() {
			public void run() {
				while (!stop) {
					Location location = locationManager
							.getLastKnownLocation(LocationManager.GPS_PROVIDER);

					double latitude = location.getLatitude();
					double longtitude = location.getLongitude();
					SendLocationData(longtitude, latitude, "test");
					try {
						Thread.sleep(40000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();

	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "Service onDestroy--->");
		super.onDestroy();
		stop = true;
	}

	public GeoPoint getGeoByLocation(Location location) {
		GeoPoint gp = null;
		try {
			if (location != null) {
				double geoLatitude = location.getLatitude() * 1E6;
				// Log.i(TAG,geoLatitude+"");
				double geoLongtitude = location.getLongitude() * 1E6;
				// Log.i(TAG,geoLongtitude+"");
				gp = new GeoPoint((int) geoLatitude, (int) geoLongtitude);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gp;
	}

	public Address getAddressByGeoPoint(Context context, GeoPoint gp) {
		Address address = null;

		try {
			if (gp != null) {
				Geocoder gc = new Geocoder(context, Locale.US);
				double geoLatitude = (int) gp.getLatitudeE6() / 1E6;
				double geoLongtitude = (int) gp.getLongitudeE6() / 1E6;
				List<Address> lstAddress = gc.getFromLocation(geoLatitude,
						geoLongtitude, 1);
				if (lstAddress.size() > 0) {
					address = lstAddress.get(0);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return address;
	}

	private void SendLocationData(double longtitude1, double latitude1,
			String username) {
		SendToServer(GetJSON(longtitude1, latitude1, username));
	}

	private String GetJSON(double longtitude, double latitude, String username) {
		String result = "";
		try {
			JSONObject info = new JSONObject();
			JSONArray location = new JSONArray();
			location.put(longtitude).put(latitude);
			info.put("location", location);
			info.put("username", username);
			info.put("password", "yuanzhifei89");
			Date curDate = new Date(System.currentTimeMillis());
			info.put("time",
					new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(curDate));
			result = info.toString();
		} catch (JSONException ex) {

			throw new RuntimeException(ex);
		}

		return result;

	}

	private void SendToServer(String message) {
		try {
			SocketChannel client = SocketChannel.open();
			// 10.0.2.2 50.17.51.190
			client.socket().connect(
					new InetSocketAddress("107.22.43.202", 9888), 5000);
			client.write(ByteBuffer.wrap(message.getBytes()));
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}