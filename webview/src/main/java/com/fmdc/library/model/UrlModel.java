package com.fmdc.library.model;

import com.google.gson.annotations.SerializedName;

public class UrlModel {
    @SerializedName("url")
    private String url;
    @SerializedName("dateModified")
    private String dateModified;

    public  UrlModel (String url, String dateModified){
        this.url = url;
        this.dateModified = dateModified;
    }

    public String getURL() { return url;}
    public String getDateModified() { return dateModified;}

    public void setUrl(String url) {this.url = url;}
    public void setDateModified(String dateModified) {this.dateModified = dateModified;}
}




