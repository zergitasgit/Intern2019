package com.example.contentprovider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.contentprovider.adapter.ContactAdapter;
import com.example.contentprovider.model.Contact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ContactActivity extends AppCompatActivity {
    RecyclerView rvContact;
    private ContactManager contactManager;
    private List<Contact> data;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        init();
        configRv();
    }

    private void configRv() {
        contactManager = new ContactManager(this);
        data = contactManager.getListContact();

        Collections.sort(data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvContact.setLayoutManager(linearLayoutManager);
        adapter = new ContactAdapter(data, this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvContact.getContext(),
                linearLayoutManager.getOrientation());
        rvContact.addItemDecoration(dividerItemDecoration);
        rvContact.setAdapter(adapter);
    }

    private void init() {
        rvContact = findViewById(R.id.rv_danh_ba);

    }
}
