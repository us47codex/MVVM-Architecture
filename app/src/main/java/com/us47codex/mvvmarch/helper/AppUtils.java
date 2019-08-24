package com.us47codex.mvvmarch.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.us47codex.mvvmarch.BuildConfig;
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.constant.Constant;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();

    public static boolean isEmpty(String strValue) {
        return TextUtils.isEmpty(strValue) || strValue.equals("null") ||
                strValue.equals("undefine") || strValue.equals("{}");
    }

    @SuppressWarnings("Annotator")
    public static boolean isValidEmailId(String email) {

        //noinspection Annotator
        return !Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static boolean isValidMobile(String phone) {
        boolean check;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            check = phone.length() >= 6 && phone.length() <= 13;
        } else {
            check = false;
        }
        return !check;
    }


    public static int getWidth(Activity activity) {
        try {
            Display display = activity.getWindowManager()
                    .getDefaultDisplay();
            Point screenSize = new Point();
            display.getSize(screenSize);
            int width = screenSize.x;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = (int) (width - (width * 0.05));
            return layoutParams.width;
        } catch (Exception e) {
            return 400;
        }
    }

    public static boolean isValidPassword(String password) {
        Pattern pattern;
        Matcher matcher;
        // final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return !matcher.matches();
    }

    public static int createRandomNo() {
        Random rand = new Random();
        int n = rand.nextInt(1000);
        n += 1;
        return n;
    }

    public static Single<String> getErrorMessageFRomErrorCode(int errorCode) {
        return Single.just(errorCode)
                .subscribeOn(Schedulers.io())
                .map(code -> {
                    String message;
                    switch (code) {
                        case Constant.BAD_REQUEST:
                            message = "Bad Request";
                            break;
                        case Constant.UNAUTHORIZED:
                            message = "Unauthorized";
                            break;
                        case Constant.FORBIDDEN:
                            message = "Forbidden";
                            break;
                        case Constant.NOT_FOUND:
                            message = "Not Found";
                            break;
                        case Constant.REQUEST_TIMEOUT:
                            message = "Request Timeout";
                            break;
                        case Constant.SERVER_BROKEN:
                            message = "Server Broken"; //internal server error
                            break;
                        case Constant.BAD_GATEWAY:
                            message = "Bad Gateway";
                            break;
                        case Constant.SERVICE_UNAVAILABLE:
                            message = "Service Unavailable";
                            break;
                        case Constant.GATEWAY_TIMEOUT:
                            message = "Gateway Timeout";
                            break;
//                        case 301:
//                            message = "Moved Permanently";
//                            break;
                        default:
                            message = "unknown error";
                            break;
                    }
                    return message;
                });
    }

    public static String getMessageForErrorCode(JSONObject jsonObject, Context context) {
        try {
            String errorCode = jsonObject.optString("code", jsonObject.optString("err_code"));
            AppLog.error(TAG, "getMessageForErrorCode: " + errorCode);
            switch (errorCode) {
                case "ERR_INVALID_DATA":
                case "INVALID_DATA":
                    break;
                case "ERR_UNKNOWN":
                case "ERR_UNKNOWN_ERROR":
                    return context.getString(R.string.err_unknown);
                case "ERR_INVALID_EMAIL":
                case "ERR_ALREADY_REGISTERED":
                    break;
                default:
                    return jsonObject.optString("message");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return context.getString(R.string.err_unknown);
    }

    public static Single<String> getMyLanguageErrorMessage(ErrorMessageHandlerModel errorMessageHandlerModel) {
        return Single.just(errorMessageHandlerModel)
                .map(error -> {
                    AppLog.error(TAG, error.errCode);
                    AppLog.error(TAG, error.errorMessages.en);
                    return error.errorMessages.en;
                });
    }


    public static File getOutputMediaFile(Context context, int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name)
        );
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyHHdd_HHmmss", Locale.ENGLISH).format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".png");

        } else {
            return null;
        }

        return mediaFile;
    }

    public static boolean hasSystemFeature(Context context, String featureName) {
        try {
            PackageManager pm = context.getPackageManager();
            return pm.hasSystemFeature(featureName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void openSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static boolean isDeviceSmartPhone(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        return !(diagonalInches >= 6.5);
    }

    public static boolean isResponseSuccess(int code) {
        return code == 201 || code == 200;
    }


}
