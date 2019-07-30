package com.example.koreanapp.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PlaceResult implements Serializable {

    @SerializedName("placeID")
    @Expose
    public Integer placeID;
    @SerializedName("placeName")
    @Expose
    public String placeName;
    @SerializedName("urlLogoPlace")
    @Expose
    public String urlLogoPlace;
    @SerializedName("categoryID")
    @Expose
    public Integer categoryID;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("phone")
    @Expose
    public String phone;
    @SerializedName("urlWeb")
    @Expose
    public String urlWeb;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("urlBanner")
    @Expose
    public Object urlBanner;
    @SerializedName("isMoreDetail")
    @Expose
    public Integer isMoreDetail;
    @SerializedName("isPromotion")
    @Expose
    public Integer isPromotion;
    @SerializedName("longitude")
    @Expose
    public double longitude;
    @SerializedName("latitude")
    @Expose
    public double latitude;
    @SerializedName("kakaoTalk")
    @Expose
    public String kakaoTalk;
    @SerializedName("listMedia")
    @Expose
    public List<Media> listMedia = null;

    public Integer getPlaceID() {
        return placeID;
    }

    public void setPlaceID(Integer placeID) {
        this.placeID = placeID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getUrlLogoPlace() {
        return urlLogoPlace;
    }

    public void setUrlLogoPlace(String urlLogoPlace) {
        this.urlLogoPlace = urlLogoPlace;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrlWeb() {
        return urlWeb;
    }

    public void setUrlWeb(String urlWeb) {
        this.urlWeb = urlWeb;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getUrlBanner() {
        return urlBanner;
    }

    public void setUrlBanner(Object urlBanner) {
        this.urlBanner = urlBanner;
    }

    public Integer getIsMoreDetail() {
        return isMoreDetail;
    }

    public void setIsMoreDetail(Integer isMoreDetail) {
        this.isMoreDetail = isMoreDetail;
    }

    public Integer getIsPromotion() {
        return isPromotion;
    }

    public void setIsPromotion(Integer isPromotion) {
        this.isPromotion = isPromotion;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getKakaoTalk() {
        return kakaoTalk;
    }

    public void setKakaoTalk(String kakaoTalk) {
        this.kakaoTalk = kakaoTalk;
    }

    public List<Media> getListMedia() {
        return listMedia;
    }

    public void setListMedia(List<Media> listMedia) {
        this.listMedia = listMedia;
    }
}