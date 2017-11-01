package com.lokaso.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.view.inputmethod.InputMethodManager;

public class DeviceUtil {

    private static final String TAG = DeviceUtil.class.getSimpleName();

    public static final String MODEL_NUMBER_MI_4G = "HM NOTE 1LTE";
    public static final String MODEL_NUMBER_ACER = "A1-840FHD";
    public static final String MODEL_NUMBER_A0001 = "A0001";
    public static final String MODEL_NUMBER_MI_4W = "MI 4W";
    public static final String MODEL_NUMBER_MI4W = "MI4W";
    public static final String MODEL_NUMBER_MI_1W = "HM NOTE 1W";

    public static final String DEVICE_XIAOMI = "Xiaomi";


	public static String getDeviceId(Context context) {
		return Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
	}

    public static String getDeviceOs(Context context) {
        return "android";
    }

    public static String getDeviceModelNumber() {

        String model = Build.MODEL;

        MyLog.e(TAG, "model : "+model);

        return model;
    }


    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    public static String getDeviceHardware() {
        return Build.HARDWARE;
    }

    public static String getDeviceProduct() {
        return Build.PRODUCT;
    }

    public static String getDeviceType() {
        return Build.TYPE;
    }

    public static String getDeviceAndroidVersion(Context context) {
        return ""+Build.VERSION.SDK_INT;
    }

    public static void hideKeyboard(Activity activity) {

        InputMethodManager imm = null;
        if(imm==null)
            imm = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static boolean isDeviceAcer() {
        String model = getDeviceModelNumber();
        return model.contains(MODEL_NUMBER_ACER);
    }


    private static boolean isDeviceA0001() {
        String model = getDeviceModelNumber();
        return model.contains(MODEL_NUMBER_A0001);
    }

    private static boolean isDeviceMI_4G() {
        String model = getDeviceModelNumber();
        return model.contains(MODEL_NUMBER_MI_4G);
    }


    private static boolean isDeviceMI_4W() {
        String model = getDeviceModelNumber();
        return model.contains(MODEL_NUMBER_MI_4W) || model.contains(MODEL_NUMBER_MI4W);
    }

    private static boolean isDeviceMI_1W() {
        String model = getDeviceModelNumber();
        return model.contains(MODEL_NUMBER_MI_1W);
    }

    private static boolean isDeviceXiaomi() {
        String model = Build.MODEL;
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        String hardware = Build.HARDWARE;
        String product = Build.PRODUCT;
        String type = Build.TYPE;

        boolean isXiaomi = false;

        String xiaomi = DEVICE_XIAOMI.toLowerCase();

        isXiaomi = model.toLowerCase().contains(xiaomi)
                || manufacturer.toLowerCase().contains(xiaomi)
                || brand.toLowerCase().contains(xiaomi)
                || hardware.toLowerCase().contains(xiaomi)
                || product.toLowerCase().contains(xiaomi)
                || type.toLowerCase().contains(xiaomi);

        return isXiaomi;
    }

    public static boolean isZoomDisabled() {

        boolean isZoomDisabled = true;

        String[] models =
                {
                        MODEL_NUMBER_A0001
                        ,MODEL_NUMBER_MI_4G
                        ,MODEL_NUMBER_MI_4W
                        ,MODEL_NUMBER_MI4W
                        ,MODEL_NUMBER_ACER

                };

        String thisModel = getDeviceModelNumber();

        for(String model : models) {
            if(model.toLowerCase().contains(thisModel.toLowerCase())) {
                isZoomDisabled = false;
                break;
            }
        }

        isZoomDisabled = isDeviceMI_4G() || isDeviceA0001() || isDeviceMI_4W()|| isDeviceXiaomi();; //|| isDeviceAcer();

        if(isDeviceMI_1W()) {
            isZoomDisabled = false;
        }



/*
        boolean forTest = false;
        boolean forAcer = isDeviceAcer() && MyLog.DEBUG && forTest;
        isZoomEnabled = isZoomEnabled || forTest ;
*/

        return isZoomDisabled;
    }

    public static boolean deviceHasCamera(Context context) {
        boolean hasCamera = true;
        try {
            PackageManager pm = context.getPackageManager();
            hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasCamera;
    }


    private boolean hasGpsSensor(Context context){
        PackageManager packMan = context.getPackageManager();
        return packMan.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    public static String getDeviceDetails(Context context) {

        String model = Build.MODEL;
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        String hardware = Build.HARDWARE;
        String product = Build.PRODUCT;
        String type = Build.TYPE;
        String id = Build.ID;
        String android_version = ""+Build.VERSION.SDK_INT;
        String app_version = ""+AppUtil.getVersionCode(context);
        String app_version_name = ""+AppUtil.getVersionName(context);
        String user_accounts = ""; //+getDeviceUserAccountsCommaSeperated(context);

        String seperator_item = ";;"; //"seperatoraqwerty";
        String seperator_detail = ":";

        String details = "id:"+id
                + seperator_item + "model"             +seperator_detail+model
                + seperator_item + "manufacturer"      +seperator_detail+manufacturer
                + seperator_item + "brand"             +seperator_detail+brand
                + seperator_item + "hardware"          +seperator_detail+hardware
                + seperator_item + "product"           +seperator_detail+product
                + seperator_item + "type"              +seperator_detail+type
                + seperator_item + "android_version"   +seperator_detail+android_version
                + seperator_item + "app_version"       +seperator_detail+app_version
                + seperator_item + "app_version_name"  +seperator_detail+app_version_name
                + seperator_item + "user_accounts"     +seperator_detail+user_accounts
                ;

        MyLog.e(TAG, "details : "+details);

        return details;
    }

    public static String getDeviceFullname(Context context) {

        String model = Build.MODEL;
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        String hardware = Build.HARDWARE;
        String product = Build.PRODUCT;
        String type = Build.TYPE;
        String id = Build.ID;

        String seperator_item = ";;";
        String seperator_detail = ":";
        String details = "id:"+id
                + seperator_item + "model"             +seperator_detail+model
                + seperator_item + "manufacturer"      +seperator_detail+manufacturer
                + seperator_item + "brand"             +seperator_detail+brand
                + seperator_item + "hardware"          +seperator_detail+hardware
                + seperator_item + "product"           +seperator_detail+product
                + seperator_item + "type"              +seperator_detail+type
                ;

        MyLog.e(TAG, "getDeviceFullname : "+details);

        return details;
    }

/*

    public static List<String> getDeviceUserAccountsList(Context context) {

        List<String> emailList = new ArrayList<>();

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                emailList.add(possibleEmail);
            }
        }

        return emailList;
    }
*/

/*
    public static String getDeviceUserAccountsCommaSeperated(Context context) {

        String emailList = "";

        String seperator = ",";

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                emailList += possibleEmail + seperator;
            }
        }

        return emailList;
    }*/
}
