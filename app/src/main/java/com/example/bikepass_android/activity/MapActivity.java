package com.example.bikepass_android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikepass_android.R;
import com.example.bikepass_android.network.*;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import com.example.bikepass_android.directionhelpers.*;


/**
 * Created by Dilan on 02.02.2020
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback, GoogleMap.OnMarkerClickListener {
    JSONParser jsonParser;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private GoogleMap mMap;
    private boolean isGPS = false;
    private boolean isContinue = false;
    ArrayList<Marker> marker = new ArrayList<Marker>();
    LatLng userLoc;
    private Polyline currentPolyline;
    final ArrayList<LatLng> loc_list = new ArrayList<LatLng>();
    final ArrayList<Bike> status = new ArrayList<Bike>();
    Button getPath;
    Dialog myDialog;
    int whichBike = -1;

    private ImageView imgMyLocation;
    private MapFragment mapFragment;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.bikes);
        mapFragment.getMapAsync(this);
        //getPath.setOnClickListener(this);
        myDialog = new Dialog(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        imgMyLocation = (ImageView) findViewById(R.id.imgMyLocation);
        imgMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyLocation();
            }
        });


        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });


    }

    private void getMyLocation() {
        LatLng latLng = new LatLng(Double.parseDouble(String.valueOf(userLoc.latitude)), Double.parseDouble(String.valueOf(userLoc.longitude)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdate);
    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

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
        setRepeatingAsyncTask();

    }

    private void setRepeatingAsyncTask() {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            BikeReq async = new BikeReq();
                            try {

                                async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
                                //mMap.clear();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 30 * 1000);  // interval of one 30 seconds

    }

    private String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            //Toast.makeText(getApplicationContext(), "Address=>" + add, Toast.LENGTH_SHORT).show();
            return add;
            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return null;
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }


    private void getLocation() {
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
                            userLoc = new LatLng(wayLatitude, wayLongitude);

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


    public void setDialog(final int bikenum) {

        //String url=getUrl(marker.get(4).getPosition(),marker.get(5).getPosition(),"walking");
        //new FetchURL(MapActivity.this).execute(url,"walking");
        int distance = (int) meterDistanceBetweenPoints((float) userLoc.latitude, (float) userLoc.longitude, (float) marker.get(bikenum).getPosition().latitude, (float) marker.get(bikenum).getPosition().longitude);
        TextView txtclose;
        TextView bikestats;
        Button btnreserved;
        TextView dist;
        TextView log;
        myDialog.setContentView(R.layout.custompopup);
        bikestats = myDialog.findViewById(R.id.bikestatus);
        bikestats.setText(status.get(bikenum).getStatus_name());
        dist = myDialog.findViewById(R.id.dist);
        dist.setText(distance + " m");
        log=myDialog.findViewById(R.id.log);
        log.setText("This bike is not avaliable right now!");
        btnreserved = (Button) myDialog.findViewById(R.id.btnrzrv);
        if (status.get(bikenum).getStatus_code() != 1) {
            //btnreserved.setEnabled(false);
            btnreserved.setVisibility(View.GONE);
        } else {
            log.setVisibility(View.GONE);
            btnreserved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SetBikeRez async = new SetBikeRez(status.get(bikenum).getId());
                        try {

                            async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
                            mMap.clear();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {

                    }
                }
            });

        }

        txtclose = (TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    @Override
    public boolean onMarkerClick(Marker myMarker) {

        for (Marker marker : marker) {
            whichBike++;
            if (marker.equals(myMarker)) {
                String url = getUrl(userLoc, marker.getPosition(), "walking");
                new FetchURL(MapActivity.this).execute(url, "walking");
                setDialog(whichBike);
            }

        }
        whichBike=-1;
        return true;
    }

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f / Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }


    class SetBikeRez extends AsyncTask<String, String, String> {

        int bikeid;

        public SetBikeRez(int bikeid) {
            super();
            this.bikeid = bikeid;
        }

        @Override
        protected String doInBackground(String[] urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonLocData = new JSONObject();
            try {
                jsonLocData.put("bikeId", this.bikeid);

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
                // Response from server after rezerving process will be stored in response variable.
                response = sb.toString().trim();
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Request failed!", Toast.LENGTH_SHORT).show();
                }
                isr.close();
                reader.close();
                return "Success";
            } catch (IOException e) {
                // Error
                e.printStackTrace();

                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    class BikeReq extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String[] urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonLocData = new JSONObject();
            try {
                jsonLocData.put("lat", wayLatitude);
                jsonLocData.put("long", wayLongitude);

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
                jsonParser = new JSONParser();
                String jsonString = response;
                if (jsonString != null) {
                    try {

                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONObject bikes = jsonObject.getJSONObject("bikes");

                        for (int i = 0; i < bikes.length(); i++) {
                            boolean flag=true;
                            int count=0;
                            JSONObject values = bikes.getJSONObject(String.valueOf(i));

                            for(Bike bike:status){
                                if(bike.getId()== Integer.parseInt(values.getString("name").substring(4))){
                                    flag=false;
                                    if(bike.getStatus_code()!=Integer.parseInt(values.getString("status"))){
                                       update_bike(bike,values.getString("status"));
                                    }
                                    break;
                                }
                                count++;
                            }
                            if(flag) {
                                add_bike(values.getString("status"),values.getString("name").substring(4));
                                loc_list.add(new LatLng(Double.parseDouble(values.getString("lat")), Double.parseDouble(values.getString("long"))));
                            }
                            else{
                                loc_list.remove(count);
                                loc_list.add(count,new LatLng(Double.parseDouble(values.getString("lat")), Double.parseDouble(values.getString("long"))));
                            }

                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mMap.addMarker(new MarkerOptions().position(userLoc).title("You are here"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15));

                                for (int i = 0; i < loc_list.size(); i++) {
                                    boolean flag=true;
                                    for(Marker marker:marker) {
                                        if (marker.getPosition().latitude == loc_list.get(i).latitude && marker.getPosition().longitude == loc_list.get(i).longitude) {
                                            flag = false;
                                        }

                                    }
                                    if(flag) {
                                        marker.add(mMap.addMarker(new MarkerOptions().position(loc_list.get(i)).title(status.get(i).getStatus_name() + " bike in  " + getAddress(loc_list.get(i).latitude, loc_list.get(i).longitude)).icon(BitmapDescriptorFactory.fromResource(status.get(i).getLogo_name()))));
                                    }
                                }

                            }
                        });

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

        public void update_bike(Bike bike,String status){
            if (status.equals("0")) {
                bike.setStatus_code(0);
                bike.setLogo_name(R.drawable.bike_offservice);
                bike.setStatus_name("Off service");
            } else if (status.equals("1")) {
                bike.setStatus_code(1);
                bike.setLogo_name(R.drawable.bike_available);
                bike.setStatus_name("Available");
            } else if (status.equals("2")) {
                bike.setStatus_code(2);
                bike.setLogo_name(R.drawable.bike_busy);
                bike.setStatus_name("Busy");
            } else if (status.equals("3")) {
                bike.setStatus_code(3);
                bike.setLogo_name(R.drawable.bike_busy);
                bike.setStatus_name("Rezerved");
            }
        }

        public void add_bike(String status_code,String name){
            if (status_code.equals("0")) {
                Bike bike = new Bike(0, "Off service", R.drawable.bike_offservice, Integer.parseInt(name));
                status.add(bike);
            } else if (status_code.equals("1")) {
                Bike bike = new Bike(1, "Available", R.drawable.bike_available, Integer.parseInt( name));
                status.add(bike);
            } else if (status_code.equals("2")) {
                Bike bike = new Bike(2, "Busy", R.drawable.bike_busy, Integer.parseInt(name));
                status.add(bike);
            } else if (status_code.equals("3")) {
                Bike bike = new Bike(3, "Rezerved", R.drawable.bike_busy, Integer.parseInt(name));
                status.add(bike);
            }
        }

    }
}

