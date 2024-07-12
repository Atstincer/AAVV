package com.example.usuario.aavv.StorageAccess;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;

import com.example.usuario.aavv.AbstractClasses.StorageAccessAbstractClass;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;

public class SalvaBDStorageAccess extends StorageAccessAbstractClass {

    //private DocumentFile file;

    public SalvaBDStorageAccess(Context ctx, String fecha) {
        super(ctx, fecha);
    }

    @Override
    protected String getNameCarpetaPrincipal() {return "Salva BD";}

    @Override
    public String getFileName() {
        if(file!=null  && file.exists()){return file.getName();}
        return "BD"+ DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR).replace("/","");
    }

    @Nullable
    @Override
    protected DocumentFile getProperDirectory() {
        return getMainDirectory();
    }

    @Nullable
    @Override
    public DocumentFile getFile() {
        DocumentFile documentFile = getProperDirectory();
        if(documentFile==null || !documentFile.exists()){return null;}
        file = documentFile.createFile("text/csv",getFileName());
        return file;
    }
}
