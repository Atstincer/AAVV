package com.example.usuario.aavv.TTOO;

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

public class DialogFragmentInfoTTOO extends DialogFragment {

    public static String TAG = "DialogFragmentInfoTTOO";

    private EditText etNombreTTOO;
    //private Button btn;

    private long idTTOOSelected;

    private MyCallBack callBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialogfragment_info_ttoo,container,false);
        bindComponents(v);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        callBack = (MyCallBack)getTargetFragment();
        super.onAttach(context);
    }

    private void bindComponents(View v){
        etNombreTTOO = (EditText) v.findViewById(R.id.et_nombre_ttoo);
        Button btn = (Button) v.findViewById(R.id.btn_info_ttoo);

        idTTOOSelected = callBack.getTTOOId();
        if(idTTOOSelected ==0){
            btn.setText("Registrar");
        }else {
            showInfoTTOO();
            btn.setText("Actualizar");
        }

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(idTTOOSelected ==0){
                    registrar();
                }else {
                    actualizar();
                }
            }
        });
    }

    private void showInfoTTOO(){
        TTOO ttoo = TTOOBDHandler.getTTOOfromDB(getContext(), idTTOOSelected);
        etNombreTTOO.setText(ttoo.getNombre());
    }

    private void registrar(){
        if(!isValid()){return;}
        TTOO ttoo = getInfo();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase sq = admin.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TTOOBDHandler.CAMPO_NOMBRE,ttoo.getNombre());
        sq.insert(TTOOBDHandler.TABLE_NAME,null,values);
        Toast.makeText(getContext(),"Registrado correctamente",Toast.LENGTH_SHORT).show();
        callBack.infoChanged();
        dismiss();
    }

    private void actualizar(){
        if(!isValid()){return;}
        TTOO ttoo = getInfo();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase sq = admin.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TTOOBDHandler.CAMPO_NOMBRE,ttoo.getNombre());
        sq.update(TTOOBDHandler.TABLE_NAME,values,"id=?",new String[]{String.valueOf(idTTOOSelected)});
        Toast.makeText(getContext(),"Actualizado correctamente",Toast.LENGTH_SHORT).show();
        callBack.infoChanged();
        dismiss();
    }

    private boolean isValid(){
        if(etNombreTTOO.getText().toString().equals("")){
            Toast.makeText(getContext(),"Debe escribir un nombre",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private TTOO getInfo(){
        return new TTOO(etNombreTTOO.getText().toString());
    }

    public interface MyCallBack{
        long getTTOOId();
        void infoChanged();
    }

}
