package com.us47codex.mvvmarch.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginParamModel {

    @Expose
    @SerializedName("username")
    public String username;

    @Expose
    @SerializedName("password")
    public String password;
}
