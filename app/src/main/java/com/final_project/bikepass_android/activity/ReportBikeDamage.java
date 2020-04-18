package com.final_project.bikepass_android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.final_project.bikepass_android.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.net.HttpURLConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.EditText;
import android.net.Uri;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedWriter;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.io.OutputStreamWriter;
import java.net.URL;
import android.provider.MediaStore;
import java.io.BufferedReader;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ReportBikeDamage extends AppCompatActivity {
    ProgressDialog progressDialog ;
    String ImageUploadPathOnSever ="https://Bikepass.herokuapp.com/API/app.php" ;
    String user_name;
    String user_addr;
    String reason;
    byte[] b;
    String encodedImage;
    LinearLayout clickedLayout;
    TextView clickedText;
    private static final int CAMERA_REQUEST = 1888;
    ImageView imageView;
    Uri image_uri;
    Bitmap bitmap;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    String GetImageNameFromEditText;
    String ImageNameFieldOnServer = "image_name" ;
    String ImagePathFieldOnServer = "image_path" ;
    private static final int PERMISSION_CODE=1000;
    private static final int IMAGE_CAPTURE_CODE=1001;
    ImageView photoButton;
    LinearLayout imagelayout;
    boolean check = true;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_bike_damage);

        Intent intent=getIntent();
        if(intent.getStringExtra("username")!=null && intent.getStringExtra("useraddr")!=null) {
            user_name=intent.getStringExtra("username");
            user_addr=intent.getStringExtra("useraddr");
        }
        imagelayout=findViewById(R.id.photolayout);
        TextView loc=findViewById(R.id.userlocation);
        loc.setText(user_addr);
        loc.setTextColor(Color.BLACK);
        photoButton=findViewById(R.id.photobutton);
        photoButton.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v)
            {

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

        imageView = new ImageView(this);
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
                startActivity(new Intent(ReportBikeDamage.this, ScanQrCode.class));
            }
        });

        setListenersForButtons();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            imagelayout.addView(imageView);
            //imageView.setRotation(270);
        }


    }
    public static File savebitmap(Bitmap bmp) throws IOException {

        String date = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());

        File folder = new File(Environment.getExternalStorageDirectory()
                + "/RefreshPhotos");

        boolean var = false;
        if (!folder.exists())
            var = folder.mkdir();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(folder
                + File.separator + "RefreshPhoto_" + date + ".png");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;
    }


    public void setListenersForButtons(){

        final LinearLayout qrcodelayout=findViewById(R.id.qrcodelayout);
        final TextView qrcodetext=findViewById(R.id.qrcodetext);
        qrcodetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedLayout!=null){
                    clickedLayout.setBackgroundColor(Color.WHITE);
                    clickedText.setTextColor(Color.BLACK);
                }
                reason=qrcodetext.getText().toString();
                clickedText=qrcodetext;
                clickedLayout=qrcodelayout;
                qrcodelayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_black));
                qrcodetext.setTextColor(Color.WHITE);

            }
        });
        final LinearLayout tekeripatlamislayout=findViewById(R.id.tekeripatlamislayout);
        final TextView tekerpatlamistext=findViewById(R.id.tekerpatlamistext);
        tekerpatlamistext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedLayout!=null){
                    clickedLayout.setBackgroundColor(Color.WHITE);
                    clickedText.setTextColor(Color.BLACK);
                }
                reason=tekerpatlamistext.getText().toString();
                clickedLayout=tekeripatlamislayout;
                clickedText=tekerpatlamistext;
                tekeripatlamislayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_black));
                tekerpatlamistext.setTextColor(Color.WHITE);
            }
        });
        final LinearLayout kilidibozulmuslayout=findViewById(R.id.kilidibozulmuslayout);
        final TextView kilidibozulmustext=findViewById(R.id.kilidibozulmustext);
        kilidibozulmustext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedLayout!=null){
                    clickedLayout.setBackgroundColor(Color.WHITE);
                    clickedText.setTextColor(Color.BLACK);
                }
                reason=kilidibozulmustext.getText().toString();
                clickedLayout=kilidibozulmuslayout;
                clickedText=kilidibozulmustext;
                kilidibozulmuslayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rounded_black));
                kilidibozulmustext.setTextColor(Color.WHITE);
            }
        });
        final LinearLayout send=findViewById(R.id.send_mail);
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendEmailService async=new SendEmailService();

                try {
                   async.execute(ImageUploadPathOnSever).get();
                   Log.i("helloo","hello");
                } catch (ExecutionException e) {

                    e.printStackTrace();
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public void onBackPressed() {

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
                jsonEmailData.put("image_name",encodedImage);

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



}