
package com.android.example.speedrun.api;

import android.arch.lifecycle.LiveData;

import com.android.example.speedrun.vo.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * REST API access points
 */
public interface SpeedrunService {
    @GET("users")
    LiveData<ApiResponse<List<User>>> getUsers();

    @GET("users/{id}")
    LiveData<ApiResponse<GetUserResponse>> getUser(@Path("id") String userId);

    @GET("games/{id}")
    LiveData<ApiResponse<GetGameResponse>> getGame(@Path("id") String gameId);

    @GET("games?orderby=released&direction=desc")
    Call<GetGamesResponse> getGamesPage(@Query("offset")Integer pageOffset, @Query("max")Integer pageSize);

    @GET("games?orderby=released&direction=desc")
    LiveData<ApiResponse<GetGamesResponse>> getGames(@Query("offset")Integer pageOffset, @Query("max")Integer pageSize);

    @GET("runs?orderby=date&direction=desc&max=1")
    LiveData<ApiResponse<GetGameRunsResponse>> getGameRuns(@Query("game") String Id);
}