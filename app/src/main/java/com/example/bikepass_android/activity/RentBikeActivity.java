package com.example.bikepass_android.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikepass_android.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Created by MustafaSaid on 21.01.2020
 */
public class RentBikeActivity extends AppCompatActivity implements View.OnClickListener {


    TextView textViewQR;
    Button buttonCancel;
    SurfaceView surfaceView;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource = null;

    private String bikeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_bike);

        textViewQR = (TextView) findViewById(R.id.textViewQR);

        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(this);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        barcodeDetector = new BarcodeDetector.Builder(this).
                setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector).
                setRequestedPreviewSize(640, 480).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cameraSource.start(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                String insideQRCode = "";
                if (qrCodes.size() != 0)
                    insideQRCode = qrCodes.valueAt(0).displayValue;

                if (qrCodes.size() != 0 && insideQRCode.toLowerCase().contains("bike")) {
                    textViewQR.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            String bikeName = qrCodes.valueAt(0).displayValue;
                            bikeId = bikeName.substring(bikeName.lastIndexOf(" ") + 1);

                            /**
                             * Bu kod parcasi bikeId'yi BikeUsingActivity'e gecirmek icin yazildi.
                             */
                            Intent intent2 = new Intent(RentBikeActivity.this, BikeUsingActivity.class);
                            intent2.putExtra("key", bikeId);
                            startActivity(intent2);


                        }
                    });
                } else {
                    //Toast.makeText(RentBikeActivity.this.getApplicationContext(), "Invalid QR CODE!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCancel:
                startActivity(new Intent(RentBikeActivity.this, BikeUsingActivity.class));
                //this.finish();
                break;
        }
    }
}
