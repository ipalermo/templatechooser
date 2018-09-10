
package com.android.example.templatechooser.api;

import android.arch.lifecycle.LiveData;

import com.android.example.templatechooser.vo.Design;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * REST API access points
 */
public interface RestService {

    @GET("api/published_designs")
    LiveData<ApiResponse<List<String>>> getDesignUrls();

    @Headers("Accept: application/json")
    @GET("designs/{id}/versions/2.0")
    LiveData<ApiResponse<Design>> getDesign(@Path("id") String id);
}