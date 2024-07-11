package com.example.usuario.aavv.Reservas;

import android.content.Context;
import android.content.UriPermission;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;

import com.example.usuario.aavv.AbstractClasses.StorageAccessAbstractClass;
import com.example.usuario.aavv.Util.Util;

public class RepVentaStorageAccess extends StorageAccessAbstractClass {

    private final String[] mesesDelAno = new String[]{"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
    public RepVentaStorageAccess(Context ctx, String fecha) {
        super(ctx, fecha);
    }

    @Override
    protected String getNameCarpetaPrincipal() {
        return "Reportes de venta";
    }

    @Override
    public String getFileName() {
        return fecha.replace("/","");
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
        DocumentFile documentFile = getProperDirectory();
        if(documentFile==null || !documentFile.exists()){return null;}
        DocumentFile file = documentFile.findFile(getFileName()+".xls");
        if(file!=null && file.exists()){
            file.delete();
        }
        return documentFile.createFile("application/vnd.ms-excel",getFileName());
    }
}
