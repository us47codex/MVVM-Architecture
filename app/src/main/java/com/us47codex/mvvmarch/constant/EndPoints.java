package com.us47codex.mvvmarch.constant;

public class EndPoints {
    public static final String NETWORK_PING_URL = "https://clients3.google.com/generate_204";

    public static final String BASE_URL = "https://sunteccustomercare.in";

    public static final String API = "/api";

    public static final String BASE_URL_API_API = BASE_URL + API + API;

    public static final String LOGIN = BASE_URL_API_API + "/login";
    public static final String PROFILE = BASE_URL_API_API + "/profile";
    public static final String UPDATE_PROFILE = BASE_URL_API_API + "/post_profile";
    public static final String OTP_SEND = BASE_URL_API_API + "/otp-send";
    public static final String OTP_PASSWORD_UPDATE = BASE_URL_API_API + "/otp-password-update";
    public static final String COMPLAINT_DETAIL = BASE_URL_API_API + "/complain-detail";
    public static final String COMPLAINT_LIST = BASE_URL_API_API + "/complain-list";
    public static final String CHANGE_PASSWORD = BASE_URL_API_API + "/change-password";
}
