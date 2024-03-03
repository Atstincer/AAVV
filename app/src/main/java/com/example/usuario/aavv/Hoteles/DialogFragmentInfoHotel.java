package com.example.usuario.aavv.Hoteles;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.usuario.aavv.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.aavv.R;

/**
 * Created by usuario on 21/07/2023.
 */

public class DialogFragmentInfoHotel extends DialogFragment {

    public static String TAG = "DialogFragmentInfoHotel";

    private EditText etNombreHotel;
    //private Button btn;

    private long idHotelSelected;

    private MyCallBack callBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialogfragment_info_hotel,container,false);
        bindComponents(v);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        callBack = (MyCallBack)getTargetFragment();
        super.onAttach(context);
    }

    private void bindComponents(View v){
        etNombreHotel = (EditText) v.findViewById(R.id.et_nombre_hotel);
        Button btn = (Button) v.findViewById(R.id.btn_info_hotel);

        idHotelSelected = callBack.getHotelId();
        if(idHotelSelected ==0){
            btn.setText("Registrar");
        }else {
            showInfoHotel();
            btn.setText("Actualizar");
        }

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(idHotelSelected ==0){
                    registrar();
                }else {
                    actualizar();
                }
            }
        });
    }

    private void showInfoHotel(){
        Hotel hotel = HotelBDHandler.getHotelfromDB(getContext(), idHotelSelected);
        etNombreHotel.setText(hotel.getNombre());
    }

    private void registrar(){
        if(!isValid()){return;}
        Hotel hotel = getInfo();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HotelBDHandler.CAMPO_NOMBRE,hotel.getNombre());
        bd.insert(HotelBDHandler.TABLE_NAME,null,values);
        Toast.makeText(getContext(),"Registrado correctamente",Toast.LENGTH_SHORT).show();
        callBack.infoChanged();
        dismiss();
    }

    private void actualizar(){
        if(!isValid()){return;}
        Hotel hotel = getInfo();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HotelBDHandler.CAMPO_NOMBRE,hotel.getNombre());
        bd.update(HotelBDHandler.TABLE_NAME,values,"id=?",new String[]{String.valueOf(idHotelSelected)});
        Toast.makeText(getContext(),"Actualizado correctamente",Toast.LENGTH_SHORT).show();
        callBack.infoChanged();
        dismiss();
    }

    private boolean isValid(){
        if(etNombreHotel.getText().toString().equals("")){
            Toast.makeText(getContext(),"Debe escribir un nombre",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Hotel getInfo(){
        return new Hotel(etNombreHotel.getText().toString());
    }

    public interface MyCallBack{
        long getHotelId();
        void infoChanged();
    }
}
