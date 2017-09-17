package com.binktec.sprint.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.binktec.sprint.modal.pojo.FileDetail;
import com.binktec.sprint.modal.pojo.PrintDetail;
import com.binktec.sprint.modal.pojo.PrintJobDetail;
import com.binktec.sprint.modal.pojo.User;
import com.binktec.sprint.modal.pojo.shop.Shop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static final String TAG = "Session Manager";
    private static final String KEY_FILE_DETAILS = "file details";
    private static final String KEY_PRINT_DETAIL = "Print Detail";
    private static final String KEY_CURR_PRINT_TRANSACTION = "Print Transaction";
    private static final String KEY_API_PRINT_JOBS = "Api print jobs";
    private static final String KEY_USER = "User";
    private static final String KEY_HISTORY_PRINT_JOBS = "History jobs";
    private static final String KEY_SHOPS = "Shops";
    private static final String KEY_FIRST_SETUP = "First Setup";
    private static SharedPreferences pref;

    public SessionManager() {}

    public static void initInstance(Context context) {
        if(pref == null){
            int PRIVATE_MODE = 0;
            pref= context.getSharedPreferences(context.getPackageName(), PRIVATE_MODE);
        }
    }

    public static void fileDetailSave(List<FileDetail> fileDetails) {
        String json;
        Gson gson=new Gson();
        SharedPreferences.Editor editor = pref.edit();
        json = gson.toJson(fileDetails);
        editor.putString(KEY_FILE_DETAILS,json);
        editor.apply();
    }

    public static ArrayList<FileDetail> getFileDetail(){
        String json;
        Gson gson=new Gson();
        json = pref.getString(KEY_FILE_DETAILS,null);
        Log.d(TAG, "The value of json is "+json);
        Type type = new TypeToken<ArrayList<FileDetail>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void clearFileDetail() {
        pref.edit().remove(KEY_FILE_DETAILS).apply();
    }

    public static void savePrintDetail(PrintDetail printOutDetail){
        String json;
        Gson gson=new Gson();
        SharedPreferences.Editor editor = pref.edit();
        json = gson.toJson(printOutDetail);
        editor.putString(KEY_PRINT_DETAIL,json);
        editor.apply();
    }

    public static PrintDetail getPrintDetail(){
        String json;
        Gson gson=new Gson();
        json = pref.getString(KEY_PRINT_DETAIL,null);
        return gson.fromJson(json,PrintDetail.class);
    }

    public static void clearPrintDetail() {
        pref.edit().remove(KEY_PRINT_DETAIL).apply();
    }

    public static void saveCurrentPrintJob(PrintJobDetail printJobDetail){
        String json;
        Gson gson=new Gson();
        SharedPreferences.Editor editor = pref.edit();
        json = gson.toJson(printJobDetail);
        editor.putString(KEY_CURR_PRINT_TRANSACTION,json);
        editor.apply();
    }

    public static PrintJobDetail getCurrentPrintJobDetail(){
        String json;
        Gson gson=new Gson();
        json = pref.getString(KEY_CURR_PRINT_TRANSACTION,null);
        return gson.fromJson(json,PrintJobDetail.class);
    }

    public static void clearCurrentPrintJob() {
        pref.edit().remove(KEY_CURR_PRINT_TRANSACTION).apply();
    }

    public static void saveApiPrintJob(List<PrintJobDetail> printJobDetails){
        String json;
        Gson gson=new Gson();
        SharedPreferences.Editor editor = pref.edit();
        json = gson.toJson(printJobDetails);
        editor.putString(KEY_API_PRINT_JOBS,json);
        editor.apply();
    }

    public static List<PrintJobDetail> getApiPrintJobDetail(){
        String json;
        Gson gson=new Gson();
        json = pref.getString(KEY_API_PRINT_JOBS,null);
        Type type = new TypeToken<ArrayList<PrintJobDetail>>() {}.getType();
        return gson.fromJson(json,type);
    }

    public static void saveHistoryPrintJob(List<PrintJobDetail> printJobDetails){
        String json;
        Gson gson=new Gson();
        SharedPreferences.Editor editor = pref.edit();
        json = gson.toJson(printJobDetails);
        editor.putString(KEY_HISTORY_PRINT_JOBS,json);
        editor.apply();
    }

    public static List<PrintJobDetail> getHistoryPrintJobDetail(){
        String json;
        Gson gson=new Gson();
        json = pref.getString(KEY_HISTORY_PRINT_JOBS,null);
        Type type = new TypeToken<ArrayList<PrintJobDetail>>() {}.getType();
        return gson.fromJson(json,type);
    }

    public static void saveShopList(List<Shop> shops){
        String json;
        Gson gson=new Gson();
        SharedPreferences.Editor editor = pref.edit();
        json = gson.toJson(shops);
        editor.putString(KEY_SHOPS,json);
        editor.apply();
    }

    public static List<Shop> getShops() {
        String json;
        Gson gson=new Gson();
        json = pref.getString(KEY_SHOPS,null);
        Type type = new TypeToken<ArrayList<Shop>>() {}.getType();
        return gson.fromJson(json,type);
    }

    public static void saveUser(User user){
        String json;
        Gson gson=new Gson();
        SharedPreferences.Editor editor = pref.edit();
        json = gson.toJson(user);
        editor.putString(KEY_USER,json);
        editor.apply();
    }

    public static User getUser(){
        String json;
        Gson gson=new Gson();
        json = pref.getString(KEY_USER,null);
        return gson.fromJson(json,User.class);
    }

    public static boolean getIsFirstSetup() {
        return pref.getBoolean(KEY_FIRST_SETUP,true);
    }

    public static void saveFirstSetup(boolean status) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_FIRST_SETUP,status);
        editor.apply();
    }
}