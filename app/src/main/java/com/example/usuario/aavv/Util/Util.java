package com.example.usuario.aavv.Util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by usuario on 17/09/2023.
 */

public class Util {

    public static void copyToClipBoard(Context ctx, String text){
        ClipboardManager clipboardManager = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text",text);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(ctx,"Texto copiado",Toast.LENGTH_SHORT).show();
    }

}
