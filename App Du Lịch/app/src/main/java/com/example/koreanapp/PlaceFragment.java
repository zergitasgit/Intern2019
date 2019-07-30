package com.example.koreanapp;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.koreanapp.Controller.Main.Adapter.PlaceAdapter;
import com.example.koreanapp.Model.Place;
import com.example.koreanapp.WonderVN.WonderVNAPIService;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceFragment extends Fragment {
    View vRoot;
    RecyclerView rvPlace;

    public PlaceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vRoot = inflater.inflate(R.layout.fragment_place, container, false);
        init();
        getData();
        return vRoot;
    }

    private void getData() {
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMessage("Loading..........");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        // khai báo , khởi tạo retrofit
        GetListPlaceBody getListPlaceBody = new GetListPlaceBody(0, 0, "");
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://150.95.115.192/api/")
                .build();

        retrofit.create(WonderVNAPIService.class).getListPlace(getListPlaceBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String strJson = null;
                try {
                    strJson = response.body().string();

                    //------------ conFigRvPlace -------
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    rvPlace.setLayoutManager(linearLayoutManager);
                    PlaceAdapter adapter = new PlaceAdapter();

                    adapter.setContext(getContext());
                    adapter.setData(place.getPlaceResults());
                    rvPlace.setAdapter(adapter);
                    rvPlace.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                    progressDoalog.dismiss();


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Lấy dữ liệu thất bại", Toast.LENGTH_SHORT).show();
                progressDoalog.dismiss();

            }
        });
    }

    class GetListPlaceBody {
        int catID, placeID;
        String searchKey;

        public GetListPlaceBody(int catID, int placeID, String searchKey) {
            this.catID = catID;
            this.placeID = placeID;
            this.searchKey = searchKey;
        }
    }

    private void init() {
        rvPlace = vRoot.findViewById(R.id.rv_Place);

    }

}
