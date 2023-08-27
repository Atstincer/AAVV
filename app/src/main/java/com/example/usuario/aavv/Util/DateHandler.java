package com.example.usuario.aavv.Util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by usuario on 28/07/2023.
 */

public class DateHandler {

    private static String formatDateToStoreInDB(int day, int month, int year){
        return year+"-"+toDosLugares(month)+"-"+toDosLugares(day);
    }

    public static String formatDateToStoreInDB(String date){
        return date.substring(6)+"-"+date.substring(3,5)+"-"+date.substring(0,2);
    }

    private static String formatDateToShow(int day,int month,int year){
        return toDosLugares(day)+"/"+toDosLugares(month)+"/"+toDosLugares(year);
    }

    public static String formatDateToShow(String date){
        return date.substring(8) + "/" + date.substring(5,7) + "/" + date.substring(0,4);
    }

    public static String getToday(MisConstantes.FormatoFecha formato){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);
        switch (formato){
            case MOSTRAR:
                return DateHandler.formatDateToShow(day,month,year);
            case ALMACENAR:
                return DateHandler.formatDateToStoreInDB(day,month,year);
        }
        return "";
    }

    private static String toDosLugares(int x){
        String cad = String.valueOf(x);
        if (cad.length() == 1){
            cad = "0" + x;
        }
        return cad;
    }

    public static boolean areDatesInOrder(String firstDate,String secondDate){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date1 = sdf.parse(firstDate);
            Date date2 = sdf.parse(secondDate);
            return date1.before(date2);
        }catch (Exception e){
            System.out.println("Exception capturada parseando fechas: "+e.getMessage());
        }
        return false;
    }

    public static void showDatePicker(Context ctx, final TextView textView, final DatePickerCallBack callBack) {
        String tv_str = "";
        if(textView.getText().toString().equals("")||textView.getText().toString().length()<10){
            tv_str = DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR);
        }else {
            tv_str = textView.getText().toString();
        }
        int year = Integer.parseInt(tv_str.substring(6));
        int month = Integer.parseInt(tv_str.substring(3, 5)) - 1;
        int day = Integer.parseInt(tv_str.substring(0, 2));

        new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                textView.setText(DateHandler.formatDateToShow(day,month+1,year));
                callBack.dateSelected();
            }
        }, year, month, day).show();
    }

    public interface DatePickerCallBack{
        void dateSelected();
    }
}
