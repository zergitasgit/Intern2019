package com.example.koreanapp.Controller.Main.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.koreanapp.Model.ContactResult;
import com.example.koreanapp.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    public Context context;
    public List<ContactResult> data;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<ContactResult> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item_layout, viewGroup, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder contactViewHolder, final int i) {
        ContactResult contactResult = data.get(i);
        contactViewHolder.tvContactName.setText(contactResult.getName());
        contactViewHolder.tvContactPhone.setText(contactResult.getPhone());
        contactViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "" + i, Toast.LENGTH_SHORT).show();
            }
        });
        contactViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", data.get(i).getPhone(), null)));

            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvContactName;
        TextView tvContactPhone;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tv_contact_name);
            tvContactPhone = itemView.findViewById(R.id.tv_contact_phone);
        }
    }
}
