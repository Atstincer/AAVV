package com.example.usuario.aavv.StorageAccess;

import android.content.Context;

public class LiquidacionGVTStorageAccess extends LiquidacionStorageAccess {

    public LiquidacionGVTStorageAccess(Context ctx, String fecha) {
        super(ctx, fecha);
    }

    @Override
    public String getFileName() {
        if(file==null || !file.exists()){
            return "LiqGVT_"+fecha.replace("/","");
        }
        return file.getName();
    }

}
