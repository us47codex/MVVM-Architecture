package com.us47codex.mvvmarch.constant;

public class EndPoints {
    public static final String NETWORK_PING_URL = "https://clients3.google.com/generate_204";

    public static final String BASE_URL = "https://sunteccustomercare.in";

    public static final String API = "/api";

    public static final String BASE_URL_API_API = BASE_URL + API +API;

    public static final String LOGIN = BASE_URL_API_API + "/login";
    public static final String PROFILE = BASE_URL_API_API + "/profile";
    public static final String UPDATE_PROFILE = BASE_URL_API_API + "/post_profile";
    public static final String OTP_SEND = BASE_URL_API_API + "/otp-send";
    public static final String OTP_PASSWORD_UPDATE = BASE_URL_API_API + "/otp-password-update";
    public static final String DASHBOARD = BASE_URL_API_API + "/dashboard";
    public static final String COMPLAINT_DETAIL = BASE_URL_API_API + "/complain-detail";
    public static final String COMPLAINT_LIST = BASE_URL_API_API + "/complain-list";
    public static final String CHANGE_PASSWORD = BASE_URL_API_API + "/change-password";
    public static final String SCHEDULE_DATE = BASE_URL_API_API + "/schedule-date";
    public static final String COMPLAIN_SCHEDULE = BASE_URL_API_API + "/complain-schedule";
    public static final String GET_REPORT_NO = BASE_URL_API_API + "/get_repoprt_no";
    public static final String WORK_START = BASE_URL_API_API + "/work_start";
    //    Complain Visit Heat Pump, HOT Water Generator, Dyare
    public static final String HEAT_PUMP_COMPLAIN_VISIT = BASE_URL_API_API + "/complain-visit";
    //    Complain Visit Burner installation & commissionin AND Pre-installed
    public static final String BURNER_INSTALLATION_COMPLAINT_VISIT = BASE_URL_API_API + "/complain-visit";
    //    Complain Visit Burner Service/Breakdown AND AMC
    public static final String BURNER_SERVICE_COMPLAINT_VISIT = BASE_URL_API_API + "/complain-visit";
}
