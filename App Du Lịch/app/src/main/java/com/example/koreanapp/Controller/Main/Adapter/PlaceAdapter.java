package com.example.koreanapp.Controller.Main.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.koreanapp.Model.PlaceResult;
import com.example.koreanapp.WonderVN.PlaceInformationActivity;
import com.example.koreanapp.R;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    public Context context;
    public List<PlaceResult> data;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<PlaceResult> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.place_item_layout, viewGroup, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlaceViewHolder placeViewHolder, final int i) {
        final PlaceResult placeResult = data.get(i);
        placeViewHolder.tvPlaceTitle.setText(placeResult.getPlaceName());
        placeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlaceInformationActivity.class);
                intent.putExtra("object", placeResult);
                context.startActivity(intent);

            }
        });
        if (data.get(i).getIsPromotion() == 1) {
            placeViewHolder.tvPlaceVip.setVisibility(View.VISIBLE);
        } else {
            placeViewHolder.tvPlaceVip.setVisibility(View.INVISIBLE
            );
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaceTitle;
        TextView tvPlaceVip;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaceTitle = itemView.findViewById(R.id.tv_place_title);
            tvPlaceVip = itemView.findViewById(R.id.tv_place_vip);
        }
    }
}
