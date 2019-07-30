package com.example.koreanapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListBanner {

@SerializedName("urlBanner")
@Expose
public String urlBanner;
@SerializedName("place")
@Expose
public PlaceHomeImage placeHomeImage;

    public String getUrlBanner() {
        return urlBanner;
    }

    public void setUrlBanner(String urlBanner) {
        this.urlBanner = urlBanner;
    }

    public PlaceHomeImage getPlaceHomeImage() {
        return placeHomeImage;
    }

    public void setPlaceHomeImage(PlaceHomeImage placeHomeImage) {
        this.placeHomeImage = placeHomeImage;
    }
}