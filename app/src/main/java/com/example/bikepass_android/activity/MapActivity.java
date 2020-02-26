package com.example.bikepass_android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.bikepass_android.R;
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

/**
 * Created by Dilan on 02.02.2020
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleMap mMap;
    private boolean isGPS = false;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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
        //getLocation();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());
                        // Log.i("latitude:",location.toString());
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(userLoc).title("Hello"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15));
                        Toast.makeText(getApplicationContext(), userLoc.toString(), Toast.LENGTH_SHORT).show();
                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };


        //If device is running SDK<23
        Log.i("message::",Build.VERSION.SDK_INT+"");
      /* if (Build.VERSION.SDK_INT < 23) {

            //Any change of location will be made awere of
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } else { */

            //If we didnt get users permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else {
                //we have permission

                locationManager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                Log.i("latitude:",lastKnownLocation.toString());
                LatLng userLoc = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude() );

               // mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLoc).title("You are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc,15));

            }
        //}
        // Add a marker in Tobb and move the camera
       //  LatLng tobb = new LatLng(39.92102,32.797466 );
         LatLng jandarma=new LatLng(39.9248788,32.8047355);
         LatLng genel_mudurluk=new LatLng(39.9172585,32.8009429);
         LatLng tarim_bakanlıgı=new LatLng(39.9220168,32.7989694);
         LatLng ato_hatira_ormani=new LatLng(39.9128171,32.7964965);

         mMap.addMarker(new MarkerOptions().position(jandarma).title("Busy bike inJandara Genel Mudurlugu").icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_busy)));
         mMap.addMarker(new MarkerOptions().position(genel_mudurluk).title("Off service bike in Orman Genel Mudurlugu").icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_offservice)));
         mMap.addMarker(new MarkerOptions().position(tarim_bakanlıgı).title("Available bike in Tarım Bakaligi!").icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_available)));
         mMap.addMarker(new MarkerOptions().position(ato_hatira_ormani).title("Available bike in Ato Hatıra Ormani!").icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_available)));


    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

}
