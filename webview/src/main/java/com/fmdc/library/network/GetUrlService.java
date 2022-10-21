package com.fmdc.library.network;

import com.fmdc.library.model.UrlModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetUrlService {

    @GET("/url.json")
    Call<UrlModel> getUrlFromService();
}
