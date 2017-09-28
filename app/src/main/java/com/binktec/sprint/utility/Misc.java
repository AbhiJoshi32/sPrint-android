package com.binktec.sprint.utility;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import com.binktec.sprint.modal.pojo.PrintDetail;
import com.binktec.sprint.modal.pojo.shop.Shop;
import com.itextpdf.text.pdf.PdfReader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Misc {
    private final static String TAG ="Miscellaneous";
    public static String getDate(){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        return df.format(c.getTime());
    }
    public static String getTime(){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
        return df.format(c.getTime());
    }
    public static String[] getFileNameAndPath(Uri uri, Context context) {
        String result[] = new String[2];
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result[0] = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result[0] == null) {
            result[0] = uri.getPath();
            int cut = result[0].lastIndexOf('/');
            if (cut != -1) {
                result[0] = result[0].substring(cut + 1);
            }
        }
        result[1] = getPath(context,uri);
        Log.d(TAG,"The name is "+result[0] +"Path is"+ result[1]);
        return result;
    }

    private static String getPath(final Context context, Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.KITKAT;
        String filePath;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                Log.d(TAG,"isexternal storage" + isExternalStorageDocument(uri));
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    filePath = "/storage/" + type + "/" + split[1];
                    return filePath;
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return
                "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return
                "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    public static float getPrintCost(PrintDetail printDetail, Shop shop) {
        String color = printDetail.getPrintColor();
        float costPerPage = 0;
        if (color.equals("Color")){
            if (printDetail.getPagesPerSheet() == 1) {
                switch (printDetail.getPrintPaperType()) {
                    case "A4":
                        costPerPage = shop.getShopCost().getShopColorCost().getColorA4Cost();
                        break;
                    case "A3":
                        costPerPage = shop.getShopCost().getShopColorCost().getColorA3Cost();
                        break;
                    case "A5":
                        costPerPage = shop.getShopCost().getShopColorCost().getColorA5Cost();
                        break;
                    case "GlossyA4":
                        costPerPage = shop.getShopCost().getShopColorCost().getColorGlossyA4Cost();
                        break;
                }
            } else {
                switch (printDetail.getPrintPaperType()) {
                    case "A4":
                        costPerPage = shop.getShopCost().getShopColorDoubleCost().getColorDoubleA4Cost();
                        break;
                    case "A3":
                        costPerPage = shop.getShopCost().getShopColorDoubleCost().getColorDoubleA3Cost();
                        break;
                    case "A5":
                        costPerPage = shop.getShopCost().getShopColorDoubleCost().getColorDoubleA5Cost();
                        break;
                    case "GlossyA4":
                        costPerPage = shop.getShopCost().getShopColorDoubleCost().getColorDoubleGlossyA4Cost();
                        break;

                }
            }
        }

        else {
            if (printDetail.getPagesPerSheet() == 1) {
                switch (printDetail.getPrintPaperType()) {
                    case "A4":
                        costPerPage = shop.getShopCost().getShopGrayscaleCost().getGrayscaleA4Cost();
                        break;
                    case "A3":
                        costPerPage = shop.getShopCost().getShopGrayscaleCost().getGrayscaleA3Cost();
                        break;
                    case "A5":
                        costPerPage = shop.getShopCost().getShopGrayscaleCost().getGrayscaleA5Cost();
                        break;
                    case "GlossyA4":
                        costPerPage = shop.getShopCost().getShopGrayscaleCost().getGrayscaleGlossyA4Cost();
                        break;
                }
            } else {
                switch (printDetail.getPrintPaperType()) {
                    case "A4":
                        costPerPage = shop.getShopCost().getShopGrayscaleDoubleCost().getGrayscaleDoubleA4Cost();
                        break;
                    case "A3":
                        costPerPage = shop.getShopCost().getShopGrayscaleDoubleCost().getGrayscaleDoubleA3Cost();
                        break;
                    case "A5":
                        costPerPage = shop.getShopCost().getShopGrayscaleDoubleCost().getGrayscaleDoubleA5Cost();
                        break;
                    case "GlossyA4":
                        costPerPage = shop.getShopCost().getShopGrayscaleDoubleCost().getGrayscaleDoubleGlossyA4Cost();
                        break;
                }
            }
        }

        float printCost;
        printCost = costPerPage*printDetail.getPagesToPrint();
        return printCost;
    }

    public static int getNumPages(String path) {
        int filePages = 0;
        try {
            if (path != null) {
                PdfReader reader = new PdfReader(path);
                filePages = reader.getNumberOfPages();
                Log.d(TAG, "Number of pages is +" + String.valueOf(filePages));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePages;
    }

    public static int getFromPagesText(String pagesText,int totalPages) {
        boolean error = false;
        int numOfPages = 0;
        int lastNum = 0;
        int numOfDash;
        int num1,num2;
        int numOfcommas= getOccurrence(pagesText,",");
        String part1;
        String part2;
        if (numOfcommas > 0) {
            String[] part = pagesText.split(",");
            Log.d(TAG,"num of commas is"+numOfcommas);
            for (int i = 0 ;i < numOfcommas;i++) {
                Log.d(TAG,"i value is" + i);
                part1 = part[i];
                part2 = part[i+1];

                numOfDash = getOccurrence(part1,"-");
                if (i==0) {
                    if (numOfDash>1) {
                        error = true;
                    } else if (numOfDash == 0) {
                        numOfPages++;
                        lastNum = Integer.parseInt(part1);
                        System.out.println("num of pages no dash part1" + numOfPages);
                    }  else {
                        String[] dashParts = part1.split("-");
                        num1 = Integer.parseInt(dashParts[0]);
                        num2 = Integer.parseInt(dashParts[1]);
                        if (num2 <= num1) {
                            error = true;
                        } else if (num2>totalPages){
                            error = true;
                        } else {
                            numOfPages += num2-num1+1;
                            lastNum = num2;
                            System.out.println("num of pages dash part1" + numOfPages);
                        }
                    }
                }

                numOfDash = getOccurrence(part2,"-");
                if (numOfDash>1) {
                    error = true;
                } else if (numOfDash == 0) {
                    num1 = Integer.parseInt(part2);
                    System.out.println("lastNum is " + lastNum + "num1 is " + num1);

                    if (num1 <= lastNum || num1>totalPages ){
                        error = true;
                    } else {
                        numOfPages++;
                        System.out.println("num of pages no dash part2" + numOfPages);
                    }
                } else {
                    String[] dashParts = part2.split("-");
                    num1 = Integer.parseInt(dashParts[0]);
                    num2 = Integer.parseInt(dashParts[1]);
                    if (num2 <= num1) {
                        error = true;
                    } else if (num2>totalPages){
                        error = true;
                    } else if (num1 <= lastNum) {
                        error = true;
                    }
                    else {
                        numOfPages += num2-num1+1;
                        lastNum = num2;
                        System.out.println("num of pages with dash second" + numOfPages);
                    }
                }

            }
        } else {
            numOfDash = getOccurrence(pagesText,"-");
            if (numOfDash>1) {
                error = true;
            } else if (numOfDash == 0) {
                num1 = Integer.parseInt(pagesText);
                System.out.println("num1 is " + num1);
                if (num1 > totalPages){
                    error = true;
                } else {
                    numOfPages++;
                    System.out.println("num of pages no dash part2" + numOfPages);
                }
            } else {
                String[] dashParts = pagesText.split("-");
                num1 = Integer.parseInt(dashParts[0]);
                num2 = Integer.parseInt(dashParts[1]);
                if (num2 <= num1) {
                    error = true;
                } else if (num2 > totalPages){
                    error = true;
                }
                else {
                    numOfPages += num2-num1+1;
                }
            }
        }
        if (error) {
            return -1;
        }
        else {
            return numOfPages;
        }
    }

    public static float getBindCost(Shop shop, PrintDetail printDetail) {
        float bindCost;
        Log.d(TAG,shop.getShopCost().toString());

        switch (printDetail.getBindingType()) {
            case "Soft":
                bindCost = shop.getShopCost().getShopBindingCost().getSoftBindingCost();
                break;
            case "Spiral":
                bindCost = shop.getShopCost().getShopBindingCost().getSpiralBindingCost();
                break;
            default:
                bindCost = 0;
                break;
        }
        return bindCost;
    }

    private static int getOccurrence(String mainString, String findString) {
        int lastIndex = 0;
        int count = 0;

        while(lastIndex != -1){

            lastIndex = mainString.indexOf(findString,lastIndex);

            if(lastIndex != -1){
                count ++;
                lastIndex += findString.length();
            }
        }
        return count;
    }
}