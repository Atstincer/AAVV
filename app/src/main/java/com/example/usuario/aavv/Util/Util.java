package com.example.usuario.aavv.Util;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;

/**
 * Created by usuario on 17/09/2023.
 */

public class Util {

    public static void copyToClipBoard(Context ctx, String text){
        ClipboardManager clipboardManager = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text",text);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(ctx,"Copiado",Toast.LENGTH_SHORT).show();
    }

    @Nullable
    public static UriPermission getPermissionGranted(Context ctx){
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        }else {
            Log.d("permisos","SDK build version: "+Build.VERSION.SDK_INT);
            return ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }*/
        for (UriPermission permission :ctx.getContentResolver().getPersistedUriPermissions()){
            if(permission.getUri().toString().equalsIgnoreCase(MySharedPreferences.getUriExtSharedDir(ctx))){
                return permission;
            }
        }
        return null;
    }

    public static boolean hasPermissionGranted(Context ctx){
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        }else {
            Log.d("permisos","SDK build version: "+Build.VERSION.SDK_INT);
            return ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }*/
        return getPermissionGranted(ctx)!=null;
    }
}
