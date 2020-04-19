package com.final_project.bikepass_android.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.SkuDetails;
import com.final_project.bikepass_android.R;

import java.util.List;

/**
 * Created by mustafatozluoglu on 20.04.2020
 */
public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.MyViewHolder> {

    ReportsActivity reportsActivity;
    List<SkuDetails> skuDetailsList;
    BillingClient billingClient;

    public CreditAdapter(ReportsActivity reportsActivity, List<SkuDetails> skuDetailsList, BillingClient billingClient) {
        this.reportsActivity = reportsActivity;
        this.skuDetailsList = skuDetailsList;
        this.billingClient = billingClient;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(reportsActivity.getBaseContext())
                .inflate(R.layout.layout_product_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.txt_product.setText(skuDetailsList.get(i).getTitle());

        //product click
        myViewHolder.setiProductClickListener(new IProductClickListener() {
            @Override
            public void onProductClickListener(View view, int position) {
                /*BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList.get(i)) //TODO: burada hata var
                        .build();
                billingClient.launchBillingFlow(reportsActivity, billingFlowParams);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return skuDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_product;

        IProductClickListener iProductClickListener;

        public void setiProductClickListener(IProductClickListener iProductClickListener) {
            this.iProductClickListener = iProductClickListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_product = (TextView) itemView.findViewById(R.id.txt_product_item);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            iProductClickListener.onProductClickListener(view, getAdapterPosition());
        }
    }


}
