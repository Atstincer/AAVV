package com.example.usuario.aavv.Almacenamiento;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

import com.example.usuario.aavv.Util.MisConstantes;

/**
 * Created by usuario on 1/11/2022.
 */

public class MySharedPreferences {

    private static final String NAME_ARCHIVO = "MyPreferences";
    private static final String KEY_NOMBRE_VENDEDOR = "nombre_vendedor";
    private static final String KEY_TELEFONO_VENDEDOR = "telefono_vendedor";
    private static final String KEY_AGENCIA_VENDEDOR = "agencia_vendedor";
    private static final String KEY_INCLUIR_DEV_EN_LIQ = "incluir_dev_en_liq";
    private static final String KEY_INCLUIR_DEV_EN_REPVENTA = "incluir_dev_en_repventa";
    private static final String KEY_FRAGMENT_INICIO = "fragment_inicio";
    private static final String KEY_PREDECIR_PRECIO = "predecir precio";
    private static final String KEY_INCLUIR_PRECIO_CUP = "incluir_precio_cup";
    private static final String KEY_TASA_CUP = "tasa_cup";
    private static final String KEY_URI_SHARED_DIR = "uri_shared_dir";
    private static final String KEY_DEFAULT_MAILS = "default_mail";
    private static final String KEY_TIPO_FECHA_A_FILTRAR = "tipo_fecha_filtrar";



    public static String getNombreVendedor(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        return preferences.getString(KEY_NOMBRE_VENDEDOR,"");
    }

    public static void storeNombreVendedor(Context ctx,String nombreVendedor){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_NOMBRE_VENDEDOR,nombreVendedor);
        editor.apply();
    }

    public static String getTelefonoVendedor(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO, MODE_PRIVATE);
        return preferences.getString(KEY_TELEFONO_VENDEDOR,"");
    }

    public static void storeTelefonoVendedor(Context ctx,String telefonoVendedor){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_TELEFONO_VENDEDOR,telefonoVendedor);
        editor.apply();
    }

    public static String getAgenciaVendedor(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        return preferences.getString(KEY_AGENCIA_VENDEDOR,"");
    }

    public static void storeAgenciaVendedor(Context ctx,String agenciaVendedor){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_AGENCIA_VENDEDOR,agenciaVendedor);
        editor.apply();
    }

    public static boolean getIncluirDevEnLiquidacion(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        return preferences.getBoolean(KEY_INCLUIR_DEV_EN_LIQ,true);
    }

    public static void storeIncluirDevEnLiquidacion(Context ctx,boolean incluir){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_INCLUIR_DEV_EN_LIQ,incluir);
        editor.apply();
    }

    public static boolean getIncluirDevEnRepVenta(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        return preferences.getBoolean(KEY_INCLUIR_DEV_EN_REPVENTA,true);
    }

    public static void storeIncluirDevEnRepVenta(Context ctx,boolean incluir){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_INCLUIR_DEV_EN_REPVENTA,incluir);
        editor.apply();
    }

    public static int getFragmentInicio(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        return preferences.getInt(KEY_FRAGMENT_INICIO,0);
    }

    public static void storeFragmentInicio(Context ctx,int fragment){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_FRAGMENT_INICIO,fragment);
        editor.apply();
    }

    public static boolean getPredecirPrecio(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        return preferences.getBoolean(KEY_PREDECIR_PRECIO,true);
    }

    public static void storePredecirPrecio(Context ctx,boolean predecir){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_PREDECIR_PRECIO,predecir);
        editor.apply();
    }

    public static boolean getIncluirPrecioCUP(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        return preferences.getBoolean(KEY_INCLUIR_PRECIO_CUP,false);
    }

    public static void storeIncluirPrecioCUP(Context ctx,boolean incluir){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_INCLUIR_PRECIO_CUP,incluir);
        editor.apply();
    }

    public static float getTasaCUP(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        return preferences.getFloat(KEY_TASA_CUP,0);
    }

    public static void storeTasaCUP(Context ctx,float tasa){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(KEY_TASA_CUP,tasa);
        editor.apply();
    }

    public static String getUriExtSharedDir(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        return preferences.getString(KEY_URI_SHARED_DIR,"");
    }

    public static void storeUriExtSharedDir(Context ctx,String uri){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_URI_SHARED_DIR,uri);
        editor.apply();
    }

    public static void addNewMail(Context ctx, String newMail){
        String mailAdressTodas;
        if(!getMails(ctx).isEmpty()) {
            mailAdressTodas = getMails(ctx) + ";" + newMail;
        }else {
            mailAdressTodas = newMail;
        }
        storeMails(ctx,mailAdressTodas);
    }

    public static void addMailsIfDoesntExits(Context ctx,String mails){
        String[] mailsStored = getArrayOfMails(ctx);
        if(mailsStored.length==0){
            storeMails(ctx,mails);
        }else {
            String[] newMails = mails.split(";");
            for(String newMail:newMails){
                boolean found = false;
                for(String storedMail:mailsStored){
                    if (newMail.equals(storedMail)) {
                        found = true;
                        break;
                    }
                }
                if(!found){
                    MySharedPreferences.addNewMail(ctx,newMail);
                }
            }
        }
    }

    public static void storeMails(Context ctx, String mails) {
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_DEFAULT_MAILS, mails);
        editor.apply();
    }

    public static String getMails(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        return preferences.getString(KEY_DEFAULT_MAILS,"");
    }

    public static String[] getArrayOfMails(Context ctx){
        String mails = getMails(ctx);
        if(!mails.isEmpty()){
            return mails.split("[;]");
        }else {
            return new String[]{mails};
        }
    }

    public static void removeMail(Context ctx,String mailToRemove){
        Log.d("SHP","mailToRemove: "+mailToRemove);
        StringBuilder builder = new StringBuilder();
        for(String mail:getArrayOfMails(ctx)){
            Log.d("SHP","mails in Array: "+mail);
            if(!mail.equals(mailToRemove)){
                Log.d("SHP","no coincide mail: "+mail);
                builder.append(mail);
                builder.append(";");
            }
        }
        storeMails(ctx,builder.toString());
    }

    public static int getTipoFechaFiltrar(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        return preferences.getInt(KEY_TIPO_FECHA_A_FILTRAR, MisConstantes.Filtrar.FECHA_EXCURSION.ordinal());
    }

    public static void storeTipoFechaFiltrar(Context ctx,int tipoFecha){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_TIPO_FECHA_A_FILTRAR,tipoFecha);
        editor.apply();
    }

}
