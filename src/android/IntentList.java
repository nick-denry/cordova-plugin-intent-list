package com.nickdenry.intentList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.lang.CharSequence;
import java.util.List;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Get Intent list from Android and send it to js.
 */
public class IntentList extends CordovaPlugin {

    public static final String ACTION_GET_INTENT_LIST = "getIntentList";

    // @see https://stackoverflow.com/a/10600736
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (ACTION_GET_INTENT_LIST.equals(action)) {
            getIntentList(callbackContext);
            return true;
        }
        callbackContext.error("Invalid action");
        return false;
    }

    private void getIntentList(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    // Get list of Intents from packageManager
                    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    PackageManager packageManager = cordova.getActivity().getPackageManager();
                    List<ResolveInfo> resovleInfoList = packageManager.queryIntentActivities(mainIntent, 0);
                    // Create JSON array for js results
                    JSONArray applicationsList = new JSONArray();
                    for (ResolveInfo resolveInfo : resovleInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName; // Get Intent package name
                        CharSequence intentLabel = resolveInfo.loadLabel(packageManager); //这里获取的是应用名 //Keep original comment ;)
                        Drawable appIcon = resolveInfo.loadIcon(packageManager); // Get Intent icon and convert it to base64
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Bitmap bitmap = drawableToBitmap(appIcon);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] intentImageBytes = baos.toByteArray();
                        // Convert Intent icon to base64
                        String intentIconBase64 = Base64.encodeToString(intentImageBytes, Base64.DEFAULT);
                        intentIconBase64 = "data:image/png;base64, " + intentIconBase64;
                        // Create json object for current Intent
                        JSONObject intentInfo = new JSONObject();
                        intentInfo.put("label", intentLabel);
                        intentInfo.put("package", packageName);
                        intentInfo.put("packageIcon", intentIconBase64);
                        applicationsList.put(intentInfo);
                    }
                    callbackContext.success(applicationsList);
                } catch (Exception e) {
                    System.err.println("Exception: " + e.getMessage());
                    callbackContext.error(e.getMessage());
                }
            }// end of Run Runnable()
        });// end of run getThreadPool()
    }
}
