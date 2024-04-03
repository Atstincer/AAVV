package com.example.usuario.aavv.Reservas;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.aavv.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;

/**
 * Created by user on 2/17/2024.
 */

public class DialogFragmentDevolver extends DialogFragment {

    public static String TAG = "DialogFragmentDevolver";

    private TextView tvInfo, tvFechaDev;
    private EditText etImporte;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_devolver,container,false);
        bindComponents(view);
        setItUp();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        myCallBack = (MyCallBack)getTargetFragment();
        super.onAttach(context);
    }

    private void bindComponents(View view){
        tvFechaDev = (TextView)view.findViewById(R.id.tv_fecha_dev);
        tvInfo = (TextView)view.findViewById(R.id.tv_info_dfdevolver);
        etImporte = (EditText)view.findViewById(R.id.et_importe_devolver);
        Button btnCancelar = (Button)view.findViewById(R.id.btn_dfdevolver_cancelar);
        Button btnDevolver = (Button)view.findViewById(R.id.btn_dfdevolver_devolver);

        tvFechaDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateHandler.showDatePicker(getContext(), tvFechaDev, new DateHandler.DatePickerCallBack() {
                    @Override
                    public void dateSelected() {
                        //do nothing
                    }
                });
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnDevolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validar()){return;}
                devolver();
            }
        });
    }

    private void setItUp(){
        Reserva reserva = ReservaBDHandler.getReservaFromDB(getContext(),myCallBack.getIdReserva());
        String info = "TE: " + reserva.getNoTE() + "\n" +
                "Excursion: " + reserva.getExcursion() + "\n" +
                "Fecha: " + reserva.getFechaEjecucion() + "\n" +
                "Pax: " + reserva.getCantPaxs(false) + "\n" +
                "Precio: " + reserva.getPrecio();
        if(reserva.getEstado()==Reserva.ESTADO_DEVUELTO){
            tvFechaDev.setText(reserva.getFechaDevolucion());
            etImporte.setText(String.valueOf(reserva.getImporteDevuelto()));
        }else {
            tvFechaDev.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
        }
        tvInfo.setText(info);
    }

    private boolean validar(){
        if(etImporte.getText().toString().equals("")){
            Toast.makeText(getContext(),"Debe introducir importe a devolver",Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Double importe = Double.parseDouble(etImporte.getText().toString());
            if (importe<0 || !ReservaBDHandler.getReservaFromDB(getContext(), myCallBack.getIdReserva()).esPosibleDevolver(importe)){
                Toast.makeText(getContext(), "Importe incorrecto", Toast.LENGTH_SHORT).show();
                return false;
            }
        }catch (ParseException e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void devolver(){
        Reserva reserva = ReservaBDHandler.getReservaFromDB(getContext(),myCallBack.getIdReserva());
        String msgHistorial = tvFechaDev.getText().toString()+" DEVUELTO ("+etImporte.getText().toString()+")";
        reserva.addToHistorial(msgHistorial);
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ReservaBDHandler.CAMPO_ESTADO,Reserva.ESTADO_DEVUELTO);
        values.put(ReservaBDHandler.CAMPO_FECHA_DEVOLUCION, DateHandler.formatDateToStoreInDB(tvFechaDev.getText().toString()));
        values.put(ReservaBDHandler.CAMPO_IMPORTE_DEVUELTO,etImporte.getText().toString());
        values.put(ReservaBDHandler.CAMPO_HISTORIAL,reserva.getHistorial());
        db.update(ReservaBDHandler.TABLE_NAME,values,"id=?",new String[]{String.valueOf(myCallBack.getIdReserva())});
        Toast.makeText(getContext(),"Devolucion registrada correctamente",Toast.LENGTH_SHORT).show();
        myCallBack.udInfoEstado();
        dismiss();
    }

    public interface MyCallBack{
        long getIdReserva();
        void udInfoEstado();
    }
}
