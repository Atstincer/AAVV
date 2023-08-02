package com.example.usuario.aavv.Reservas;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.aavv.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.TTOO.TTOO;
import com.example.usuario.aavv.TTOO.TTOOBDHandler;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;

/**
 * Created by usuario on 18/07/2023.
 */

public class FragmentReservar extends Fragment {

    public static final String TAG = "FragmentReservar";

    private long idSelectedReserva;

    private TextView tvFechaConfeccion, tvFechaEjecucion;
    private EditText etNumeroTE, etNombreCliente, etAdultos, etMenores, etInfantes, etNoHab, etPrecio, etObservaciones;
    private AutoCompleteTextView actvNombreExcursion, actvAgencia, actvIdioma, actvHotel;
    private Button btn;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reservar,container,false);
        bindComponents(v);
        if(getArguments()!=null){
            idSelectedReserva = getArguments().getLong("id");
        }
        setItUP();
        myCallBack.udUI(FragmentReservar.TAG);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        myCallBack = (MyCallBack)context;
        super.onAttach(context);
    }

    private void bindComponents(View v){
        tvFechaConfeccion = (TextView)v.findViewById(R.id.tv_fecha_confeccion);
        tvFechaEjecucion = (TextView)v.findViewById(R.id.tv_fecha_ejecucion);
        etNumeroTE = (EditText)v.findViewById(R.id.et_ticket);
        etNombreCliente = (EditText)v.findViewById(R.id.et_nombre_cliente);
        etAdultos = (EditText)v.findViewById(R.id.et_adultos);
        etMenores = (EditText)v.findViewById(R.id.et_menores);
        etInfantes = (EditText)v.findViewById(R.id.et_infantes);
        etNoHab = (EditText)v.findViewById(R.id.et_hab);
        etPrecio = (EditText)v.findViewById(R.id.et_precio);
        etObservaciones = (EditText)v.findViewById(R.id.et_observaciones);
        actvNombreExcursion = (AutoCompleteTextView) v.findViewById(R.id.actv_excursion);
        actvAgencia = (AutoCompleteTextView) v.findViewById(R.id.actv_agencia);
        actvIdioma = (AutoCompleteTextView) v.findViewById(R.id.actv_idioma);
        actvHotel = (AutoCompleteTextView) v.findViewById(R.id.actv_hotel);
        btn = (Button)v.findViewById(R.id.btn_fragment_reservar);
    }

    private void setItUP(){
        tvFechaConfeccion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DateHandler.showDatePicker(getContext(), tvFechaConfeccion, new DateHandler.DatePickerCallBack() {
                    @Override
                    public void dateSelected() {
                        //do nothing
                    }
                });
            }
        });
        tvFechaEjecucion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DateHandler.showDatePicker(getContext(), tvFechaEjecucion, new DateHandler.DatePickerCallBack() {
                    @Override
                    public void dateSelected() {
                        //do nothing
                    }
                });
            }
        });

        String[] excursiones = {"Catamarán JC","Aventura marina","Nado con delfines","Nado con delfines Plus","Pesca de altura 1bote 4hrs","Pesca especializada " +
                "1bote 4hrs","Santa Clara-Remedios","Tres ciudades","Dos ciudades coloniales","Aventura en la montaña","Santa María adentro","Buceo 2 inmersiones" +
                "","Catamarán MJ exclusivo","Jeep safari"};
        ArrayAdapter<String> adapterExcursiones = new ArrayAdapter<String>(getContext(),R.layout.my_simple_dropdown_item_1line,excursiones);
        actvNombreExcursion.setThreshold(1);
        actvNombreExcursion.setAdapter(adapterExcursiones);

        ArrayAdapter<TTOO> adapterTTOO = new ArrayAdapter<TTOO>(getContext(),R.layout.my_simple_dropdown_item_1line, TTOOBDHandler.getAllTTOOfromDB(getContext()));
        actvAgencia.setThreshold(1);
        actvAgencia.setAdapter(adapterTTOO);

        String [] idiomas = {"Español","Inglés","Frances","Aleman","Italiano","Ruso"};
        ArrayAdapter<String> adapterIdiomas = new ArrayAdapter<String>(getContext(),R.layout.my_simple_dropdown_item_1line,idiomas);
        actvIdioma.setThreshold(1);
        actvIdioma.setAdapter(adapterIdiomas);

        String[] hoteles = {"Valentin","Casa del Mar","Paradisus","Muthus CSM","Gran Memories","Memories","Royalton","Starfish","Playa" +
                "","Dunas","Melia CSM","Sol CSM","Buenavista","Ensenachos","Angsana","Dhawa","Gran Aston","Sirenis","One Gallery"};
        ArrayAdapter<String> adapterHoteles = new ArrayAdapter<String>(getContext(),R.layout.my_simple_dropdown_item_1line,hoteles);
        actvHotel.setThreshold(1);
        actvHotel.setAdapter(adapterHoteles);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch (myCallBack.getEstado()){
                    case EDITAR:
                        actualizar();
                        break;
                    case NUEVO:
                        registrar();
                        break;
                }
            }
        });

        switch (myCallBack.getEstado()){
            case EDITAR:
                setUpEditarMoode();
                break;
            case NUEVO:
                setUpNuevoMode();
                break;
        }
    }

    private void setUpEditarMoode(){
        showInfoReserva(ReservaBDHandler.getReservaFromDB(getContext(), idSelectedReserva));
        btn.setText("Actualizar");
    }

    private void setUpNuevoMode(){
        tvFechaConfeccion.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
        btn.setText("Registrar");
    }

    private void actualizar(){
        if(!isvalid()){return;}
        Reserva reserva = getNuevaReserva();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues values = ReservaBDHandler.getContentValues(reserva);
        db.update(ReservaBDHandler.TABLE_NAME,values,"id=?",new String[]{String.valueOf(idSelectedReserva)});
        Toast.makeText(getContext(),"Actualizado correctamente",Toast.LENGTH_SHORT).show();
    }

    private void registrar(){
        if(!isvalid()){return;}
        Reserva nuevaReserva = getNuevaReserva();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues values = ReservaBDHandler.getContentValues(nuevaReserva);
        db.insert(ReservaBDHandler.TABLE_NAME,null,values);
        limpiarCampos();
        Toast.makeText(getContext(),"Registrado correctamente",Toast.LENGTH_SHORT).show();
    }

    private void showInfoReserva(Reserva reserva){
        tvFechaConfeccion.setText(reserva.getFechaConfeccion());
        tvFechaEjecucion.setText(reserva.getFechaEjecucion());
        etNumeroTE.setText(reserva.getNoTE());
        etNombreCliente.setText(reserva.getCliente());
        etAdultos.setText(String.valueOf(reserva.getAdultos()));
        etMenores.setText(String.valueOf(reserva.getMenores()));
        etInfantes.setText(String.valueOf(reserva.getInfantes()));
        etNoHab.setText(reserva.getNoHab());
        etPrecio.setText(String.valueOf(reserva.getPrecio()));
        etObservaciones.setText(reserva.getObservaciones());
        actvNombreExcursion.setText(reserva.getExcursion());
        actvAgencia.setText(reserva.getAgencia());
        actvIdioma.setText(reserva.getIdioma());
        actvHotel.setText(reserva.getHotel());
    }

    private void limpiarCampos(){
        etNumeroTE.setText("");
        actvNombreExcursion.setText("");
        etNombreCliente.setText("");
        etAdultos.setText("");
        etMenores.setText("");
        etInfantes.setText("");
        tvFechaEjecucion.setText("Fecha");
        actvAgencia.setText("");
        actvHotel.setText("");
        etNoHab.setText("");
        etPrecio.setText("");
        actvIdioma.setText("");
        etObservaciones.setText("");
    }

    private Reserva getNuevaReserva(){
        Reserva reserva = new Reserva();
        reserva.setFechaConfeccion(tvFechaConfeccion.getText().toString());
        reserva.setNoTE(etNumeroTE.getText().toString());
        reserva.setExcursion(actvNombreExcursion.getText().toString());
        reserva.setCliente(etNombreCliente.getText().toString());
        reserva.setAdultos(Integer.parseInt(etAdultos.getText().toString()));
        if(!etMenores.getText().toString().equals("")) {
            reserva.setMenores(Integer.parseInt(etMenores.getText().toString()));
        }else {
            reserva.setMenores(0);
        }
        if(!etInfantes.getText().toString().equals("")) {
            reserva.setInfantes(Integer.parseInt(etInfantes.getText().toString()));
        }else {
            reserva.setInfantes(0);
        }
        reserva.setFechaEjecucion(tvFechaEjecucion.getText().toString());
        reserva.setAgencia(actvAgencia.getText().toString());
        reserva.setHotel(actvHotel.getText().toString());
        reserva.setNoHab(etNoHab.getText().toString());
        if(!etPrecio.getText().toString().equals("")) {
            reserva.setPrecio(Double.parseDouble(etPrecio.getText().toString()));
        }else {
            reserva.setPrecio(0);
        }
        reserva.setIdioma(actvIdioma.getText().toString());
        reserva.setObservaciones(etObservaciones.getText().toString());
        return reserva;
    }

    private boolean isvalid(){
        String falta = "Faltó:";
        boolean makeToastAtEnd = false;

        //obligatorio
        if(etNumeroTE.getText().toString().equals("")){
            Toast.makeText(getContext(),"Falta número de TE",Toast.LENGTH_SHORT).show();
            return false;
        }

        //obligatorio
        if(actvNombreExcursion.getText().toString().equals("")){
            Toast.makeText(getContext(),"Falta nombre de excurción",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(etNombreCliente.getText().toString().equals("")){
            falta += "\n- nombre de cliente";
            makeToastAtEnd = true;
        }

        //obligatorio
        if(etAdultos.getText().toString().equals("")){
            Toast.makeText(getContext(),"Falta al menos 1 adulto",Toast.LENGTH_SHORT).show();
            return false;
        }

        //obligatorio
        if(tvFechaEjecucion.getText().toString().equals("Fecha")){
            Toast.makeText(getContext(),"Falta la fecha de la excursión",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(actvAgencia.getText().toString().equals("")){
            falta += "\n- agencia";
            makeToastAtEnd = true;
        }

        //obligatorio
        if(actvHotel.getText().toString().equals("")){
            Toast.makeText(getContext(),"Falta el hotel",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(etNoHab.getText().toString().equals("")){
            falta += "\n- número de habitación";
            makeToastAtEnd = true;
        }
        if(etPrecio.getText().toString().equals("")){
            falta += "\n- precio";
            makeToastAtEnd = true;
        }
        if(actvIdioma.getText().toString().equals("")){
            falta += "\n- idioma";
            makeToastAtEnd = true;
        }

        if(makeToastAtEnd){
            Toast.makeText(getContext(),falta,Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public interface MyCallBack{
        MisConstantes.Estado getEstado();
        void udUI(String tag);
    }

}
