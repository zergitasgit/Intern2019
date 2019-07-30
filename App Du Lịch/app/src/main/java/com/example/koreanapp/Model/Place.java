package com.example.koreanapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Place implements Serializable {

    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("code")
    @Expose
    public Integer code;
    @SerializedName("result")
    @Expose
    public List<PlaceResult> placeResults = null;
    @SerializedName("message")
    @Expose
    public String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<PlaceResult> getPlaceResults() {
        return placeResults;
    }

    public void setPlaceResults(List<PlaceResult> placeResults) {
        this.placeResults = placeResults;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}