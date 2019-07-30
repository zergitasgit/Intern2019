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

import com.example.koreanapp.Controller.Main.Adapter.ContactAdapter;
import com.example.koreanapp.Model.Contact;
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
public class ContactFragment extends Fragment {
    View vRoot;
    RecyclerView rvContact;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vRoot =  inflater.inflate(R.layout.fragment_contact, container, false);
        init();
        getData();
        return vRoot;
    }

    private void init() {
        rvContact = vRoot.findViewById(R.id.rv_Contact);
    }

    private void getData() {
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMessage("Loading..........");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        GetListContactBody getListContactBody = new GetListContactBody("madara","madara","",0);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://150.95.115.192/api/")
                .build();
        retrofit.create(WonderVNAPIService.class).getListContact(getListContactBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String strJson = response.body().string();
                    Gson gson = new Gson();
                    Contact contact = gson.fromJson(strJson, Contact.class);
                    // ------------- configRvContact
                    LinearLayoutManager linearLayoutManager = new
                            LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    rvContact.setLayoutManager(linearLayoutManager);
                    ContactAdapter adapter = new ContactAdapter();
                    adapter.setContext(getContext());
                    adapter.setData(contact.getContactResult());
                    rvContact.setAdapter(adapter);
                        rvContact.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
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
    class GetListContactBody {
        String userAPI, passAPI, searchKey;
        int contactID;

        public GetListContactBody(String userAPI, String passAPI, String searchKey, int contactID) {
            this.userAPI = userAPI;
            this.passAPI = passAPI;
            this.searchKey = searchKey;
            this.contactID = contactID;
        }
    }


}
