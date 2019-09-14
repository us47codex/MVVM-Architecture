package com.us47codex.mvvmarch.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.DefaultErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.WalledGardenInternetObservingStrategy;
import com.us47codex.mvvmarch.BuildConfig;
import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.SunTecApplication;
import com.us47codex.mvvmarch.constant.Constants;
import com.us47codex.mvvmarch.constant.EndPoints;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Completable;
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

    public static boolean isValidUserName(String username) {
        String userNameValid = "^[a-zA-Z]{3}[0-9]{4}$";
        return Pattern.compile(userNameValid).matcher(username).matches();
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
                        case Constants.BAD_REQUEST:
                            message = "Bad Request";
                            break;
                        case Constants.UNAUTHORIZED:
                            message = "Unauthorized";
                            break;
                        case Constants.FORBIDDEN:
                            message = "Forbidden";
                            break;
                        case Constants.NOT_FOUND:
                            message = "Not Found";
                            break;
                        case Constants.REQUEST_TIMEOUT:
                            message = "Request Timeout";
                            break;
                        case Constants.SERVER_BROKEN:
                            message = "Server Broken"; //internal server error
                            break;
                        case Constants.BAD_GATEWAY:
                            message = "Bad Gateway";
                            break;
                        case Constants.SERVICE_UNAVAILABLE:
                            message = "Service Unavailable";
                            break;
                        case Constants.GATEWAY_TIMEOUT:
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
                    AppLog.error(TAG, error.errorMessages);
                    return error.errorMessages;
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

    public static Single<Boolean> checkHardInternetConnection() {
        int initialInterval = 0;
        int interval = 2000;
        String host = EndPoints.NETWORK_PING_URL;
        int port = 80;
        int timeout = 5000;
        int httpResponse = HttpURLConnection.HTTP_NO_CONTENT;
        ErrorHandler errorHandler = new DefaultErrorHandler();
        InternetObservingStrategy strategy = new WalledGardenInternetObservingStrategy();

        InternetObservingSettings settings = InternetObservingSettings.builder()
                .initialInterval(initialInterval)
                .interval(interval)
                .host(host)
                .port(port)
                .timeout(timeout)
                .httpResponse(httpResponse)
                .errorHandler(errorHandler)
                .strategy(strategy)
                .build();

        return ReactiveNetwork.checkInternetConnectivity(settings);
    }

    public static Completable clearPreference() {
        return Completable.fromAction(() -> {
            SunTecApplication.getInstance().getPreferenceManager().clear();
        });
    }

    /**
     * Return Formatted  Date and Time to Show
     *
     * @param format Global format for AavGo app "MM-dd-yyyy hh:mm a"
     */
    public static String convertDateToString(Date date, String format) {
        String resultDate = "00-00-0000 00:00";
        boolean b = false;
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(format, Locale.ENGLISH); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            resultDate = dateFormatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.error(TAG, e);
        }
        return resultDate;
    }

    /**
     * Return Formatted  Date and Time to Show
     *
     * @param format Global format for AavGo app "MM-dd-yyyy hh:mm a"
     */
    @SuppressLint("SimpleDateFormat")
    public static Date convertDateStringToDate(String date, String format) {
        Date resultDate = new Date();
        try {
            resultDate = new SimpleDateFormat(format).parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.error(TAG, e);
        }
        return resultDate;
    }


    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static String convertImageBase64(String str_Path) {
        try {
            Log.e(TAG, "str_Path image : >>>>>" + str_Path);
            Bitmap bm = BitmapFactory.decodeFile(str_Path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            Log.e(TAG, "convertImageBase64 image : >>>>>" + encodedImage);
            return encodedImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap convertBase64ToStringURL(String strImg) {
        try {
            String base64Content = strImg;
            byte[] bytes = Base64.decode(base64Content, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }






}
