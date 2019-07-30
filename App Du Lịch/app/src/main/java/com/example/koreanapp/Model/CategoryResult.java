package com.example.koreanapp.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryResult {

@SerializedName("listCate")
@Expose
public List<ListCate> listCate = null;
@SerializedName("listBanner")
@Expose
public List<ListBanner> listBanner = null;

    public List<ListCate> getListCate() {
        return listCate;
    }

    public void setListCate(List<ListCate> listCate) {
        this.listCate = listCate;
    }

    public List<ListBanner> getListBanner() {
        return listBanner;
    }

    public void setListBanner(List<ListBanner> listBanner) {
        this.listBanner = listBanner;
    }
}