package com.us47codex.mvvmarch.constant;

public class Constants {

    public static final String APP_NAME = "SunTec";
    public static final String PACKAGE_NAME = "com.us47codex.mvvmarch";

    public static final int NOTIFICATION_ID = 100;
    public static final int INVITE_VENDOR_DEPARTMENT_ID = 101;


    public static final int PROFILE_ID = 1;
    public static final int CHANGE_PASSWORD_ID = 2;
    public static final int COMPLAINTS_ID = 3;
    public static final int ABOUT_US_ID = 4;
    public static final int LOGOUT_ID = 5;

    /*
     * KEYs
     * */
    public static final String KEY_COMPLAIN_ID = "complain_id";
    public static final String KEY_FILTER_COMPLAINT = "filter_complaint";
    public static final String STATUS_CLOSED = "closed";
    public static final String STATUS_OPEN = "open";
    public static final String STATUS_SCHEDULE = "schedule";
    public static final String STATUS_ALL = "all";
    public static final String BURNER = "burner";
    public static final String HEAT_PUMP = "heat pump";
    public static final String DRYER = "dryer";
    public static final String HOT_WATER_GENERATOR = "hot water generator";
    public static final String PRE_INSTALLED = "pre-installed";
    public static final String INSTALLATION_AND_COMMISSIONING = "installation & commissioning";
    public static final String SERVICE_BREAKDOWN = "service/breakdown";
    public static final String AMC = "amc";

    /**
     * pagination
     */
    public static final int LOADING_PAGE_SIZE = 20;
    public static final int PREFETCH_DISTANCE = 2;
    public static final int NUMBERS_OF_THREADS = 3;

    /**
     * API ERROR CODES
     */
    public static final String ERR_CONTAINS = "ERR_";
    public static final String ERR_RECORD_FOUND = "ERR_RECORD_FOUND";
    public static final String ERR_SOMETHING_WRONG = "ERR_SOMETHING_WRONG";
    public static final String TIMEOUT_ERROR = "TIMEOUT_ERROR";
    public static final String NO_CONNECTION_ERROR = "NO_CONNECTION_ERROR";
    public static final String AUTH_FAILURE_ERROR = "AUTH_FAILURE_ERROR";
    public static final String SERVER_ERROR = "SERVER_ERROR";
    public static final String NETWORK_ERROR = "NETWORK_ERROR";
    public static final String PARSE_ERROR = "PARSE_ERROR";
    public static final String SERVER_COULD_NOT_BE_FOUND = "SERVER_COULD_NOT_BE_FOUND";


    /**
     * API
     */
    public static final int API_CALL_TIMEOUT = 60000;
    public static final String API_SUCCESS = "1"; //response success = 1
    public static final String API_FAIL = "0"; //response success = 0
    public static final String API_ERROR = "-1"; //response success = 0
    public static final String KEY_SUCCESS = "success";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CODE = "code";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_ERROR = "error";
    public static final String KEY_DATA = "data";
    public static final String KEY_API_TAG = "api_tag";
    public static final String KEY_TIMEOUT = "timeout";
    public static final String KEY_TIME_OUT = "time out";
    public static final String KEY_AUTHORIZATION = "authorization";
    public static final String KEY_MODEL = "model";
    public static final String KEY_OBJECT = "object";
    public static final String KEY_COMPLAIN_ACTION = "complain_action";
    public static final String SYSTEM_LOCATION_MANAGER_CHANGE = "locationMangerChange";
    public static final int PUSH_TYPE_SYSTEM_LOCATION_CHANGE = 101010;

    /**
     * login error code
     */
    public static final String KEY_ERR_LOGIN_FAIL = "LOGIN_FAILED";


    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int REQUEST_TIMEOUT = 408;
    public static final int SERVER_BROKEN = 500; //internal server error
    public static final int BAD_GATEWAY = 502;
    public static final int SERVICE_UNAVAILABLE = 503;
    public static final int GATEWAY_TIMEOUT = 504;

    public static final int CHANGE_PASSWORD = 5000;

    /**
     * language key
     */
    public static final String KEY_EN = "en";


    /**
     * Complain unique rx worker tag
     */
    public static final String KEY_COMPLAIN_UNIQUE_WORKER = "complain_fcm_socket_worker";

    /**
     * stand for which comment list you want get into comment list app
     */
    public static final int KEY_COMMENT_FOR_NORMAL_COMMENT = 1;
    public static final int KEY_COMMENT_FOR_HISTORY_COMMENT = 2;

    /**
     * Font name
     */
    public static final String CANDARA_FONTS = "fonts/CANDARA.TTF";
    public static final String CANDARAB_FONTS = "fonts/CANDARAB.TTF";

    public static final String MONTSERRAT_BOLD_FONTS = "fonts/Montserrat-Bold.ttf";
    public static final String MONTSERRAT_LIGHT_FONTS = "fonts/Montserrat-Light.ttf";
    public static final String MONTSERRAT_MEDIUM_FONTS = "fonts/Montserrat-Medium.ttf";
    public static final String MONTSERRAT_REGULAR_FONTS = "fonts/Montserrat-Regular.ttf";
    public static final String MONTSERRAT_SEMI_BOLD_FONTS = "fonts/Montserrat-SemiBold.ttf";
    public static final String JURA_BOLD_FONTS = "fonts/Jura-Bold.ttf";

    /**
     *
     */
    public static final int KEY_SEARCH_DEBOUNCE_TIME = 300;
    public static final String SESSION_MANAGER = "TeroTamCustomerPref";
    public static final String SPECIAL_CHARACTERS = "[-+.^:,]";

}
