package com.example.koreanapp.Controller.Main.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.koreanapp.Model.Category;
import com.example.koreanapp.Model.CategoryResult;
import com.example.koreanapp.Model.ListBanner;
import com.example.koreanapp.Model.ListCate;
import com.example.koreanapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context context;
    public ArrayList<Object> data;

    public CategoryAdapter(Context context, ArrayList<Object> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof CategoryResult) {
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.category_item_layout, viewGroup, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.category_image_layout, viewGroup, false);
            return new MediaViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (data.get(i) instanceof CategoryResult){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            headerViewHolder.tvIcon.setText(((CategoryResult) data.get(i)).getListCate().get(i).getName());
            Picasso.get().load(((CategoryResult) data.get(i)).getListCate().get(i).getUrlCategory()).into(headerViewHolder.imgIcon);
        }



    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.img_category_media);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvIcon;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_category_item);
            tvIcon = itemView.findViewById(R.id.tv_category_title);

        }
    }
}
