package com.example.usuario.aavv.Reservas;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.aavv.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.aavv.Excursiones.Excursion;
import com.example.usuario.aavv.Excursiones.ExcursionBDHandler;
import com.example.usuario.aavv.Hoteles.Hotel;
import com.example.usuario.aavv.Hoteles.HotelBDHandler;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.TTOO.TTOO;
import com.example.usuario.aavv.TTOO.TTOOBDHandler;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;

import java.util.List;


/**
 * Created by usuario on 18/07/2023.
 */

public class FragmentReservar extends Fragment {

    public static final String TAG = "FragmentReservar";

    private long idSelectedReserva;
    private List<Excursion> excursionesList;

    private TextView tvFechaConfeccion, tvFechaEjecucion, tvEstado;
    private EditText etNumeroTE, etNombreCliente, etAdultos, etMenores, etInfantes, etAcompanante, etNoHab, etPrecio, etObservaciones;
    private AutoCompleteTextView actvNombreExcursion, actvAgencia, actvIdioma, actvHotel;
    private Button btn;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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
        tvEstado = (TextView)v.findViewById(R.id.tv_estado);
        etNombreCliente = (EditText)v.findViewById(R.id.et_nombre_cliente);
        etAdultos = (EditText)v.findViewById(R.id.et_adultos);
        etMenores = (EditText)v.findViewById(R.id.et_menores);
        etInfantes = (EditText)v.findViewById(R.id.et_infantes);
        etAcompanante = (EditText)v.findViewById(R.id.et_acompanante);
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
                        actvAgencia.requestFocus();
                    }
                });
            }
        });

        String[] excursionesPordefecto = {"Catamarán JC","Aventura marina","Nado con delfines","Nado con delfines Plus","Pesca de altura 1bote 4hrs","Pesca especializada " +
                "1bote 4hrs","Santa Clara-Remedios","Tres ciudades","Dos ciudades coloniales","Aventura en la montaña","Santa María adentro","Buceo 2 inmersiones" +
                "", "Buceo 1 inmersión","Catamarán MJ exclusivo","Jeep safari"};
        excursionesList = ExcursionBDHandler.getAllExcursionesfromDB(getContext());

        //ArrayAdapter adapterExcursiones = new ArrayAdapter<>(getContext(),R.layout.my_simple_dropdown_item_1line,excursiones);
        actvNombreExcursion.setThreshold(1);
        if(!excursionesList.isEmpty()) {
            actvNombreExcursion.setAdapter(new ArrayAdapter<>(getContext(),R.layout.my_simple_dropdown_item_1line,excursionesList));
        }else {
            actvNombreExcursion.setAdapter(new ArrayAdapter<>(getContext(),R.layout.my_simple_dropdown_item_1line,excursionesPordefecto));
        }
        actvNombreExcursion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                predictPrice();
            }
        });

        ArrayAdapter<TTOO> adapterTTOO = new ArrayAdapter<TTOO>(getContext(),R.layout.my_simple_dropdown_item_1line, TTOOBDHandler.getAllTTOOfromDB(getContext()));
        actvAgencia.setThreshold(1);
        actvAgencia.setAdapter(adapterTTOO);

        String [] idiomas = {"Español","Inglés","Frances","Aleman","Italiano","Ruso"};
        ArrayAdapter<String> adapterIdiomas = new ArrayAdapter<String>(getContext(),R.layout.my_simple_dropdown_item_1line,idiomas);
        actvIdioma.setThreshold(1);
        actvIdioma.setAdapter(adapterIdiomas);

        String[] hotelesPorDefecto = {"Valentin","Casa del Mar","Paradisus","Muthus CSM","Gran Memories","Memories","Royalton","Starfish","Playa" +
                "","Melia las Dunas","Melia CSM","Sol CSM","Buenavista","Ensenachos","Angsana","Dhawa","Gran Aston","Sirenis","One Gallery"};
        List<Hotel> hoteles = HotelBDHandler.getAllHotelesfromDB(getContext());

        actvHotel.setThreshold(1);
        if(!hoteles.isEmpty()){
            actvHotel.setAdapter(new ArrayAdapter<>(getContext(),R.layout.my_simple_dropdown_item_1line,hoteles));
        }else {
            actvHotel.setAdapter(new ArrayAdapter<>(getContext(),R.layout.my_simple_dropdown_item_1line,hotelesPorDefecto));
        }

        setOnTextChangedListener(etAdultos);
        setOnTextChangedListener(etMenores);
        setOnTextChangedListener(etAcompanante);

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
        tvEstado.setText("");
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
        getReadyForNextTE();
        Toast.makeText(getContext(),"Registrado correctamente",Toast.LENGTH_SHORT).show();
    }

    private void showInfoReserva(Reserva reserva){
        tvFechaConfeccion.setText(reserva.getFechaConfeccion());
        tvFechaEjecucion.setText(reserva.getFechaEjecucion());
        etNumeroTE.setText(reserva.getNoTE());
        actvNombreExcursion.setText(reserva.getExcursion());
        showEstado(reserva.getEstado());
        etNombreCliente.setText(reserva.getCliente());
        if(reserva.getAdultos()!=0) {
            etAdultos.setText(String.valueOf(reserva.getAdultos()));
        }
        if(reserva.getMenores()!=0) {
            etMenores.setText(String.valueOf(reserva.getMenores()));
        }
        if(reserva.getInfantes()!=0) {
            etInfantes.setText(String.valueOf(reserva.getInfantes()));
        }
        if(reserva.getAcompanantes()!=0) {
            etAcompanante.setText(String.valueOf(reserva.getAcompanantes()));
        }
        etNoHab.setText(reserva.getNoHab());
        etPrecio.setText(String.valueOf(reserva.getPrecio()));
        etObservaciones.setText(reserva.getObservaciones());
        actvAgencia.setText(reserva.getAgencia());
        actvIdioma.setText(reserva.getIdioma());
        actvHotel.setText(reserva.getHotel());
    }

    private void showEstado(int estado){
        switch (estado){
            case Reserva.ESTADO_ACTIVO:
                tvEstado.setText("");
                break;
            case Reserva.ESTADO_CANCELADO:
                tvEstado.setText("CANCELADO");
                break;
            case Reserva.ESTADO_DEVUELTO:
                tvEstado.setText("DEVUELTO");
                break;
        }
        getActivity().invalidateOptionsMenu();
    }

    private void getReadyForNextTE(){
        String teAnterior = etNumeroTE.getText().toString();
        limpiarCampos();
        try {
            int newTE = Integer.parseInt(teAnterior) + 1;
            String newTEString = String.valueOf(newTE);
            int difTamano = teAnterior.length() - newTEString.length();
            if(difTamano>0){
                for(int i=0;i<difTamano;i++){
                    newTEString = "0"+newTEString;
                }
            }
            etNumeroTE.setText(newTEString);
            actvNombreExcursion.requestFocus();
            //InputMethodManager imm = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
        }catch (Exception e){
            //do nothing
            etNumeroTE.requestFocus();
        }
    }
    
    private void predictPrice(Excursion excursion){
        if(excursion == null){return;}
        float precioTotal = 0;
        int adultos = 0;
        int menores = 0;
        int acompanantes = 0;
        if(!etAdultos.getText().toString().equals("")){
            adultos += Integer.parseInt(etAdultos.getText().toString());
        }
        if(!etMenores.getText().toString().equals("")){
            menores += Integer.parseInt(etMenores.getText().toString());
        }
        if(!etAcompanante.getText().toString().equals("")){
            acompanantes += Integer.parseInt(etAcompanante.getText().toString());
        }
        precioTotal += acompanantes*excursion.getPrecioAcomp();
        switch (excursion.getTipoPrecio()){
            case Excursion.PRECIO_X_PAX:
                precioTotal += adultos*excursion.getPrecioAd() + (menores*excursion.getPrecioMenor());
                break;
            case Excursion.PRECIO_X_RANGO:
                if(excursion.getRangoHasta()<=1){return;}
                if(adultos>0){
                    precioTotal += excursion.getPrecioRango();
                }
                if(adultos-excursion.getRangoHasta()>0){
                    precioTotal += (adultos-excursion.getRangoHasta())*excursion.getPrecioAd();
                    precioTotal += menores*excursion.getPrecioMenor();
                }else {
                    if((adultos+menores)-excursion.getRangoHasta()>0){
                        precioTotal += ((adultos+menores)-excursion.getRangoHasta())*excursion.getPrecioMenor();
                    }
                }
                break;
        }
        etPrecio.setText(String.valueOf(precioTotal));
    }

    private void predictPrice(){
        String excursionSelected = actvNombreExcursion.getText().toString();
        for(Excursion excursion:excursionesList){
            if(excursion.getNombre().equals(excursionSelected)){
                predictPrice(excursion);
                return;
            }
        }
    }

    private void setOnTextChangedListener(EditText et){
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                predictPrice();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void limpiarCampos(){
        etNumeroTE.setText("");
        actvNombreExcursion.setText("");
        etNombreCliente.setText("");
        etAdultos.setText("");
        etMenores.setText("");
        etInfantes.setText("");
        etAcompanante.setText("");
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
        if(!etAdultos.getText().toString().equals("")) {
            reserva.setAdultos(Integer.parseInt(etAdultos.getText().toString()));
        }else {
            reserva.setAdultos(0);
        }
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
        if(!etAcompanante.getText().toString().equals("")) {
            reserva.setAcompanante(Integer.parseInt(etAcompanante.getText().toString()));
        }else {
            reserva.setAcompanante(0);
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
            //Toast.makeText(getContext(),"Falta al menos 1 adulto",Toast.LENGTH_SHORT).show();
            //return false;
            falta += "\n- tiene 0 clientes adultos";
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

    private void setActivo(){
        setEstado(Reserva.ESTADO_ACTIVO);
        showEstado(Reserva.ESTADO_ACTIVO);
    }

    private void setCancelado(){
        setEstado(Reserva.ESTADO_CANCELADO);
        showEstado(Reserva.ESTADO_CANCELADO);
    }

    private void setDevuelto(){
        setEstado(Reserva.ESTADO_DEVUELTO);
        showEstado(Reserva.ESTADO_DEVUELTO);
    }

    private void setEstado(int estado){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ReservaBDHandler.CAMPO_ESTADO,estado);
        db.update(ReservaBDHandler.TABLE_NAME,values,"id=?",new String[]{String.valueOf(idSelectedReserva)});
        String mensaje = "";
        switch (estado){
            case Reserva.ESTADO_ACTIVO:
                mensaje = "Activada";
                break;
            case Reserva.ESTADO_CANCELADO:
                mensaje = "Cancelado";
                break;
            case Reserva.ESTADO_DEVUELTO:
                mensaje = "Devuelto";
                break;
        }
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }

    private void confirmarEliminar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Seguro que desea eliminar esta reserva?");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                eliminarReserva();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        AlertDialog confirmationDialog = builder.create();
        confirmationDialog.show();
    }

    private void eliminarReserva(){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.delete(ReservaBDHandler.TABLE_NAME,"id=?",new String[]{String.valueOf(idSelectedReserva)});
        Toast.makeText(getContext(),"Eliminado correctamente",Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){menu.clear();}
        if(myCallBack.getEstado()== MisConstantes.Estado.EDITAR){
            switch (ReservaBDHandler.getReservaFromDB(getContext(),idSelectedReserva).getEstado()){
                case Reserva.ESTADO_ACTIVO:
                    inflater.inflate(R.menu.menu_frag_reservar_activo,menu);
                    break;
                case Reserva.ESTADO_CANCELADO:
                    inflater.inflate(R.menu.menu_frag_reservar_cancelado,menu);
                    break;
                case Reserva.ESTADO_DEVUELTO:
                    inflater.inflate(R.menu.menu_frag_reservar_devuelto,menu);
                    break;
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_set_activo:
                setActivo();
                break;
            case R.id.menu_item_set_cancelado:
                setCancelado();
                break;
            case R.id.menu_item_set_devuelto:
                setDevuelto();
                break;
            case R.id.menu_item_eliminar_reserva:
                confirmarEliminar();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface MyCallBack{
        MisConstantes.Estado getEstado();
        void udUI(String tag);
    }

}
