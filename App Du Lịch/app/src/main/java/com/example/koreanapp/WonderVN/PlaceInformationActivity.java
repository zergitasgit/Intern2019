package com.example.koreanapp.WonderVN;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.koreanapp.Controller.Main.Adapter.PlaceInformationAdapter;
import com.example.koreanapp.Model.PlaceResult;
import com.example.koreanapp.R;

import java.util.ArrayList;

public class PlaceInformationActivity extends AppCompatActivity {
    RecyclerView rvPlaceInformation;
    Toolbar tbPlaceInformation;
    ArrayList<Object> data = new ArrayList<>();
    PlaceInformationAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_information);
        init();
        setToolbar();
        conFigRv();
        getData();
    }

    private void setToolbar() {
        setSupportActionBar(tbPlaceInformation);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tbPlaceInformation.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PlaceInformationActivity.this, "ok", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void conFigRv() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvPlaceInformation.setLayoutManager(layoutManager);
        adapter = new PlaceInformationAdapter(data, this);
        rvPlaceInformation.setAdapter(adapter);


    }

    private void getData() {
        PlaceResult placeResult = (PlaceResult) getIntent().getSerializableExtra("object");
        data.add(placeResult);
        for (int i = 0; i < placeResult.getListMedia().size(); i++) {
            data.add(placeResult.getListMedia().get(i));
        }
        adapter.notifyDataSetChanged();


    }

    private void init() {
        rvPlaceInformation = findViewById(R.id.rv_place_information);
        tbPlaceInformation = findViewById(R.id.tb_place_information);
    }
}
