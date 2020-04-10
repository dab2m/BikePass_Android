package com.example.bikepass_android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikepass_android.R;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class RentBikeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    private String bikeId = null;
    private String qrCode;
    private String username;
    private String myResult;
    private String message;

    private float lat;
    private float lng;

    private boolean isPromotion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;

        SharedPreferences preferences = getSharedPreferences("username", getApplicationContext().MODE_PRIVATE);
        username = preferences.getString("username", null);

        SharedPreferences prefs = getSharedPreferences("LOCATION", MODE_PRIVATE);
        lat = prefs.getFloat("lat", 0);
        lng = prefs.getFloat("lng", 0);
        Log.i("LAT", String.valueOf(lat));
        Log.i("LONG", String.valueOf(lng));

        /**
         * Asagidaki kod parcasi MapRequests'den isPromotion'u almak icin yazilmistir.
         */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isPromotion = extras.getBoolean("isPromotion");
        }

        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (checkPermission()) {

            } else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(RentBikeActivity.this)
                .setMessage(message)
                .setPositiveButton("START", okListener)
                .setNegativeButton("CANCEL", null)
                .create()
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void handleResult(Result result) {
        myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        qrCode = myResult;
        bikeId = qrCode.substring(qrCode.lastIndexOf(" ") + 1);

        getRequestForUnlockBike();

    }

    private void getRequestForUnlockBike() {
        // REST API
        MyAsyncBikeIdForUnlock async = new MyAsyncBikeIdForUnlock();
        String bikeStatus = null;
        try {
            bikeStatus = async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        bikeStatusParser(bikeStatus);
    }

    public void showDialogForStart(Activity activity, String msg) {
        final String myResult = msg;
        Log.d("QRCodeScanner", msg);
        Log.d("QRCodeScanner", msg.toString());

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialogbox_for_start);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.txt_file_path);
        if (!isPromotion)
            text.setText(msg);
        else
            text.setText("PROMOTION IS STARTING");

        Button dialogBtn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scannerView.resumeCameraPreview(RentBikeActivity.this);
                dialog.dismiss();
            }
        });

        Button dialogBtn_start = (Button) dialog.findViewById(R.id.btn_start);
        dialogBtn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPromotion) {
                    bikeId = myResult.substring(myResult.lastIndexOf(" ") + 1);
                    /**
                     * Bu kod parcasi bikeId'yi BikeUsingActivity'e gecirmek icin yazildi.
                     */
                    Intent intent = new Intent(RentBikeActivity.this, BikeUsingActivity.class);
                    intent.putExtra("key", bikeId);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(RentBikeActivity.this, MapRequests.class);
                    intent.putExtra("isQrScanned", true);
                    startActivity(intent);
                }

                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void showDialogForWarning(Activity activity, String msg) {
        final String myResult = msg;
        Log.d("QRCodeScanner", msg);
        Log.d("QRCodeScanner", msg.toString());

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialogbox_for_warning);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.txt_file_path);
        text.setText(msg);

        Button dialogBtn_okay = (Button) dialog.findViewById(R.id.btn_okay);
        dialogBtn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scannerView.resumeCameraPreview(RentBikeActivity.this);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void bikeStatusParser(String bikeStatus) {
        if (bikeStatus != null) {
            if (bikeStatus.equals("0")) { // Bike is unlocked
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(50);

                showDialogForStart(this, "INFO : " + myResult.toUpperCase());
            } else if (bikeStatus.equals("1")) { // Unidentified bike_id
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(30);

                showDialogForWarning(this, "INFO : " + message);
            } else if (bikeStatus.equals("3")) { // Bike is not available
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(30);

                showDialogForWarning(this, "INFO : " + message);
            } else if (bikeStatus.equals("4")) { // Database error!
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(30);

                showDialogForWarning(this, "INFO : " + message);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Connection Error !", Toast.LENGTH_LONG).show();
            scannerView.resumeCameraPreview(RentBikeActivity.this);
        }

    }

    class MyAsyncBikeIdForUnlock extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
            JSONObject jsonObject = new JSONObject();
            try {
                // QR kodunun icinde "Bike Id: 1" tarzinda bir icerik olacak bu yuzden bike id'yi almak icin bosluktan sonrasi okunur.
                jsonObject.put("bike_id", qrCode.substring(qrCode.lastIndexOf(" ") + 1)); // okunan qr kodun icinde id olacak ona gore rest ile search yapacak
                jsonObject.put("username", username); // qr kod okunduktan sonra bisikleti hangi kullanici kiraladi ona gore server'a haber verecek
                jsonObject.put("lat", lat);
                jsonObject.put("long", lng);
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
                message = jObj.getString("message"); // request sonucu donen bisikletin durum mesaji
                String status = jObj.getString("status"); // request sonucu donen bisikletin statusu

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

}