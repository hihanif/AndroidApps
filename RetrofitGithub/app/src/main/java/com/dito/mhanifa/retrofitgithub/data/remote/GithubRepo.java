package com.dito.mhanifa.retrofitgithub.data.remote;

public class GithubRepo {
    public String name;
    public String owner;
    public String url;

    @Override
    public String toString() {
        return "GithubRepo{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
