package com.elisuntech.rentalmanagementapp2.commonMethods;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

public   class sharedPreference {
    private static String TAG = sharedPreference.class.getSimpleName();
    static SharedPreferences pref;
    static SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "rentalmanagementapp.Session";
    private static final String KEY_USERID = "ImeiNumber";
    private static final String KEY_USERDETAILS = "userdetails";
    private static final String KEY_DEFAULTERUSERDATA = "defaulterdetails";
    private static final String KEY_CONTACTSELECTED = "contactselected";
    private static final String KEY_PRODUCTSELECTED = "productselected";
    private static final String KEY_IMAGE = "PhoneNumber";
    private static final String KEY_PRODUCTDETAILS = "productdetails";
    private static final String KEY_APP_USE_DATA = "appusedata";
    private static final String KEY_RESETPASSWORD = "password";


    public sharedPreference(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /*  SETTING SESSION FOR LOGIN USER */
    public static void setIsLoggedIn (String userID) {
        editor.putString(KEY_USERID, userID);
        editor.commit();
        Log.d(TAG, "KEY_USERID "+KEY_USERID+" recorded");
    }

    public static String getIsLoggedIn () {
        return pref.getString(KEY_USERID, "0");
    }

    /* LOGIN USER DETAILS */
    public static void setUSERDETAILS (JSONObject object) {
        Gson gson = new Gson();
        String jsonText = gson.toJson(object);
        editor.putString(KEY_USERDETAILS, jsonText);
        editor.apply();
    }

    public static JSONObject getUSERDETAILS   () {
        String objectStored = pref.getString(KEY_USERDETAILS, "");
        Gson convert = new Gson();
        JSONObject jsonObject = convert.fromJson(objectStored, JSONObject.class);
        return jsonObject;

    }

    /* Defaulter  DETAILS */
    public static void setDEFAULTERUSERDATA (JSONObject object) {
        Gson gson = new Gson();
        String jsonText = gson.toJson(object);
        editor.putString(KEY_DEFAULTERUSERDATA, jsonText);
        editor.apply();
    }

    public static JSONObject getDEFAULTERUSERDATA   () {
        String objectStored = pref.getString(KEY_DEFAULTERUSERDATA, "");
        Gson convert = new Gson();
        JSONObject jsonObject = convert.fromJson(objectStored, JSONObject.class);
        return jsonObject;

    }

    // Chat contact selected
    public static void setChatcontact  (String userID) {
        editor.putString(KEY_CONTACTSELECTED, userID);
        editor.commit();
        Log.d(TAG, "KEY_CONTACTSELECTED "+KEY_CONTACTSELECTED+" recorded");
    }

    public static String getChatcontact () {
        return pref.getString(KEY_CONTACTSELECTED, "");
    }

    // Product selected
    public static void setproductSelected  (String product) {
        editor.putString(KEY_PRODUCTSELECTED, product);
        editor.commit();
        Log.d(TAG, "KEY_PRODUCTSELECTED "+KEY_PRODUCTSELECTED+" recorded");
    }

    public static String getproductSelected () {
        return pref.getString(KEY_PRODUCTSELECTED, "");
    }

    /* product Details */
    public static void setProductDetails (JSONObject object) {
        Gson gson = new Gson();
        String jsonText = gson.toJson(object);
        editor.putString(KEY_PRODUCTDETAILS, jsonText);
        editor.apply();
    }

    public static JSONObject getProductDetails   () {
        String objectStored = pref.getString(KEY_PRODUCTDETAILS, "");
        Gson convert = new Gson();
        JSONObject jsonObject = convert.fromJson(objectStored, JSONObject.class);
        return jsonObject;

    }
    /* app user details that allow the user to use the app .this will toggle between expiry and paid */
    public static void setAUTHENTICATEAPPUSES (JSONObject object) {
        Gson gson = new Gson();
        String jsonText = gson.toJson(object);
        editor.putString(KEY_APP_USE_DATA, jsonText);
        editor.apply();
    }

    public static JSONObject getAUTHENTICATEAPPUSES () {
        String objectStored = pref.getString(KEY_APP_USE_DATA, "");
        Gson convert = new Gson();
        JSONObject jsonObject = convert.fromJson(objectStored, JSONObject.class);
        return jsonObject;

    }

    // Reset password
    public static void setphonereset  (String phone) {
        editor.putString(KEY_RESETPASSWORD, phone);
        editor.commit();
        Log.d(TAG, "KEY_RESETPASSWORD "+KEY_RESETPASSWORD+" recorded");
    }

    public static String getphonereset () {
        return pref.getString(KEY_RESETPASSWORD, "");
    }

}
