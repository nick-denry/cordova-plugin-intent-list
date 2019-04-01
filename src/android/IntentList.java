package com.nickdenry.intentList;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;

import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This class echoes a string called from JavaScript.
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

    private void getIntentList(CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    // Get list of installed apps from packageManager
                    PackageManager packageManager = cordova.getActivity().getPackageManager();
                    List<ApplicationInfo> installedPackages = packageManager.getInstalledApplications(0);
                    // Create JSON array for js results
                    JSONArray applicationsList = new JSONArray();
                    for (ApplicationInfo packageInfo : installedPackages) {
                        // Skip system applications
                        if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                            continue;
                        } else {
                            // Get app icon
                            Drawable appIcon = packageManager.getApplicationIcon(packageInfo);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            Bitmap bitmap = drawableToBitmap(appIcon);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] appImageBytes = baos.toByteArray();
                            // Convert app icon to base64
                            String appIconBase64 = Base64.encodeToString(appImageBytes, Base64.DEFAULT);
                            // Create json object for current app
                            JSONObject pkgInfo = new JSONObject();
                            pkgInfo.put("label", packageInfo.loadLabel(packageManager)); //这里获取的是应用名 //Keep original comment ;)
                            pkgInfo.put("packageName", packageInfo.packageName);
                            pkgInfo.put("packageIcon", appIconBase64);
                            applicationsList.put(pkgInfo);
                        }
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
