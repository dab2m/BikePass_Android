package com.example.bikepass_android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import com.example.bikepass_android.directionhelpers.*;


/**
 * Created by Dilan on 02.02.2020
 */
public class MapRequests extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    JSONParser jsonParser;
    Dialog myDialog;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private GoogleMap mMap;
    private boolean isGPS = false;
    private boolean isContinue = false;

    private TextView timer_textview;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 1800000; // 30 min
    private boolean timerRunning;
    private boolean isPromotion = true;
    private boolean isQrScanned = false;
    private Button finishPromotion_button;
    private int distance;
    private int bikeId;
    private int elapsedTime;
    private int earnCredit;
    private String deleteRequest_username;

    private List<Request> requestList = new ArrayList<>();

    ArrayList<Marker> marker = new ArrayList<Marker>();
    LatLng userLoc;

    private Polyline currentPolyline;
    final ArrayList<Hotspots> hotspots = new ArrayList<Hotspots>();
    final ArrayList<Bike> bike_request = new ArrayList<Bike>();
    final ArrayList<Bike> bike_avaliable = new ArrayList<Bike>();
    private String user_name;
    int whichBike = -1;
    private MapFragment mapFrag;

    SharedPreferences sharedpreferences;
    Button scanqr;
    private Marker requested_bike;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getRequestForHotpoints();

        setContentView(R.layout.activity_map_requests);
        sharedpreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        user_name = sharedpreferences.getString("username", "");
        mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.requests);

        finishPromotion_button = (Button) findViewById(R.id.finishPromotion_button);
        finishPromotion_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (earnCredit > 0) {
                    showDialogForEarnCredit(MapRequests.this);
                    finishPromotion_button.setVisibility(View.GONE);

                    stopTimer();
                    updateTimer();
                    timer_textview.setVisibility(View.GONE);
                } else {
                    showDialogForWarning(MapRequests.this);
                }

            }
        });
        timer_textview = (TextView) findViewById(R.id.timer_textview);
        /**
         * Asagidaki kod parcasi RentBikeActivity'den isQrScanned'i almak icin yazilmistir.
         */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isQrScanned = extras.getBoolean("isQrScanned");
            bikeId = extras.getInt("bikeId");
            deleteRequest_username = extras.getString("deletereq");
        }
        if (!isQrScanned) {
            timer_textview.setVisibility(View.GONE);
            finishPromotion_button.setVisibility(View.GONE);
        } else {
            timer_textview.setVisibility(View.VISIBLE);
            finishPromotion_button.setVisibility(View.VISIBLE);
            startTimer();
        }

        mapFrag.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds
        scanqr = findViewById(R.id.btnreq);
        scanqr.setVisibility(View.GONE);
        scanqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanqr.setVisibility(View.GONE);
                /**
                 * Bu kod parcasi isPromotion'i RentBİkeActivity'e gecirmek icin yazildi.
                 */
                Intent intent = new Intent(MapRequests.this, RentBikeActivity.class);
                intent.putExtra("isPromotion", isPromotion);
                intent.putExtra("deletereq", deleteRequest_username);
                startActivity(intent);
            }
        });
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        myDialog = new Dialog(this);

    }

    @Override
    public void onBackPressed() { // timer calisiyorsa o sayfadan geri gitme
        if (timerRunning)
            Toast.makeText(this, "You can't go back when timer is running!", Toast.LENGTH_SHORT).show();
        else
            super.onBackPressed();
    }

    public void setTimer(int min) {
        this.timeLeftInMillis = min * 6000;
    }

    public void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                updateTimer();
            }

            @Override
            public void onFinish() {
                showDialogForPenalty(MapRequests.this);
            }
        }.start();

        timerRunning = true;
    }

    public void stopTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }

    public void updateTimer() {
        int min = (int) timeLeftInMillis / 60000;
        int sec = (int) timeLeftInMillis % 60000 / 1000;

        String timeLeftText;
        timeLeftText = "" + min;
        timeLeftText += ":";
        if (sec < 10)
            timeLeftText += "0";
        timeLeftText += sec;
        timer_textview.setText(timeLeftText);
    }


    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(MapRequests.this);
        mMap.setOnMyLocationClickListener(MapRequests.this);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

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

        getHotSpots();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onMarkerClick(Marker marker) {
        boolean showdialog = false;
        int creditToBeEarned = 0;
        for (Bike bike : bike_request) {
            if (bike.getLatitude() == marker.getPosition().latitude && bike.getLongitude() == marker.getPosition().longitude) {
                for (Request request : requestList) {
                    if (request.lat == marker.getPosition().latitude && request.lng == marker.getPosition().longitude) {
                        String requestTime = request.requestTime;
                        deleteRequest_username = request.username;
                        try {
                            creditToBeEarned = getCreditToBeEarned(requestTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                showdialog = true;
            }
        }
        String url = getUrl(userLoc, marker.getPosition(), "walking");
        new FetchURL(MapRequests.this).execute(url, "walking");
        distance = (int) meterDistanceBetweenPoints((float) userLoc.latitude, (float) userLoc.longitude, (float) marker.getPosition().latitude, (float) marker.getPosition().longitude);
        if (showdialog) {
            myDialog.setContentView(R.layout.custom_infowindow);
            Window window = myDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.TOP;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            window.getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
            TextView textmoney = myDialog.findViewById(R.id.creditTxt);
            textmoney.setText("You will get " + creditToBeEarned + " credit if you manage this in 30 minutes!");
            TextView adressText = myDialog.findViewById(R.id.addressTxt);
            adressText.setText("It's only " + distance + " M away from you");
            TextView txtclose = myDialog.findViewById(R.id.txtclose);
            txtclose.setText("X");
            txtclose.setTextColor(Color.BLACK);
            txtclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });
            Button btnOne = myDialog.findViewById(R.id.btnOne);
            btnOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();

                    scanqr.setVisibility(View.VISIBLE);
                }
            });
            myDialog.show();
            marker.showInfoWindow();
        }
        return true;
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

    private void getHotSpots() {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            GetHotspotAndIssues async = new GetHotspotAndIssues();
                            try {
                                String result = async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
                                if (result.equals("0"))
                                    setHotspots();
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

    public void setHotspots() {
        for (Hotspots hotspot : hotspots) {
            mMap.addCircle(
                    new CircleOptions()
                            .center(new LatLng(hotspot.getLatitude(), hotspot.getLongitude()))
                            .radius(hotspot.getRadius())
                            .strokeWidth(3f)
                            .strokeColor(Color.BLUE)
                            .fillColor(Color.argb(70, 150, 50, 50))
            );
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MapRequests.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapRequests.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapRequests.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(MapRequests.this, new OnSuccessListener<Location>() {
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
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(MapRequests.this, new OnSuccessListener<Location>() {
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


    private String getAddress(final double lat, final double lng) {
        Log.i("message:", "lat:" + lat + " lng:" + lng);
        final String[] add = {""};
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {

                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                    Address obj = addresses.get(0);
                    add[0] = obj.getAddressLine(0);

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        };
        thread.start();
        Log.i("add:", add[0]);
        return add[0];
    }

    public void update_bike(final Bike bike, String status, String latitude, String longitude) {

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                marker.add(mMap.addMarker(new MarkerOptions().position(new LatLng(bike.getLatitude(), bike.getLongitude())).title(bike.getStatus_name() + " bike in  " + getAddress(bike.getLatitude(), bike.getLongitude())).icon(BitmapDescriptorFactory.fromResource(bike.getLogo_name()))));
            }
        });
    }

    class GetHotspotAndIssues extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String[] urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL url = null;
            String response = null;
            JSONObject jsonReqData = new JSONObject();
            try {
                jsonReqData.put("hotpoints", "hotpoints");
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
                request.write(String.valueOf(jsonReqData));
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
                JSONObject jObj = new JSONObject(response);
                String status = jObj.getString("status");
                String jsonString = response;
                if (jsonString != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONArray hotspot_array = jsonObject.getJSONArray("hotpoints");
                        for (int i = 0; i < hotspot_array.length(); i++) {
                            boolean addpoint = true;
                            JSONObject values = hotspot_array.getJSONObject(i);
                            for (Hotspots spot : hotspots) {
                                if (spot.getPoint_name().equals(values.getString("point_name"))) {
                                    addpoint = false;
                                }
                            }
                            if (addpoint) {
                                Hotspots spot = new Hotspots(Double.parseDouble(values.getString("radius")), values.getString("point_name"), Integer.parseInt(values.getString("frequency")), Double.parseDouble(values.getString("lat")), Double.parseDouble(values.getString("long")));
                                hotspots.add(spot);
                            }
                        }
                        JSONArray request_array = jsonObject.getJSONArray("requests");
                        for (int i = 0; i < request_array.length(); i++) {
                            boolean add_request = true;
                            JSONObject values = request_array.getJSONObject(i);
                            final LatLng lat = new LatLng(Double.parseDouble(values.getString("lat")), Double.parseDouble(values.getString("long")));
                            for (Bike bike : bike_request) {
                                if (bike.getId() == Integer.parseInt(values.getString("id")))
                                    add_request = false;
                            }
                            if (add_request) {
                                Bike bike = new Bike(1, "Requested", R.drawable.bike_requested, Integer.parseInt(values.getString("id")), lat.latitude, lat.longitude);
                                bike_request.add(bike);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        marker.add(mMap.addMarker(new MarkerOptions().position(lat).title("Request in " + getAddress(lat.latitude, lat.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_requested))));
                                    }
                                });
                            }
                        }

                        JSONArray available_array = jsonObject.getJSONArray("bikes");
                        for (int i = 0; i < available_array.length(); i++) {
                            boolean add_bike = true;
                            JSONObject values = available_array.getJSONObject(i);
                            final LatLng lat = new LatLng(Double.parseDouble(values.getString("lat")), Double.parseDouble(values.getString("long")));
                            for (Bike bike : bike_avaliable) {
                                if (bike.getId() == Integer.parseInt(values.getString("name").substring(4))) {
                                    add_bike = false;
                                    if (bike.getLatitude() != lat.latitude || bike.getLongitude() != lat.longitude) {
                                        LatLng old_lat = new LatLng(bike.getLatitude(), bike.getLongitude());
                                        marker.remove(old_lat);
                                        update_bike(bike, "1", values.getString("lat"), values.getString("long"));
                                    }

                                }
                            }
                            if (add_bike) {
                                Bike bike = new Bike(1, "Available", R.drawable.bike_available, Integer.parseInt(values.getString("name").substring(4)), lat.latitude, lat.longitude);
                                bike_avaliable.add(bike);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        marker.add(mMap.addMarker(new MarkerOptions().position(lat).title("Available in " + getAddress(lat.latitude, lat.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_available))));
                                    }
                                });
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
                isr.close();
                reader.close();
                return status;
            } catch (IOException | JSONException e) {
                // Error
                e.printStackTrace();
                return null;
            }

        }
    }

    class MyAsyncForGetRequests extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] urls) {
            String message;
            String status;

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("hotpoints", true);
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
                request.write(String.valueOf(jsonObject));
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
                message = jObj.getString("message");
                status = jObj.getString("status");
                JSONArray jsonArray = jObj.getJSONArray("requests");

                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String username = obj.getString("username");
                        double latitude = obj.getDouble("lat");
                        double longitude = obj.getDouble("long");
                        double radius = obj.getDouble("radius");
                        String requestTime = obj.getString("request_time");
                        int id = obj.getInt("id");


                        Request response_request = new Request(username, latitude, longitude, radius, requestTime, id);
                        requestList.add(response_request);
                    }
                }

                isr.close();
                reader.close();

                Log.i("status", status);
                Log.i("message", message);

                return status;
            } catch (IOException e) {
                // Error
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class Request {
        private String username;
        private double lat;
        private double lng;
        private double radius;
        private String requestTime;
        private int id;

        public Request(String username, double lat, double lng, double radius, String requestTime, int id) {
            this.username = username;
            this.lat = lat;
            this.lng = lng;
            this.radius = radius;
            this.requestTime = requestTime;
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getRadius() {
            return radius;
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }

        public String getRequestTime() {
            return requestTime;
        }

        public void setRequestTime(String requestTime) {
            this.requestTime = requestTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    private void getRequestForHotpoints() {
        // REST API
        MyAsyncForGetRequests async = new MyAsyncForGetRequests();
        try {
            async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getCreditToBeEarned(String requestTime) throws ParseException {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");  //it will give you the date in the formate that is given in the image
        String current_time = dateformat.format(c.getTime());


        SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss");
        Date dt2 = dt.parse(current_time);
        Date dt3 = dt.parse(requestTime);

        elapsedTime = (int) (dt2.getTime() - dt3.getTime()) / 60000;

        return 2000 - elapsedTime;
    }

    class MyAsyncForFinish extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", user_name);
                jsonObject.put("bike_id", bikeId);
                jsonObject.put("lat", userLoc.latitude);
                jsonObject.put("long", userLoc.longitude);
                jsonObject.put("bike_time", 1800);
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
                request.write(String.valueOf(jsonObject));
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
                String message = jObj.getString("message"); // request sonucu donen bisikletin durum mesaji
                String status = jObj.getString("status"); // request sonucu donen bisikletin statusu
                earnCredit = jObj.getInt("credit"); // promosyondan kazanilan credit miktari

                isr.close();
                reader.close();

                Log.i("status", status);
                Log.i("message", message);

                return status;
            } catch (IOException e) {
                // Error
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class MyAsyncDeleteRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("deletereq", deleteRequest_username);
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
                request.write(String.valueOf(jsonObject));
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
                String message = jObj.getString("message");
                String status = jObj.getString("status");

                isr.close();
                reader.close();

                Log.i("status", status);
                Log.i("message", message);

                return status;
            } catch (IOException e) {
                // Error
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void getRequestForDeleteRequest() {
        // REST API
        MyAsyncDeleteRequest async = new MyAsyncDeleteRequest();
        try {
            async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getRequestForFinish() {
        // REST API
        MyAsyncForFinish async = new MyAsyncForFinish();
        try {
            async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showDialogForWarning(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialogbox_for_warning);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        TextView text = (TextView) dialog.findViewById(R.id.txt_file_path);
        text.setText("WARNING !");

        TextView text2 = (TextView) dialog.findViewById(R.id.tv_info);
        text2.setText("You must drop the bike on request point which taken by you.");


        Button dialogBtn_okay = (Button) dialog.findViewById(R.id.btn_okay);
        dialogBtn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void showDialogForPenalty(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialogbox_for_warning);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        TextView text = (TextView) dialog.findViewById(R.id.txt_file_path);
        text.setText("WARNING !");

        TextView text2 = (TextView) dialog.findViewById(R.id.tv_info);
        text2.setText("You have not completed the task. Redirecting to Bike Rent Page.");


        Button dialogBtn_okay = (Button) dialog.findViewById(R.id.btn_okay);
        dialogBtn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapRequests.this, BikeUsingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("penaltyCredit", "18000");
                intent.putExtra("username", user_name);
                intent.putExtra("bikeId", String.valueOf(bikeId));
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void showDialogForEarnCredit(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialogbox_for_finish);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.txt_file_path);
        text.setText("HAVE YOU ARRIVED?");

        TextView text2 = (TextView) dialog.findViewById(R.id.finish_text);
        text2.setText("Do you want to finish?");

        Button dialogBtn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button dialogBtn_finish = (Button) dialog.findViewById(R.id.btn_finish);
        dialogBtn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRequestForDeleteRequest();

                getRequestForFinish();

                Toast.makeText(getApplicationContext(), "You earned " + earnCredit + " credit.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MapRequests.this, MapRequests.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("earnCredit", earnCredit);
                startActivity(intent);
                dialog.cancel();
            }
        });

        dialog.show();
    }
}

