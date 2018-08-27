package com.android.example.speedrun.api;

import com.android.example.speedrun.vo.User;
import com.google.gson.annotations.SerializedName;


public class GetUserResponse {
    @SerializedName("data")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GetUserResponse(User user) {
        this.user = user;
    }
}
