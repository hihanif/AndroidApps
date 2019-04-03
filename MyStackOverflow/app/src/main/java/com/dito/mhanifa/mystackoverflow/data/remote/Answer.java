package com.dito.mhanifa.mystackoverflow.data.remote;

import com.google.gson.annotations.SerializedName;

public class Answer {

    @SerializedName("answer_id")
    public int answerId;

    @SerializedName("is_accepted")
    public boolean isAccepted;

    public int score;

    @Override
    public String toString() {
        return "Answer{" +
                "answerId=" + answerId +
                ", isAccepted=" + isAccepted +
                ", score=" + score +
                '}';
    }
}
