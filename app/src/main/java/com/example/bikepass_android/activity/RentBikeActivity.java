package com.example.bikepass_android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
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

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class RentBikeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    private String bikeId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;

       /* Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour > 8 && hour < 17){
            scannerView.setFlash(false);
        }
        else{
            scannerView.setFlash(true);
        }*/

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
        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        if (qrCodeAnalyzer(myResult)) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(50);

            showDialog(this, "INFO : " + String.valueOf(result).toUpperCase());

        } else {

            Toast.makeText(getApplicationContext(), "Invalid QR Code!", Toast.LENGTH_LONG).show();
            scannerView.resumeCameraPreview(RentBikeActivity.this);

        }

    }

    public void showDialog(Activity activity, String msg) {
        final String myResult = msg;
        Log.d("QRCodeScanner", msg);
        Log.d("QRCodeScanner", msg.toString());

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialogbox_for_start);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.txt_file_path);
        text.setText(msg);

        Button dialogBtn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scannerView.resumeCameraPreview(RentBikeActivity.this);
                dialog.dismiss();
            }
        });

        Button dialogBtn_start = (Button) dialog.findViewById(R.id.btn_okay);
        dialogBtn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bikeId = myResult.substring(myResult.lastIndexOf(" ") + 1);
                /**
                 * Bu kod parcasi bikeId'yi BikeUsingActivity'e gecirmek icin yazildi.
                 */
                Intent intent = new Intent(RentBikeActivity.this, BikeUsingActivity.class);
                intent.putExtra("key", bikeId);
                startActivity(intent);
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public boolean qrCodeAnalyzer(String qrCode) {
        if (!qrCode.toLowerCase().contains("bike")) {
            return false;
        }
        return true;
        //TODO buraya kendi bisikletlerimizin id'si cekilecek ve buna gore okutulan qr kodunun bize ait olup olmadigi REST ile kontrol edilecek.
    }
}