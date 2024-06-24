package com.example.usuario.aavv.Util;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

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

    public static boolean isPermissionGranted(Context ctx){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        }else {
            Log.d("permisos","SDK build version: "+Build.VERSION.SDK_INT);
            return ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }
}
