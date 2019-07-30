package com.example.koreanapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.koreanapp.Controller.Main.Adapter.CategoryAdapter;
import com.example.koreanapp.Model.Category;
import com.example.koreanapp.Model.ListCate;
import com.example.koreanapp.WonderVN.WonderVNAPIService;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {
    View vRoot;
    RecyclerView rvCategory;
    ArrayList<Object> data = new ArrayList<>();

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vRoot = inflater.inflate(R.layout.fragment_category, container, false);
        init();
        getData();
        return vRoot;
    }

    private void init() {
        rvCategory = vRoot.findViewById(R.id.rv_Category);
    }

    private void getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://150.95.115.192/api/")
                .build();
        retrofit.create(WonderVNAPIService.class).getListCategory().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String strJson = null;
                try {
                    strJson = response.body().string();
                    Gson gson = new Gson();
                    Category category = gson.fromJson(strJson, Category.class);
                    data.add(category.getCategoryResult());
                    CategoryAdapter adapter = new CategoryAdapter(getContext(),data);
                   /* LinearLayoutManager layoutManager = new
                            LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                    rvCategory.setLayoutManager(layoutManager);
                    */
                    rvCategory.setLayoutManager(new GridLayoutManager(getContext(), 4));
                    rvCategory.setAdapter(adapter);

                    /*
                    rvCategory.setLayoutManager(new GridLayoutManager(getContext(), 4));
                    CategoryAdapter adapter = new CategoryAdapter();
                    adapter.setContext(getContext());
                    adapter.setData(category.getCategoryResult());
                    rvCategory.setAdapter(adapter);
                    */

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "lấy dữ liệu thất bại", Toast.LENGTH_SHORT).show();

            }
        });


    }


}
