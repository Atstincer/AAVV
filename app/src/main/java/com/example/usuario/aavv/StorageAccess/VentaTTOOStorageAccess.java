package com.example.usuario.aavv.StorageAccess;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;

import com.example.usuario.aavv.AbstractClasses.StorageAccessAbstractClass;

public class VentaTTOOStorageAccess extends StorageAccessAbstractClass {

    private final String agencia;
    private final String hasta;

    public VentaTTOOStorageAccess(Context ctx, String desde, String hasta, String agencia) {
        super(ctx, desde);
        this.hasta = hasta;
        this.agencia = agencia;
    }

    @Override
    protected String getNameCarpetaPrincipal() {
        return "Venta por agencias-período";
    }

    @Nullable
    @Override
    protected DocumentFile getProperDirectory() {
        DocumentFile documentFile = getMainDirectory();
        if(documentFile==null || !documentFile.exists()){return null;}
        DocumentFile documentFile1 = getDirectory(documentFile,agencia);
        if(documentFile1==null){return null;}
        return getDirectory(documentFile1,fecha.substring(6));
    }

    @Nullable
    @Override
    public DocumentFile getFile() {
        DocumentFile documentFile = getProperDirectory();
        if(documentFile==null || !documentFile.exists()){return null;}
        DocumentFile file = documentFile.findFile(getFileName()+ ".xls");
        if(file!=null && file.exists()){
            file.delete();
        }
        this.file = documentFile.createFile("application/vnd.ms-excel",getFileName());
        return this.file;
    }

    @Override
    public String getFileName() {
        if(file!=null && file.exists()){return file.getName();}
        return fecha.replace("/","") + "-" + hasta.replace("/","") + " " + agencia;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getHasta() {
        return hasta;
    }


}
