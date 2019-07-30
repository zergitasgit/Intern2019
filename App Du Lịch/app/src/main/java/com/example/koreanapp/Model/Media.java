package com.example.koreanapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Media implements Serializable {

@SerializedName("urlID")
@Expose
public String urlID;
@SerializedName("type")
@Expose
public Integer type;

    public String getUrlID() {
        return urlID;
    }

    public void setUrlID(String urlID) {
        this.urlID = urlID;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}