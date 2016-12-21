package com.redgeckotech.beerfinder.api;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import rx.Observable;

public interface BreweryApi {

    @GET("/jbekas/AND_CapstoneProject/master/data/austin.json")
    Observable<Response<ResponseBody>> retrieveAustinBreweries();

}
