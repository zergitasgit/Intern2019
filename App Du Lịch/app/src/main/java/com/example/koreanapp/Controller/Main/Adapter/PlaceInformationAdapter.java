package com.example.koreanapp.Controller.Main.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.koreanapp.Model.Media;
import com.example.koreanapp.Model.PlaceResult;
import com.example.koreanapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaceInformationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<Object> data;
    public Context context;

    public PlaceInformationAdapter(ArrayList<Object> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof PlaceResult) {
            return 0;
        } else
            return 1;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.place_header_item_layout, viewGroup, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.place_media_item_layout, viewGroup, false);
            return new MediaViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (data.get(i) instanceof PlaceResult) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            if (((PlaceResult) data.get(i)).getUrlLogoPlace().isEmpty()) {
                Picasso.get().load("https://nato-pa.int/sites/default/files/default_images/default-image.jpg").into(headerViewHolder.imgInformation);
            }
            else {
                Picasso.get().load(((PlaceResult) data.get(i)).getUrlLogoPlace()).placeholder(R.mipmap.img_default).into(headerViewHolder.imgInformation);

            }
            headerViewHolder.tvInformationName.setText(((PlaceResult) data.get(i)).getPlaceName());
            headerViewHolder.getTvInformationPhone.setText(((PlaceResult) data.get(i)).getPhone());
            headerViewHolder.tvInformationLocation.setText(((PlaceResult) data.get(i)).getAddress());
            headerViewHolder.tvInformationWebsite.setText(((PlaceResult) data.get(i)).getUrlWeb());
            headerViewHolder.tvInformationDescription.setText(((PlaceResult) data.get(i)).getDescription());
        } else {
            MediaViewHolder mediaViewHolder = (MediaViewHolder) viewHolder;
            mediaViewHolder.configWithMedia((Media) data.get(i));


        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMedia;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMedia = itemView.findViewById(R.id.img_media);
        }

        void configWithMedia(Media media) {
            Picasso.get().load(media.getUrlID()).placeholder(R.mipmap.img_default).into(imgMedia);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvInformationName, getTvInformationPhone, tvInformationLocation, tvInformationWebsite, tvInformationDescription;
        ImageView imgInformation;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInformationName = itemView.findViewById(R.id.tv_infomation_place_name);
            getTvInformationPhone = itemView.findViewById(R.id.tv_infomation_place_phone);
            tvInformationLocation = itemView.findViewById(R.id.tv_infomation_place_location);
            tvInformationWebsite = itemView.findViewById(R.id.tv_infomation_place_website);
            tvInformationDescription = itemView.findViewById(R.id.tv_infomation_place_description);
            imgInformation = itemView.findViewById(R.id.img_information_place);
        }
    }


}
