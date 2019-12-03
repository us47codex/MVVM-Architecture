package com.us47codex.mvvmarch.helper;

import com.google.gson.annotations.SerializedName;

public class ErrorMessageHandlerModel {

    @SerializedName("code")
    public int code;

    @SerializedName("err_code")
    public String errCode;

    @SerializedName("message")
    public String errorMessages;

//    @SerializedName("message")
//    public final errorMessages errorMessages = new errorMessages();
//
//    public class errorMessages {
//        public String en;
//    }
}
