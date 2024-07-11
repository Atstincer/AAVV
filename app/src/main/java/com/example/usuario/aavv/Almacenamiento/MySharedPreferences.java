package com.example.usuario.aavv.Almacenamiento;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by usuario on 1/11/2022.
 */

public class MySharedPreferences {

    private static final String NAME_ARCHIVO = "MyPreferences";
    private static final String KEY_NOMBRE_VENDEDOR = "nombre_vendedor";
    private static final String KEY_TELEFONO_VENDEDOR = "telefono_vendedor";
    private static final String KEY_AGENCIA_VENDEDOR = "agencia_vendedor";
    private static final String KEY_INCLUIR_DEV = "incluir_dev_en_liq";
    private static final String KEY_FRAGMENT_INICIO = "fragment_inicio";
    private static final String KEY_PREDECIR_PRECIO = "predecir precio";
    private static final String KEY_INCLUIR_PRECIO_CUP = "incluir_precio_cup";
    private static final String KEY_TASA_CUP = "tasa_cup";
    private static final String KEY_URI_SHARED_DIR = "uri_shared_dir";



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
        return preferences.getBoolean(KEY_INCLUIR_DEV,false);
    }

    public static void storeIncluirDevEnLiquidacion(Context ctx,boolean incluir){
        SharedPreferences preferences = ctx.getSharedPreferences(NAME_ARCHIVO,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_INCLUIR_DEV,incluir);
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
}
