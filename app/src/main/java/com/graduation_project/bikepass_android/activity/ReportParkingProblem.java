package com.graduation_project.bikepass_android.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.graduation_project.bikepass_android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ReportParkingProblem extends AppCompatActivity {
    String ImageUploadPathOnSever ="https://Bikepass.herokuapp.com/recoverymail.php" ;
    String user_name;
    String user_addr;
    String reason;
    LinearLayout clickedLayout;
    TextView clickedText;
    Uri image_uri;
    Bitmap bitmap;
    private static final int PERMISSION_CODE=1000;
    private static final int IMAGE_CAPTURE_CODE=1001;
    ImageView photoButton;
    LinearLayout imagelayout;
    ImageView imageView;
    byte[] b;
    String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_parking_problem);
        Intent intent=getIntent();
        if(intent.getStringExtra("username")!=null && intent.getStringExtra("useraddr")!=null) {
            user_name=intent.getStringExtra("username");
            user_addr=intent.getStringExtra("useraddr");
        }
        TextView loc=findViewById(R.id.userlocation);
        loc.setText(user_addr);
        loc.setTextColor(Color.BLACK);
        ImageView arrow=findViewById(R.id.txtclose);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ReportDamage.class);
                intent.putExtra("username",user_name);
                intent.putExtra("useraddr",user_addr);
                startActivity(intent);
            }
        });

        LinearLayout qrscan=findViewById(R.id.qrscan);
        qrscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportParkingProblem.this, ScanQrCode.class));
            }
        });
        imageView = new ImageView(this);
        imagelayout=findViewById(R.id.imageview);
        photoButton=findViewById(R.id.takephoto);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED) {

                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

                        requestPermissions(permission, PERMISSION_CODE);
                    } else {

                        openCamera();
                    }
                }
                else{
                    openCamera();
                }
            }
        });
        setListeners();

    }

    public void openCamera(){

        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Pıcture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //Camera intent
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);

    }

    //handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        switch(requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else{
                    Toast.makeText(this,"Permission denied..",Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode != RESULT_OK ) {
            TextView damagephoto = findViewById(R.id.damagephoto);
            imagelayout.removeView(damagephoto);
            imageView.setImageURI(image_uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                b = baos.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                Log.i("encoded image:",encodedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imagelayout.addView(imageView);
            //imageView.setRotation(270);
        }
    }


    public void setListeners(){
        final LinearLayout erisilebilirliklayout=findViewById(R.id.erisilebilirliklayout);
        final TextView erisililebilirlik=findViewById(R.id.erisilebilirlik);
        erisililebilirlik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedLayout!=null){
                    clickedLayout.setBackgroundColor(Color.WHITE);
                    clickedText.setTextColor(Color.BLACK);
                }
                reason=erisililebilirlik.getText().toString();
                clickedText=erisililebilirlik;
                clickedLayout=erisilebilirliklayout;
                erisilebilirliklayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_black));
                erisililebilirlik.setTextColor(Color.WHITE);

            }
        });
        final LinearLayout ozelmulkiyetlayout=findViewById(R.id.ozelmulkiyetlayout);
        final TextView ozelmulkiyet=findViewById(R.id.ozelmulkiyet);
        ozelmulkiyet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedLayout!=null){
                    clickedLayout.setBackgroundColor(Color.WHITE);
                    clickedText.setTextColor(Color.BLACK);
                }
                reason=ozelmulkiyet.getText().toString();
                clickedLayout=ozelmulkiyetlayout;
                clickedText=ozelmulkiyet;
                ozelmulkiyetlayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_black));
                ozelmulkiyet.setTextColor(Color.WHITE);
            }
        });

        final LinearLayout devrilmislayout=findViewById(R.id.devrilmislayout);
        final TextView devrilmis=findViewById(R.id.devrilmis);
        devrilmis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedLayout!=null){
                    clickedLayout.setBackgroundColor(Color.WHITE);
                    clickedText.setTextColor(Color.BLACK);
                }
                reason=devrilmis.getText().toString();
                clickedLayout=devrilmislayout;
                clickedText=devrilmis;
                devrilmislayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_black));
                devrilmis.setTextColor(Color.WHITE);
            }
        });
        final LinearLayout send=findViewById(R.id.  send_mail_button);
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendEmailService async=new SendEmailService();

                try {
                    async.execute(ImageUploadPathOnSever).get();
                } catch (ExecutionException e) {

                    e.printStackTrace();
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
        });

    }


    class SendEmailService extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String[]urls) {

            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL url = null;
            String response = null;
            JSONObject jsonEmailData = new JSONObject();
            try {
                jsonEmailData.put("username",user_name);
                jsonEmailData.put("message",reason);
                jsonEmailData.put("image_data",encodedImage);

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
                request.write(String.valueOf(jsonEmailData));
                request.flush();
                request.close();
                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);

              /*  StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                // Response from server after recovery process will be stored in response variable.
                response = sb.toString().trim();
                String jsonString=response;

                isr.close(); */
                reader.close();
                return "";
            } catch (IOException e) {
                // Error
                return "Failure sending";
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

}
