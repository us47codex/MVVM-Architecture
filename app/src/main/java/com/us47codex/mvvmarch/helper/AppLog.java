package com.us47codex.mvvmarch.helper;

import android.util.Log;

import com.us47codex.mvvmarch.BuildConfig;
import com.us47codex.mvvmarch.constant.Config;
import com.us47codex.mvvmarch.constant.Endpoint;


/**
 * Created by Upen on 20/9/17.
 */

public class AppLog {

    /**
     * Logging and Console
     */
    private static String TAG = AppLog.class.getSimpleName();
    private static boolean DO_LOG = BuildConfig.DEBUG;
    public static boolean DO_API_LOG = BuildConfig.DEBUG;
    public static boolean DO_FCR = BuildConfig.DEBUG;
    private static String HOST = Endpoint.BASE_URL;

    public static void print(String mesg) {
        if (AppLog.DO_LOG) {
            System.out.println(mesg);
        }
    }

    public static void print(String title, String mesg) {
        if (AppLog.DO_LOG) {
            System.out.println(title + " :: " + mesg);
        }
    }

    public static void print(String tag, String title, String mesg) {
        if (AppLog.DO_LOG) {
            System.out.println(tag + " :: " + title + " :: " + mesg);
        }
    }

    public static void verbose(String title, String mesg) {
        if (AppLog.DO_LOG) {
            Log.v(title, mesg);
        }
    }

    public static void verbose(String title, String mesg, Throwable throwable) {
        if (AppLog.DO_LOG) {
            Log.v(title, mesg, throwable);
        }
    }

    public static void debug(String title, String mesg) {
        if (AppLog.DO_LOG) {
            Log.d(title, mesg);
        }
    }

    public static void debug(String title, String mesg, Throwable throwable) {
        if (AppLog.DO_LOG) {
            Log.d(title, mesg, throwable);
        }
    }

    public static void warn(String title, String mesg) {
        if (AppLog.DO_LOG) {
            Log.w(title, mesg);
        }
    }

    public static void warn(String title, Throwable throwable) {
        if (AppLog.DO_LOG) {
            Log.w(title, throwable);
        }
    }

    public static void warn(String title, String mesg, Throwable throwable) {
        if (AppLog.DO_LOG) {
            Log.w(title, mesg, throwable);
        }
    }

    public static void info(String title, String mesg, Throwable throwable) {
        if (AppLog.DO_LOG) {
            Log.i(title, mesg, throwable);
        }
    }

    public static void info(String title, String mesg) {
        if (AppLog.DO_LOG) {
            Log.i(title, mesg);
        }
    }

    public static void error(String title, String mesg) {
        if (AppLog.DO_LOG) {
            Log.e(title, mesg);
        }
    }

    public static void error(String title, Exception e) {
        if (AppLog.DO_LOG) {
            Log.e(title, e.getMessage());
        }
    }

    public static void error(String title, String mesg, Exception e) {
        if (AppLog.DO_LOG) {
            Log.e(title, mesg, e);
        }
    }

    public static void error(String title, String mesg, Throwable t) {
        if (AppLog.DO_LOG) {
            Log.e(title, mesg, t);
        }
    }

    public static void error(String title, Throwable t) {
        if (AppLog.DO_LOG) {
            Log.e(title, "", t);
        }
    }

    public static void errorFCR(String title, Throwable t) {
        if (AppLog.DO_LOG) {
            Log.e(Config.APP_NAME + " HOST :: " + HOST, title, t);
        }
//        if (AppLog.DO_FCR) {
//            Crashlytics.logException(new Exception(Config.APP_NAME + " HOST :: " + HOST + " Title :: " + title, t));
//            try {
//                Crashlytics.logException(new Exception(Config.APP_NAME + " HOST :: " + HOST + " Title :: " + title, t));
//            } catch (Exception e) {
//                Crashlytics.logException(new Exception(Config.APP_NAME + " HOST :: " + HOST + " Title :: " + title + " ", t));
//                Crashlytics.logException(new Exception(Config.APP_NAME + " HOST :: " + HOST + " Title :: " + title + " ", e));
//            }
//        }
    }

    public static void errorFCR(Throwable t) {
        if (AppLog.DO_LOG) {
            Log.e(Config.APP_NAME, " HOST :: " + HOST, t);
        }
//        if (AppLog.DO_FCR) {
//            try {
//                Crashlytics.logException(new Exception(Config.APP_NAME + " HOST :: " + HOST, t));
//            } catch (Exception e) {
//                Crashlytics.logException(new Exception(Config.APP_NAME + " HOST :: " + HOST + " ", t));
//                Crashlytics.logException(new Exception(Config.APP_NAME + " HOST :: " + HOST + " ", e));
//            }
//        }
    }
}