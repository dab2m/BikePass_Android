package com.final_project.bikepass_android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Berk on 10.03.2020
 */
public class ReportsAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    Button bLike;
    private String recipeId;
    ArrayList<String> list = new ArrayList<>();

    /*public ReportsAdapter(Activity activity, ArrayList<FoodModel> recipeArrayList){
        this.context = activity;
        this.recipeArrayList = recipeArrayList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }*/

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    /*@Override
    public Object getItem(int position) {
        return recipeArrayList.get(position);
    }*/

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    /*@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        /*View view = layoutInflater.inflate(R.layout.item_recipe_with_like, null);
        TextView recipeName = (TextView) view.findViewById(R.id.yemekIsmi);
        ImageView recipeImage = (ImageView) view.findViewById(R.id.yemekResmi);
        new DownLoadImageTask(recipeImage).execute(recipeArrayList.get(position).getFoodImage());
        TextView recipeCreated = (TextView) view.findViewById(R.id.yemekEkleyen);
        TextView recipeDecription = (TextView) view.findViewById(R.id.yemekAciklamasi);
        TextView recipeTags = (TextView) view.findViewById(R.id.yemekEtiketleri);
        bLike = (Button) view.findViewById(R.id.bBegen);
        bLike.setClickable(true);
        bLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bLike.setClickable(false);
                recipeId = recipeArrayList.get(position).getFoodId();
                SharedPreferences prefs = context.getSharedPreferences("MyApp", MODE_PRIVATE); // LoginActivity sayfasindan username'i almak icin kullanildi!
                String username = prefs.getString("username", "UNKNOWN");
                if (list.isEmpty()) {
                    list.add(recipeId);
                    likePost(recipeId, username);
                }
                else {
                    if(!list.contains(recipeId)) {
                        list.add(recipeId);
                        likePost(recipeId, username);
                    }
                }
            }
        });
        recipeName.setText(recipeArrayList.get(position).getFoodName());
        recipeCreated.setText(recipeArrayList.get(position).getFoodCreated() + " bu tarifi ekledi");
        recipeDecription.setText(recipeArrayList.get(position).getFoodDescription());
        recipeTags.setText(recipeArrayList.get(position).displayTags());
        bLike.setText(recipeArrayList.get(position).getFoodLikes()+"");
        return view;
    }*/

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is;
                if(urlOfImage.contains("no.png")){
                    is = new URL("https://res.cloudinary.com/dewae3den/image/upload/v1563364160/no_anet7u.png").openStream();
                }else{
                    is = new URL(urlOfImage).openStream();
                }
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){
                e.printStackTrace();
            }
            return logo;
        }

        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }

    public void likePost(final String recipeId, final String username) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("like", recipeId);
                    jsonParam.put("username", username);
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
}