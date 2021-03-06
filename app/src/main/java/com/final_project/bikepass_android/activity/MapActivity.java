package com.final_project.bikepass_android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.final_project.bikepass_android.R;
import com.final_project.bikepass_android.network.*;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import com.final_project.bikepass_android.activity.*;
import com.final_project.bikepass_android.directionhelpers.*;


/**
 * Created by Dilan on 02.02.2020
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
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
    final ArrayList<Bike> status = new ArrayList<Bike>();
    Button seecards;
    Dialog myDialog;
    int whichBike = -1;
    boolean clicked = false;
    MapFragment mapFragment;
    SharedPreferences sharedpreferences;
    String user_name;
    int has_request = -1;
    double user_request_lat;
    double user_request_long;
    boolean has_created;
    Animation anim;
    Dialog popupdialog;
    Dialog canceldialog;
    Marker request;
    Button tasks;
    ImageView report_problem;
    Dialog infodialog;
    private ImageButton returnBikes;
    String user_addr;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        sharedpreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        user_name = sharedpreferences.getString("username", "");
        anim = new AlphaAnimation(0.0f, 1.0f);
        tasks = (Button) findViewById(R.id.tasks);
        tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, MapRequests.class));
            }
        });
        has_created=true;
        has_request = -1;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.bikes);
        mapFragment.getMapAsync(this);
        myDialog = new Dialog(this);
        popupdialog = new Dialog(this);
        canceldialog = new Dialog(this);
        infodialog = new Dialog(this);

        returnBikes = (ImageButton) findViewById(R.id.returnbikes);
        returnBikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapActivity.this, RentBikeActivity.class));
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        seecards = findViewById(R.id.seecards);
        seecards.setVisibility(View.GONE);
        seecards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = true;
                showInfoDıalog("Please click the position you want to set request");
            }
        });
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
        report_problem = (ImageView) findViewById(R.id.report_problem);
        report_problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ReportDamage.class);
                intent.putExtra("username",user_name);
                intent.putExtra("useraddr",user_addr);
                startActivity(intent);
            }
        });
    }


    public void showInfoDıalog(String title) {
        endAnimation();
        popupdialog.setContentView(R.layout.set_request_popup);
        TextView txttoclose = (TextView) popupdialog.findViewById(R.id.close);
        txttoclose.setText("X");
        txttoclose.setTextColor(Color.BLACK);
        txttoclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupdialog.dismiss();
            }
        });
        popupdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textmessage = (TextView) popupdialog.findViewById(R.id.textviewmessage);
        textmessage.setText(title);
        Window window = popupdialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        window.getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        popupdialog.show();
        // Hide after some seconds
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (popupdialog.isShowing()) {
                    popupdialog.dismiss();
                }
            }
        };
        popupdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 5000);
    }

    public void cancelRequestDialog(String address) {

        canceldialog.setContentView(R.layout.cancel_request_popup);
        canceldialog.setCancelable(false);
        TextView txttoclose = (TextView) canceldialog.findViewById(R.id.close);
        txttoclose.setText("X");
        txttoclose.setTextColor(Color.BLACK);
        txttoclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
            }
        });
        TextView textmessage = (TextView) canceldialog.findViewById(R.id.textviewmessage);
        textmessage.setText("You have a request created in "+address+".Do you want tou continue or cancel the request?");
        textmessage.setTextColor(Color.BLACK);
        Window window = canceldialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        window.getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        Button continue_reuqest;
        continue_reuqest = (Button) canceldialog.findViewById(R.id.cont);
        continue_reuqest.setTextColor(Color.BLACK);
        continue_reuqest.setText("Continue");
        Button cancel_reuqest;
        cancel_reuqest = (Button) canceldialog.findViewById(R.id.cancelreq);
        cancel_reuqest.setTextColor(Color.BLACK);
        cancel_reuqest.setText("Cancel");
        canceldialog.show();
        continue_reuqest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
            }
        });
        cancel_reuqest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                cancelRequest();
                has_created=true;

            }
        });
    }
    private void cancelRequest() {
        try {

            CancelRequest async = new CancelRequest();
            try {
                String result = async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
                if (result.equals("0")) {
                    request.remove();
                    //cagır
                    // showSetRequest();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {

        }

    }

    @SuppressLint("WrongConstant")
    private void manageBlinkEffect() {
        seecards.setVisibility(View.VISIBLE);
        anim.setDuration(350); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        seecards.startAnimation(anim);
    }


    public void endAnimation() {
        anim.cancel();
        seecards.setVisibility(View.GONE);
    }

    private void getMyLocation() {
        LatLng latLng = new LatLng(Double.parseDouble(String.valueOf(userLoc.latitude)), Double.parseDouble(String.valueOf(userLoc.longitude)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdate);

        SharedPreferences.Editor editor = getSharedPreferences("LOCATION", MODE_PRIVATE).edit();
        editor.putFloat("lat", (float) userLoc.latitude);
        editor.putFloat("lng", (float) userLoc.longitude);
        editor.apply();
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMyLocationButtonClickListener(MapActivity.this);
        mMap.setOnMyLocationClickListener(MapActivity.this);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //GoogleMapOptions option=new GoogleMapOptions().zoomControlsEnabled(true);
        //SupportMapFragment fragmen
        mMap.setMyLocationEnabled(true);
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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                if (clicked) {

                    endAnimation();

                    String result=makeRequest(latLng.latitude + "", latLng.longitude + "");

                    if (!result.equals("1")) {

                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(latLng);

                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                        request = mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title("Request created in ").icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_requested)));

                        marker.add(request);

                        request.showInfoWindow();

                    }

                    clicked=false;
                }

            }
        });

    }

    public String makeRequest(String lat, String lng) {

        try {
            MakeRequest async = new MakeRequest(lat, lng);
            try {
                String result = async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
                if (result.substring(0,1).equals("0"))
                    return result.substring(1);
                else
                    return "1";
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {

        }
        return "";
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
                                String result = async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
                                if (result.equals("0"))
                                    showSetRequest();
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

        timer.schedule(task, 0, 5 * 1000);  // interval of one 30 seconds

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

    public void showSetRequest() {
        boolean setRequest = false;
        for (Bike bike : status) {
            int distance = (int) (meterDistanceBetweenPoints((float) bike.getLatitude(), (float) bike.getLongitude(), (float) userLoc.latitude, (float) userLoc.longitude));
            if (distance < 1000)
                setRequest = true;
        }
        if (setRequest) { //if(!setRequest) yap
            manageBlinkEffect();
        }
    }


    public void setDialog(final int bikenum) throws ExecutionException, InterruptedException {

        int distance = (int) meterDistanceBetweenPoints((float) userLoc.latitude, (float) userLoc.longitude, (float) marker.get(bikenum).getPosition().latitude, (float) marker.get(bikenum).getPosition().longitude);
        TextView txtclose;
        TextView bikestats;
        Button btnreserved;
        TextView dist;
        TextView log;
        myDialog.setContentView(R.layout.custompopup);
        myDialog.setCancelable(false);
        bikestats = myDialog.findViewById(R.id.bikestatus);
        bikestats.setTextColor(Color.DKGRAY);
        bikestats.setText(status.get(bikenum).getStatus_name());
        dist = myDialog.findViewById(R.id.dist);
        dist.setText(distance + " m");
        log = myDialog.findViewById(R.id.log);
        btnreserved = (Button) myDialog.findViewById(R.id.btnrzrv);
        if (status.get(bikenum).getStatus_code() != 1) {

            log.setText("This bike is not avaliable right now!");
            log.setVisibility(View.VISIBLE);
            btnreserved.setVisibility(View.GONE);
        } else {

            log.setTextColor(Color.DKGRAY);

            log.setText("Bike in "+status.get(bikenum).getAddress());

            log.setVisibility(View.VISIBLE);

            btnreserved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        SetBikeRez async = new SetBikeRez(status.get(bikenum).getId());
                        try {
                            async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {

                    }
                    myDialog.dismiss();
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
                try {
                    setDialog(whichBike);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        whichBike = -1;
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

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        getMyLocation();
        return false;
    }

    public void putRequestedMarker(String address) {

        request = mMap.addMarker((new MarkerOptions().position(new LatLng(user_request_lat, user_request_long)).title("Your request in "+address ).icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_requested))));
        request.showInfoWindow();

    }

    public void update_bike(final Bike bike, String status, String latitude, String longitude, final int hangi, final String address) {

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
        bike.setLatitude(Double.parseDouble(latitude));
        bike.setLongitude(Double.parseDouble(longitude));
        bike.setAddress(address);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                marker.remove(hangi);
                marker.add(mMap.addMarker(new MarkerOptions().position(new LatLng(bike.getLatitude(), bike.getLongitude())).title(bike.getStatus_name() + " bike in  "+address ).icon(BitmapDescriptorFactory.fromResource(bike.getLogo_name()))));

            }
        });
    }

    public Bike add_bike(String status_code, String name, String latitude, String longitude,String address) {
        Bike bike;
        if (status_code.equals("0")) {
            bike = new Bike(0, "Off service", R.drawable.bike_offservice, Integer.parseInt(name), Double.parseDouble(latitude), Double.parseDouble(longitude),address);
        } else if (status_code.equals("1")) {
            bike = new Bike(1, "Available", R.drawable.bike_available, Integer.parseInt(name), Double.parseDouble(latitude), Double.parseDouble(longitude),address);
        } else if (status_code.equals("2")) {
            bike = new Bike(2, "Busy", R.drawable.bike_busy, Integer.parseInt(name), Double.parseDouble(latitude), Double.parseDouble(longitude),address);
        } else {
            bike = new Bike(3, "Rezerved", R.drawable.bike_busy, Integer.parseInt(name), Double.parseDouble(latitude), Double.parseDouble(longitude),address);
        }
        return bike;
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
                jsonLocData.put("bike_id", this.bikeid);
                jsonLocData.put("usernameres", user_name);

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
                if (!status.equals("0")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Request failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Bike rezerved for you!", Toast.LENGTH_SHORT).show();
                        }
                    });
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

    class MakeRequest extends AsyncTask<String, String, String> {
        String lat;
        String lng;

        public MakeRequest(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        protected String doInBackground(String[] urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL url = null;
            String response = null;
            JSONObject jsonLocData = new JSONObject();
            try {
                jsonLocData.put("usernamereq", user_name);
                jsonLocData.put("lat", this.lat);
                jsonLocData.put("long", this.lng);
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
                String addr = jsonObject.getString("address");
                isr.close();
                reader.close();
                return status+addr;

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

    class CancelRequest extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String[] urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL url = null;
            String response = null;
            JSONObject jsonLocData = new JSONObject();
            try {
                jsonLocData.put("deletereq", user_name);

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
                isr.close();
                reader.close();
                if (status.equals("0")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Request canceled", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return "0";
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error when canceling request", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return "1";
                }

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
                jsonLocData.put("usernamebikes", user_name);
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
                jsonParser = new JSONParser();
                String jsonString = response;
                if (jsonString != null) {
                    try {
                        final JSONObject jsonObject = new JSONObject(jsonString);
                        JSONArray bikes = jsonObject.getJSONArray("bikes");
                        has_request = Integer.parseInt(jsonObject.getString("status"));
                        user_addr=jsonObject.getString("useraddress");
                        Log.i("addr:",user_addr);
                        for (int i = 0; i < bikes.length(); i++) {
                            boolean flag = true;
                            int count = 0;
                            JSONObject values = bikes.getJSONObject(i);
                            for (Bike bike : status) {
                                if (bike.getId() == Integer.parseInt(values.getString("name").substring(4))) {
                                    flag = false;
                                    if (bike.getStatus_code() != Integer.parseInt(values.getString("status")) || bike.getLatitude() != Double.parseDouble(values.getString("lat"))
                                            || bike.getLongitude() != Double.parseDouble(values.getString("long"))) {
                                        update_bike(bike, values.getString("status"), values.getString("lat"), values.getString("long"), count,values.getString("address"));
                                        break;
                                    }
                                }
                                count++;
                            }
                            if (flag)
                                status.add(add_bike(values.getString("status"), values.getString("name").substring(4), values.getString("lat"), values.getString("long"),values.getString("address")));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(userLoc!=null)
                                    mMap.addMarker(new MarkerOptions().position(userLoc).title("You are here"));
                                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15));
                                for (int i = 0; i < status.size(); i++) {
                                    boolean flag = true;
                                    for (Marker marker : marker) {
                                        if (marker.getPosition().latitude == status.get(i).getLatitude() && marker.getPosition().longitude == status.get(i).getLongitude()) {
                                            flag = false;
                                        }
                                    }
                                    if (flag) {
                                        marker.add(mMap.addMarker(new MarkerOptions().position(new LatLng(status.get(i).getLatitude(), status.get(i).getLongitude())).title(status.get(i).getStatus_name() + " bike in  " ).icon(BitmapDescriptorFactory.fromResource(status.get(i).getLogo_name()))));
                                    }
                                }
                                if (has_request == 0 && has_created) {
                                    try {
                                        user_request_lat = Double.parseDouble(jsonObject.getString("lat"));
                                        user_request_long = Double.parseDouble(jsonObject.getString("long"));
                                        putRequestedMarker(jsonObject.getString("address"));
                                        cancelRequestDialog(jsonObject.getString("address"));
                                        has_created=false;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else if (has_request == 2 && has_created) {
                                    if(userLoc!=null)
                                    showSetRequest();
                                    has_created=false;
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
            }

            return null;
        }

    }
}

