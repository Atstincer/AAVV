package com.example.usuario.aavv.StorageAccess;

import static com.example.usuario.aavv.Util.Util.mesesDelAno;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;

import com.example.usuario.aavv.AbstractClasses.StorageAccessAbstractClass;

public abstract class LiquidacionStorageAccess extends StorageAccessAbstractClass {

    public LiquidacionStorageAccess(Context ctx, String fecha) {
        super(ctx, fecha);
    }

    @Override
    protected String getNameCarpetaPrincipal() {
        return "Liquidaciones";
    }

    @Nullable
    @Override
    protected DocumentFile getProperDirectory() {
        DocumentFile documentFile = getMainDirectory();
        if(documentFile==null || !documentFile.exists()){return null;}
        DocumentFile documentFile1 = getDirectory(documentFile,fecha.substring(6));
        if(documentFile1==null || !documentFile1.exists()){return null;}
        return getDirectory(documentFile1,mesesDelAno[Integer.parseInt(fecha.substring(3,5))-1]);
    }

    @Nullable
    @Override
    public DocumentFile getFile() {
        if(this.file!=null && this.file.exists()){return this.file;}
        DocumentFile documentFile = getProperDirectory();
        if(documentFile==null || !documentFile.exists()){return null;}
        DocumentFile file = documentFile.findFile(getFileName()+".xls");
        if(file!=null && file.exists()){
            file.delete();
        }
        this.file = documentFile.createFile("application/vnd.ms-excel",getFileName());
        return this.file;
    }
}
