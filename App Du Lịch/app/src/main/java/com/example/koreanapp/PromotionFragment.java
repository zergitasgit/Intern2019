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

import com.example.koreanapp.Controller.Main.Adapter.PromotionAdapter;
import com.example.koreanapp.Model.Promotion;
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
public class PromotionFragment extends Fragment {
    View vRoot;
    RecyclerView rvPromotion;

    public PromotionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vRoot = inflater.inflate(R.layout.fragment_promotion, container, false);
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
        GetListPromoionBody getListPromoionBody = new GetListPromoionBody(0,0);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://150.95.115.192/api/")
                .build();
        retrofit.create(WonderVNAPIService.class).getListPromotion(getListPromoionBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String strJson = response.body().string();
                    Gson gson = new Gson();
                    Promotion promotion = gson.fromJson(strJson, Promotion.class);
                    //-----------conFigRvPromotion
                    LinearLayoutManager linearLayoutManager = new
                            LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    rvPromotion.setLayoutManager(linearLayoutManager);
                    PromotionAdapter adapter = new PromotionAdapter();
                    adapter.setContext(getContext());
                    adapter.setData(promotion.getResult());
                    rvPromotion.setAdapter(adapter);
                    rvPromotion.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                    progressDoalog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDoalog.dismiss();

            }
        });

    }

    class GetListPromoionBody {
        Integer page, promotionID;

        public GetListPromoionBody(Integer page, Integer promotionID) {
            this.page = page;
            this.promotionID = promotionID;
        }
    }

    private void init() {
        rvPromotion = vRoot.findViewById(R.id.rv_promotion);
    }

}
