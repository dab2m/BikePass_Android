package com.example.bikepass_android.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bikepass_android.R;
import com.example.bikepass_android.activity.UsageData;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dilan on 10.03.2020
 */
public class UsageDataListAdapter extends ArrayAdapter<UsageData> {

    private static final String TAG="UsageDataAdapter";
    private Context mcontext;
    int mresource;


    public UsageDataListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<UsageData> objects) {
        super(context, resource, objects);
        mcontext=context;
        mresource=resource;
    }


     @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String user_name=getItem(position).getUserName();
        int bike_usage=getItem(position).getUsage();

        UsageData usageData=new UsageData(bike_usage,user_name);

        LayoutInflater inflater=LayoutInflater.from(mcontext);
        convertView=inflater.inflate(mresource,parent,false);

        TextView user_name_view=(TextView) convertView.findViewById(R.id.textViewUser);
        TextView bike_usage_time=(TextView)convertView.findViewById(R.id.textViewDk);
        ImageView bike_image=(ImageView)convertView.findViewById(R.id.imageView);

        user_name_view.setText(user_name);
        bike_usage_time.setText(bike_usage+" sec");
        bike_image.setImageResource(R.drawable.bike_busy);
        if(user_name.equals("You")) {
            user_name_view.setTextColor(Color.MAGENTA);
            bike_usage_time.setTextColor(Color.MAGENTA);
            bike_image.setImageResource(R.drawable.deneme);
        }
        return convertView;
    }
}
