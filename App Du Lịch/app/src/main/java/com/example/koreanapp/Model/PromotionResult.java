package com.example.koreanapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PromotionResult implements Serializable {

@SerializedName("promotionID")
@Expose
public Integer promotionID;
@SerializedName("urlImage")
@Expose
public String urlImage;
@SerializedName("promotionName")
@Expose
public String promotionName;
@SerializedName("placeDetail")
@Expose
public PlaceResult placeResult;

    public Integer getPromotionID() {
        return promotionID;
    }

    public void setPromotionID(Integer promotionID) {
        this.promotionID = promotionID;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public PlaceResult getPlaceResult() {
        return placeResult;
    }

    public void setPlaceResult(PlaceResult placeResult) {
        this.placeResult = placeResult;
    }
}