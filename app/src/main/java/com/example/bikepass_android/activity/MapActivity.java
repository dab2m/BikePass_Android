package com.example.bikepass_android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bikepass_android.R;
import com.example.bikepass_android.adapter.UsageDataListAdapter;
import com.example.bikepass_android.model.UsageData;
import com.example.bikepass_android.network.JSONParser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Dilan on 02.02.2020
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    JSONParser jsonParser;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private GoogleMap mMap;
    private boolean isGPS = false;
    private boolean isContinue = false;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.bikes);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (!isGPS) {
            Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
            return;
        }
        getLocation();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        LatLng userLoc = new LatLng(wayLatitude, wayLongitude);

                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(userLoc).title("You are here"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15));
                        Toast.makeText(getApplicationContext(), userLoc.toString(), Toast.LENGTH_SHORT).show();
                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }

            }
        };

        Bike async = new Bike();
        try {
            Log.i("execute","execute");
            async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //}
        // Add a marker in Tobb and move the camera
       //  LatLng tobb = new LatLng(39.92102,32.797466 );
       /*LatLng jandarma=new LatLng(39.9248788,32.8047355);
         LatLng genel_mudurluk=new LatLng(39.9172585,32.8009429);
         LatLng tarim_bakanlıgı=new LatLng(39.9220168,32.7989694);
         LatLng ato_hatira_ormani=new LatLng(39.9128171,32.7964965);

         mMap.addMarker(new MarkerOptions().position(jandarma).title("Busy bike in Jandara Genel Mudurlugu").icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_busy)));
         mMap.addMarker(new MarkerOptions().position(genel_mudurluk).title("Off service bike in Orman Genel Mudurlugu").icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_offservice)));
         mMap.addMarker(new MarkerOptions().position(tarim_bakanlıgı).title("Available bike in Tarım Bakaligi!").icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_available)));
         mMap.addMarker(new MarkerOptions().position(ato_hatira_ormani).title("Available bike in Ato Hatıra Ormani!").icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_available))); */


    }
    private void getLocation(){
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(MapActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            LatLng userLoc = new LatLng(wayLatitude, wayLongitude);

                            // mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(userLoc).title("You are here"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15));
                        } else {
                            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    }
                });
            }
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isContinue) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(MapActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    wayLatitude = location.getLatitude();
                                    wayLongitude = location.getLongitude();
                                } else {
                                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }


    class Bike extends AsyncTask<String, String,String> {

        @Override
        protected String doInBackground(String[]urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonLocData = new JSONObject();
            try {
                jsonLocData.put("lat",wayLatitude);
                jsonLocData.put("long",wayLongitude);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");
                request = new OutputStreamWriter(connection.getOutputStream());
                request.write(String.valueOf(jsonLocData));
                request.flush();
                request.close();
                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                response = sb.toString().trim();
                JSONObject jObj = new JSONObject(response);
                Log.i("dilan","dilan");
                jsonParser = new JSONParser();
                String jsonString = response;
                if (jsonString != null ) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONObject bikes = jsonObject.getJSONObject("bikes");
                        for (int i = 0; i < bikes.length(); i++) {
                            JSONObject values = bikes.getJSONObject(String.valueOf(i));
                            String bike_name = values.getString("name");
                            String lat = values.getString("lat");
                            String lng = values.getString("long");
                            String status = values.getString("status");

                        }
                    /*        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    mMap.addMarker(new MarkerOptions().position(jandarma).title("Busy bike in Jandara Genel Mudurlugu").icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_busy)));
                                    mMap.addMarker(new MarkerOptions().position(genel_mudurluk).title("Off service bike in Orman Genel Mudurlugu").icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_offservice)));
                                    mMap.addMarker(new MarkerOptions().position(tarim_bakanlıgı).title("Available bike in Tarım Bakaligi!").icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_available)));
                                    mMap.addMarker(new MarkerOptions().position(ato_hatira_ormani).title("Available bike in Ato Hatıra Ormani!").icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_available)));
                                }
                            }); */

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("JSON_RESPONSE", "Empty page resource!");
                }
                isr.close();
                reader.close();
                return "Success";
            } catch (IOException e) {
                // Error
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


    }
}
