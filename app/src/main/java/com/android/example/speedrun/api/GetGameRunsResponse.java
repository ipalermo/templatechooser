package com.android.example.speedrun.api;

import com.android.example.speedrun.vo.Run;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetGameRunsResponse {
    @SerializedName("data")
    private List<Run> runs;

    public List<Run> getRuns() {
        return runs;
    }

    public void setRuns(List<Run> runs) {
        this.runs = runs;
    }
}
