package com.final_project.bikepass_android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.final_project.bikepass_android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Berk on 03.02.2020
 */
public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {

    Button bLeaderboard;
    ImageButton bRentBike;
    ImageButton bGoMap;
    ImageButton bSettings;
    TextView view;
    TextView totalTimeCount;
    TextView totalRecoveryCount;
    TextView totalCreditCount;
    CardView time_cardView;
    CardView co2_cardView;
    CardView credit_cardView;
    GridLayout gl;

    private String time = null;
    private String total_credit = null;
    private String bikeId = null;
    private String user_name;
    private int earnCredit = 0;
    private String _time_double;
    private String co2String;
    ListView listView;
    String mTitle[]={"Message","Message"};
    String mDescription[]={"Message1","Message2"};
    int images[]={R.drawable.open,R.drawable.open};
    private JSONArray data_list; // user's all bike usage data inside this list
    private List<String> data_list_inString = new ArrayList<>();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        Intent intent = getIntent();
        //Toast.makeText(getApplicationContext(),"" +intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        bLeaderboard = (Button)findViewById(R.id.worldleaderboard);
        bRentBike = (ImageButton)findViewById(R.id.returnbikes);
        bGoMap = (ImageButton)findViewById(R.id.map);
        bSettings = (ImageButton)findViewById(R.id.settings);
        gl = (GridLayout)findViewById(R.id.grid_layout);
        time_cardView = (CardView)findViewById(R.id.time_cardView);
        co2_cardView = (CardView)findViewById(R.id.co2_cardView);
        credit_cardView = (CardView)findViewById(R.id.credit_cardView);
        totalTimeCount = (TextView)findViewById(R.id.totalTimeCount);
        totalRecoveryCount = (TextView)findViewById(R.id.totalRecoveryCount);
        totalCreditCount = (TextView)findViewById(R.id.totalCreditCount);
        bLeaderboard.setOnClickListener(this);
        bRentBike.setOnClickListener(this);
        bGoMap.setOnClickListener(this);
        bSettings.setOnClickListener(this);
        time_cardView.setOnClickListener(this);
        co2_cardView.setOnClickListener(this);
        credit_cardView.setOnClickListener(this);
        view = findViewById(R.id.textview);
        SharedPreferences sharedpreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        user_name = sharedpreferences.getString("username", "");
        view.setText("Welcome back, "+user_name);
        Bundle extras = getIntent().getExtras();
        if(extras!=null)
            earnCredit = extras.getInt("earnCredit");
        if(earnCredit>0)
            showDialogForEarnCredit(this);
        getRequestForUsageData();
        getRequestForTimeAndCredit();
        Button message_button=findViewById(R.id.message);
        final Dialog myDialog = new Dialog(this);
        message_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.setContentView(R.layout.custom_listview_messages);
                listView=myDialog.findViewById(R.id.messagelist);

                myDialog.setCancelable(false);
                TextView txtclose = (TextView) myDialog.findViewById(R.id.txtclose);
                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });


                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                MyAdapter adapter =new MyAdapter(getApplication(),mTitle,mDescription,images);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getApplicationContext(),"Messageee", Toast.LENGTH_SHORT).show();
                    }
                });

                myDialog.show();
            }
        });
    }

    class MyAdapter extends ArrayAdapter<String>{
        Context context;
        String rTitle[];
        String rDescription[];
        int rImgs[];
        MyAdapter(Context c,String title[],String description[],int imgs[]){
            super(c,R.layout.row,R.id.textView1,title);
            this.context=c;
            this.rTitle=title;
            this.rDescription=description;
            this.rImgs=imgs;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater=(LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=layoutInflater.inflate(R.layout.row,parent,false);
            ImageView images=row.findViewById(R.id.image);
            TextView myTitle=row.findViewById(R.id.textView1);
            TextView myDescription=row.findViewById(R.id.textView2);
            images.setImageResource(rImgs[position]);
            myTitle.setText(rTitle[position]);
            myDescription.setText(rDescription[position]);
            return row;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            time = extras.getString("time");
            bikeId = extras.getString("bikeId");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.worldleaderboard:
                startActivity(new Intent(this, LeaderboardActivity.class));
                break;
            case R.id.returnbikes:
                SharedPreferences preferences = getSharedPreferences("username", getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", user_name);
                editor.commit();
                SharedPreferences preferences2 = getSharedPreferences("total_credit", getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor2 = preferences2.edit();
                editor2.putString("total_credit", total_credit);
                editor2.commit();
                Intent intent1 = new Intent(this, RentBikeActivity.class);
                Intent intent2 = new Intent(this, BikeUsingActivity.class);
                intent2.putExtra("bikeId", bikeId);
                intent2.putExtra("username", user_name);
                if(total_credit!=null && Integer.parseInt(total_credit)>0)
                    startActivity(intent1);
                else
                    showDialogForWarning(this, "NOT ENOUGH CREDIT !");
                break;
            case R.id.map:
                goToMapActivity(v);
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.time_cardView:
                try{
                    showDialogForTime(this);
                }catch(JSONException e){
                    e.printStackTrace();
                }
                break;
            case R.id.co2_cardView:
                showDialogForCO2(this);
                break;
            case R.id.credit_cardView:
                showDialogForCredit(this);
                break;
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public void goToMapActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(intent);
    }

    public void postMethod(final String requestUrl) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(requestUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();
                    JSONObject jsonParam = new JSONObject();
                    JSONArray tagsArray = new JSONArray(Arrays.asList());
                    //jsonParam.put("", );
                    Log.i("JSON", jsonParam.toString());
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", conn.getResponseMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
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
        TextView text = (TextView)dialog.findViewById(R.id.txt_file_path);
        text.setText(msg);
        TextView tv_info = (TextView)dialog.findViewById(R.id.tv_info);
        tv_info.setText("Your credit: "+total_credit+"  ||  You must buy credit.");
        Button dialogBtn_okay = (Button)dialog.findViewById(R.id.btn_okay);
        dialogBtn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialogForTime(Activity activity) throws JSONException {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogbox_for_time);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tv_currentCreditCount = (TextView)dialog.findViewById(R.id.tv_currentCreditCount);
        tv_currentCreditCount.setText(_time_double+" MIN");
        ListView listView = (ListView)dialog.findViewById(R.id.list_view_for_time);
        itemParser();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data_list_inString);
        listView.setAdapter(arrayAdapter);
        Button close_button = (Button)dialog.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialogForCO2(Activity activity) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogbox_for_co2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tv_currentCO2Recovery = (TextView)dialog.findViewById(R.id.tv_currentCO2Recovery);
        tv_currentCO2Recovery.setText(co2String+" KG");
        TextView tv_co2Info = (TextView)dialog.findViewById(R.id.tv_co2Info);
        tv_co2Info.setText("Çevre dostu bisikletler ile karbon salınımına savaş açıyoruz.\n"+
                "Günlük koşuşturma içerisinde bizi gideceğimiz yere hızlı ve kolay bir şekilde götüren motorlu taşıtları tercih ediyoruz."+
                "Bu nedenle çoğu zaman çevremizdeki birçok güzelliği keşfedemediğimiz gibi kirliliği de fark etmiyoruz."+
                "Hatta her bir yolcu için 271 gram karbon emisyonu üreten araçlar ve 101 gram karbon emisyonu üreten otobüsler ile "+
                "hem çevreyi hem de insan sağlığını tehdit etmeye devam ediyoruz."+
                "Bu sorunun önüne geçebilecek en önemli çözüm yollarından biri ise kilometre başına karbon emisyonunu 21 grama düşüren bisiklet kullanımını artırmak."+
                "Bugün bir yolcu araçla 271, otobüsle 101 gram karbon salıyor. Bisiklet kullanıcıları ise kilometre başına sadece 21 gram karbon emisyonu üretiyor."+
                "Özellikle yakın mesafelerde bisiklet kullanımının artması; karbon emisyonlarını azaltıyor, obeziteyi önlüyor ve daha gürültüsüz "+
                "şehirlerde yaşamaya imkan sağlıyor.");
        Button close_button = (Button)dialog.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialogForCredit(Activity activity) { //TODO: cardviewler satin alma islevi icin tiklanabilir hale getirilecek
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogbox_for_credit);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tv_currentCreditCount = (TextView)dialog.findViewById(R.id.tv_currentCreditCount);
        tv_currentCreditCount.setText(total_credit);
        CardView justACycle_cardView = (CardView)dialog.findViewById(R.id.justACycle_cardView);
        CardView procycler_cardView = (CardView)dialog.findViewById(R.id.procycler_cardView);
        CardView cycleAddict_cardView = (CardView)dialog.findViewById(R.id.cycleAddict_cardView);
        Button close_button = (Button)dialog.findViewById(R.id.close_button);
        justACycle_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("JUST A CYCLE");
            }
        });
        procycler_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("PROCYCLE");
            }
        });
        cycleAddict_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CYCLEADDICT");
            }
        });
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialogForEarnCredit(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialogbox_for_warning);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView text = (TextView)dialog.findViewById(R.id.txt_file_path);
        text.setText("THANK YOU");
        TextView text2 = (TextView)dialog.findViewById(R.id.tv_info);
        text2.setText("You earned "+earnCredit+" credits for dropping the bike on hotpoints.");
        Button dialogBtn_okay = (Button)dialog.findViewById(R.id.btn_okay);
        dialogBtn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void getRequestForUsageData() {
        // REST API FOR GETTING USAGE DATA
        MyAsyncForGetUsageData dataGetter = new MyAsyncForGetUsageData();
        String usage_data = null;
        try {
            usage_data = dataGetter.execute("https://Bikepass.herokuapp.com/API/app.php").get();
        }catch(ExecutionException e){
            e.printStackTrace();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    public void getRequestForTimeAndCredit() {
        // REST API FOR GETTING TIME AND CREDIT
        MyAsync async = new MyAsync();
        String time_and_credit = null;
        try{
            time_and_credit = async.execute("https://Bikepass.herokuapp.com/API/app.php").get();
        }catch(ExecutionException e){
            e.printStackTrace();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        if(time!=null){
            time = time_and_credit.substring(0, time_and_credit.indexOf(" "));
            total_credit = time_and_credit.substring(time_and_credit.indexOf(" ")+1);
            double time_double = Double.parseDouble(time);
            time_double = time_double/60;
            DecimalFormat df2 = new DecimalFormat("#.##");
            _time_double = df2.format(time_double);
            totalTimeCount.setText(_time_double + " min");
            double _time = Double.parseDouble(time);
            double co2 = (_time/180.0)*0.271;
            DecimalFormat df = new DecimalFormat("#.###");
            co2String = df.format(co2);
            totalRecoveryCount.setText(co2String+" kg");
            totalCreditCount.setText(total_credit+" Credit");
        }
    }

    class MyAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] urls) {
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL url = null;
            String response = null;
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("username", user_name);
            }catch(JSONException e){
                e.printStackTrace();
            }
            try{
                url = new URL(urls[0]);
                connection = (HttpURLConnection)url.openConnection();
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
                while((line = reader.readLine())!=null) {
                    sb.append(line + "\n");
                }
                response = sb.toString().trim();
                JSONObject jObj = new JSONObject(response);
                time = jObj.getString("bike using time");
                total_credit = jObj.getString("total_credit");
                isr.close();
                reader.close();
                Log.i("time", time);
                Log.i("total_credit", total_credit);
                return time+" "+total_credit;
            }catch(IOException e){
                e.printStackTrace();
            }catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    class MyAsyncForGetUsageData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] urls) {
            data_list = new JSONArray();
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL url = null;
            String response = null;
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("username", user_name);
                jsonObject.put("type", "time");
                jsonObject.put("all", true);
                jsonObject.put("date", true);
            }catch(JSONException e){
                e.printStackTrace();
            }
            try{
                url = new URL(urls[0]);
                connection = (HttpURLConnection)url.openConnection();
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
                while((line = reader.readLine())!=null){
                    sb.append(line+"\n");
                }
                response = sb.toString().trim();
                JSONObject jObj = new JSONObject(response);
                data_list = jObj.getJSONArray("data");
                isr.close();
                reader.close();
                return data_list.toString();
            }catch(IOException e){
                e.printStackTrace();
            }catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public void itemParser() throws JSONException { // parse from json object array to string array
        if(data_list.length()!=0){
            for(int i=0;i<data_list.length();i++){
                String date = data_list.getJSONObject(i).getString("day");
                String time = data_list.getJSONObject(i).getString("bike_using_time");
                ListItem item = new ListItem(date, time);
                data_list_inString.add(item.toString());
            }
        }
    }

    class ListItem {
        private String date;
        private String time;
        public ListItem(String date, String time) {
            this.date = date;
            this.time = time;
        }
        public String toString() {
            return "Date: "+date+"  ||  Usage: "+time+" sec";
        }
    }
}