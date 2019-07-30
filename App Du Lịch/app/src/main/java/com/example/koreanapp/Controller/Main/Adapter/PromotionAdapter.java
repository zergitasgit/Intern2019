package com.example.koreanapp.Controller.Main.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.koreanapp.Model.PromotionResult;
import com.example.koreanapp.R;
import com.example.koreanapp.WonderVN.PlaceInformationActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder> {
    public Context context;
    public List<PromotionResult> data;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<PromotionResult> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.promotion_item_layout, viewGroup, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder promotionViewHolder, final int i) {
        PromotionResult promotionResult = data.get(i);
        promotionViewHolder.tvPromotionName.setText(promotionResult.getPlaceResult().getPlaceName());
        promotionViewHolder.tvPromotionAddress.setText(promotionResult.getPlaceResult().getAddress());
        Picasso.get().load(promotionResult.getPlaceResult().getUrlLogoPlace()).into(promotionViewHolder.imgPromotion);

        // chuyen mang hinh
        promotionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlaceInformationActivity.class);
                intent.putExtra("object",data.get(i).getPlaceResult());
                context.startActivity(intent);


            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PromotionViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPromotion;
        TextView tvPromotionAddress;
        TextView tvPromotionName;

        public PromotionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPromotionName = itemView.findViewById(R.id.tv_promotion_name);
            tvPromotionAddress = itemView.findViewById(R.id.tv_promotion_address);
            imgPromotion = itemView.findViewById(R.id.img_promotion);
        }
    }
}
