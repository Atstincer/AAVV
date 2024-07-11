package com.example.usuario.aavv.AbstractClasses;

import android.content.Context;
import android.content.UriPermission;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;

import com.example.usuario.aavv.Util.Util;

public abstract class StorageAccessAbstractClass {

    protected String fecha;
    protected final Context ctx;

    protected StorageAccessAbstractClass(Context ctx, String fecha){
        this.ctx = ctx;
        this.fecha = fecha;
    }

    protected abstract String getNameCarpetaPrincipal();
    public abstract String getFileName();

    @Nullable
    protected DocumentFile getMainDirectory(){
        UriPermission uriPermission = Util.getPermissionGranted(ctx);
        if(uriPermission==null){return null;}
        DocumentFile documentFile = DocumentFile.fromTreeUri(ctx,uriPermission.getUri());
        if(documentFile==null || !documentFile.exists()){
            //Log.d("URI","documentFile=null || notExist()");
            return null;
        }
        //Log.d("URI", "documentFile.exists(): " + documentFile.exists());
        if(documentFile.findFile(getNameCarpetaPrincipal())==null){
            return documentFile.createDirectory(getNameCarpetaPrincipal());
        }
        return documentFile.findFile(getNameCarpetaPrincipal());
    }

    @Nullable
    protected abstract DocumentFile getProperDirectory();

    @Nullable
    protected static DocumentFile getDirectory(DocumentFile documentFile,String directory){
        if(documentFile.findFile(directory)==null){
            return documentFile.createDirectory(directory);
        }
        return documentFile.findFile(directory);
    }

    @Nullable
    public abstract DocumentFile getFile();

    public Context getContext(){
        return this.ctx;
    }

    public String getFecha(){
        return this.fecha;
    }
}
