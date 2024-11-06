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
import android.util.Log;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.aavv.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
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
import java.util.Objects;


/**
 * Created by usuario on 18/07/2023.
 */

public class FragmentReservar extends Fragment implements DialogFragmentDevolver.MyCallBack, DialogFragmentHistorial.MyCallBack{

    public static final String TAG = "FragmentReservar";

    private String idSelectedReserva;
    //private Reserva reservaSelected;
    private List<Excursion> excursionesList;

    private CheckBox checkBoxIncluirRepVenta;
    private TextView tvFechaConfeccion, tvFechaEjecucion, tvFechaRepVenta, tvEstado;
    private EditText etNumeroTE, etNombreCliente, etAdultos, etMenores, etInfantes, etAcompanante, etNoHab, etPrecio, etObservaciones;
    private AutoCompleteTextView actvNombreExcursion, actvAgencia, actvIdioma, actvHotel;
    private Button btn;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_reservar, container, false);
        bindComponents(v);
        if(getArguments()!=null){
            String fechaLiq = getArguments().getString("fechaLiq");
            String lastTE = getArguments().getString("lastTE");
            if(fechaLiq != null && !fechaLiq.isEmpty()) {
                tvFechaConfeccion.setText(fechaLiq);
            }
            if(lastTE != null && !lastTE.isEmpty()){
                getReadyForNextTE(lastTE);
            }
        }
        if(savedInstanceState!=null){
            String fechaConf = savedInstanceState.getString("fechaConf");
            String fechaEjec = savedInstanceState.getString("fechaEjec");
            if(fechaConf != null){
                tvFechaConfeccion.setText(fechaConf);
            }
            if(fechaEjec != null){
                tvFechaEjecucion.setText(fechaEjec);
            }
        }
        setItUP();
        myCallBack.udUI(FragmentReservar.TAG);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        myCallBack = (MyCallBack) context;
        super.onAttach(context);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(tvFechaConfeccion!=null) {
            outState.putString("fechaConf", tvFechaConfeccion.getText().toString());
        }
        if(tvFechaEjecucion!=null) {
            outState.putString("fechaEjec", tvFechaEjecucion.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void bindComponents(View v) {
        checkBoxIncluirRepVenta = v.findViewById(R.id.checkbox_incluir_rep_venta);
        tvFechaConfeccion = v.findViewById(R.id.tv_fecha_confeccion);
        tvFechaEjecucion = v.findViewById(R.id.tv_fecha_ejecucion);
        tvFechaRepVenta = v.findViewById(R.id.tv_fecha_repventa_reservar);
        etNumeroTE = v.findViewById(R.id.et_ticket);
        tvEstado = v.findViewById(R.id.tv_estado);
        etNombreCliente = v.findViewById(R.id.et_nombre_cliente);
        etAdultos = v.findViewById(R.id.et_adultos);
        etMenores = v.findViewById(R.id.et_menores);
        etInfantes = v.findViewById(R.id.et_infantes);
        etAcompanante = v.findViewById(R.id.et_acompanante);
        etNoHab = v.findViewById(R.id.et_hab);
        etPrecio = v.findViewById(R.id.et_precio);
        etObservaciones = v.findViewById(R.id.et_observaciones);
        actvNombreExcursion = v.findViewById(R.id.actv_excursion);
        actvAgencia = v.findViewById(R.id.actv_agencia);
        actvIdioma = v.findViewById(R.id.actv_idioma);
        actvHotel = v.findViewById(R.id.actv_hotel);
        btn = v.findViewById(R.id.btn_fragment_reservar);
    }

    private void setItUP() {
        tvFechaConfeccion.setOnClickListener(view -> DateHandler.showDatePicker(getContext(), tvFechaConfeccion, () -> {
            //do nothing
        }));
        tvFechaEjecucion.setOnClickListener(view -> DateHandler.showDatePicker(getContext(), tvFechaEjecucion,
                () -> actvAgencia.requestFocus()));

        tvFechaRepVenta.setOnClickListener(view -> DateHandler.showDatePicker(getContext(), tvFechaRepVenta,
                () -> actvAgencia.requestFocus()));

        String[] excursionesPordefecto = {"Catamarán JC", "Aventura marina", "Nado con delfines", "Nado con delfines Plus",
                "Pesca de altura 1bote 4hrs", "Pesca especializada 1bote 4hrs", "Santa Clara-Remedios", "Tres ciudades",
                "Dos ciudades coloniales", "Aventura en la montaña", "Santa María adentro", "Buceo 2 inmersiones", "Buceo 1 inmersión",
                "Catamarán MJ exclusivo", "Jeep safari"};
        excursionesList = ExcursionBDHandler.getAllExcursionesfromDB(getContext());

        actvNombreExcursion.setThreshold(1);
        if (!excursionesList.isEmpty()) {
            actvNombreExcursion.setAdapter(new ArrayAdapter<>(getContext(), R.layout.my_simple_dropdown_item_1line, excursionesList));
        } else {
            actvNombreExcursion.setAdapter(new ArrayAdapter<>(getContext(), R.layout.my_simple_dropdown_item_1line, excursionesPordefecto));
        }
        actvNombreExcursion.setOnItemClickListener((adapterView, view, i, l) -> {
            predictPrice();
            etNombreCliente.requestFocus();
        });

        ArrayAdapter<TTOO> adapterTTOO = new ArrayAdapter<>(getContext(), R.layout.my_simple_dropdown_item_1line, TTOOBDHandler.getAllTTOOfromDB(getContext()));
        actvAgencia.setThreshold(1);
        actvAgencia.setAdapter(adapterTTOO);
        actvAgencia.setOnItemClickListener((adapterView, view, i, l) -> actvHotel.requestFocus());

        String[] idiomas = {"Español", "Inglés", "Frances", "Aleman", "Italiano", "Ruso"};
        ArrayAdapter<String> adapterIdiomas = new ArrayAdapter<String>(getContext(), R.layout.my_simple_dropdown_item_1line, idiomas);
        actvIdioma.setThreshold(1);
        actvIdioma.setAdapter(adapterIdiomas);
        actvIdioma.setOnItemClickListener((adapterView, view, i, l) -> etObservaciones.requestFocus());

        String[] hotelesPorDefecto = {"Valentin", "Casa del Mar", "Paradisus", "Muthus CSM", "Gran Memories", "Memories", "Royalton",
                "Starfish", "Playa CSM","Melia las Dunas", "Melia CSM", "Sol CSM", "Buenavista", "Ensenachos", "Angsana", "Dhawa",
                "Gran Aston", "Sirenis", "One Gallery"};
        List<Hotel> hoteles = HotelBDHandler.getAllHotelesfromDB(getContext());

        actvHotel.setThreshold(1);
        if (!hoteles.isEmpty()) {
            actvHotel.setAdapter(new ArrayAdapter<>(getContext(), R.layout.my_simple_dropdown_item_1line, hoteles));
        } else {
            actvHotel.setAdapter(new ArrayAdapter<>(getContext(), R.layout.my_simple_dropdown_item_1line, hotelesPorDefecto));
        }
        actvHotel.setOnItemClickListener((adapterView, view, i, l) -> etNoHab.requestFocus());

        setOnTextChangedListener(etAdultos);
        setOnTextChangedListener(etMenores);
        setOnTextChangedListener(etAcompanante);

        checkBoxIncluirRepVenta.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                tvFechaRepVenta.setVisibility(View.VISIBLE);
            }else {
                tvFechaRepVenta.setVisibility(View.GONE);
            }
        });

        btn.setOnClickListener(view -> {
            switch (myCallBack.getEstadoFragmentReservar()) {
                case EDITAR:
                    actualizar();
                    break;
                case NUEVO:
                    registrar();
                    break;
            }
        });

        switch (myCallBack.getEstadoFragmentReservar()) {
            case EDITAR:
                setUpEditarMoode();
                break;
            case NUEVO:
                setUpNuevoMode();
                break;
        }
    }

    private void setUpEditarMoode() {
        if (getArguments() != null) {
            idSelectedReserva = getArguments().getString("id");
            //reservaSelected = ReservaBDHandler.getReservaFromDB(getContext(),idSelectedReserva);
        }
        showInfoReserva(ReservaBDHandler.getReservaFromDB(getContext(), idSelectedReserva));
//        layoutRepVenta.setVisibility(View.VISIBLE);
        etNumeroTE.setEnabled(false);
        btn.setText("Actualizar");
    }

    private void setUpNuevoMode() {
        if(tvFechaConfeccion.getText().toString().equals("Fecha")) {
            tvFechaConfeccion.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
        }
        checkBoxIncluirRepVenta.setChecked(true);
        tvFechaRepVenta.setText(tvFechaConfeccion.getText().toString());
        tvEstado.setText("");
        btn.setText("Registrar");
    }

    private void actualizar() {
        if (!isvalid()) {
            return;
        }
        Reserva reserva = getNuevaReserva();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        String msgHistorial = DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR)+" ACTUALIZADO";
        Reserva reservaBD = ReservaBDHandler.getReservaFromDB(getContext(),idSelectedReserva);
        if(!reserva.getObservaciones().isEmpty() &&
                !reserva.getObservaciones().equalsIgnoreCase(reservaBD.getObservaciones())){
            msgHistorial += "\nobs: " + reserva.getObservaciones();
        }
        reserva.addToHistorial(msgHistorial);
        ContentValues values = ReservaBDHandler.getContentValues(reserva);
        db.update(ReservaBDHandler.TABLE_NAME, values, ReservaBDHandler.CAMPO_NUMERO_TE+"=?",
                new String[]{idSelectedReserva});
        Toast.makeText(getContext(), "Actualizado correctamente", Toast.LENGTH_SHORT).show();
    }

    private void registrar() {
        if (!isvalid()) {
            return;
        }
        Reserva nuevaReserva = getNuevaReserva();
        nuevaReserva.setFechaOriginalEjecucion(nuevaReserva.getFechaEjecucion());
        String msgHistorial = DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR)+" REGISTRADO";
        if(nuevaReserva.getObservaciones() != null &&
                !nuevaReserva.getObservaciones().isEmpty()){
            msgHistorial += "\nobs: " + nuevaReserva.getObservaciones();
        }
        nuevaReserva.addToHistorial(msgHistorial);
        registrar(nuevaReserva);
    }

    private void registrar(Reserva reserva) {
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues values = ReservaBDHandler.getContentValues(reserva);
        long result = db.insert(ReservaBDHandler.TABLE_NAME, null, values);
        if(result >= 0) {
            getReadyForNextTE();
            Toast.makeText(getContext(), "Registrado correctamente", Toast.LENGTH_SHORT).show();
        }else {
            StringBuilder msj = new StringBuilder();
            if(!checkIfTeExist(reserva.getNoTE()).isEmpty()) {
                msj.append("La reserva ya existe:\n");
                msj.append(checkIfTeExist(reserva.getNoTE()));
            }else {
                msj.append("No se pudo registrar la reserva");
            }
            Toast.makeText(getContext(), msj.toString(), Toast.LENGTH_LONG).show();
        }
        Log.d("registrar","db.insert = "+result);
    }

    private String checkIfTeExist(String te){
        Reserva reserva = ReservaBDHandler.getReservaFromDB(getContext(),te);
        if(!reserva.getExcursion().isEmpty()){
            return "TE: "+reserva.getNoTE() + "\n" + reserva.getExcursion() +
                    "\nReservada el: " + reserva.getFechaConfeccion();
        }
        return "";
    }

    private void showInfoReserva(Reserva reserva) {
        tvFechaConfeccion.setText(reserva.getFechaConfeccion());
        tvFechaEjecucion.setText(reserva.getFechaEjecucion());
        etNumeroTE.setText(reserva.getNoTE());
        actvNombreExcursion.setText(reserva.getExcursion());
        showEstado();
        etNombreCliente.setText(reserva.getCliente());
        if (reserva.getAdultos() != 0) {
            etAdultos.setText(String.valueOf(reserva.getAdultos()));
        }
        if (reserva.getMenores() != 0) {
            etMenores.setText(String.valueOf(reserva.getMenores()));
        }
        if (reserva.getInfantes() != 0) {
            etInfantes.setText(String.valueOf(reserva.getInfantes()));
        }
        if (reserva.getAcompanantes() != 0) {
            etAcompanante.setText(String.valueOf(reserva.getAcompanantes()));
        }
        etNoHab.setText(reserva.getNoHab());
        etPrecio.setText(String.valueOf(reserva.getPrecio()));
        etObservaciones.setText(reserva.getObservaciones());
        actvAgencia.setText(reserva.getAgencia());
        actvIdioma.setText(reserva.getIdioma());
        actvHotel.setText(reserva.getHotel());
        checkBoxIncluirRepVenta.setChecked(reserva.incluirEnRepVenta());
        if(reserva.getFechaReporteVenta()!=null || !reserva.getFechaReporteVenta().isEmpty()) {
            tvFechaRepVenta.setText(reserva.getFechaReporteVenta());
        }
        if (reserva.incluirEnRepVenta()) {
            tvFechaRepVenta.setVisibility(View.VISIBLE);
        }else {
            tvFechaRepVenta.setVisibility(View.GONE);
        }
    }

    private void showEstado() {
        Reserva reserva = ReservaBDHandler.getReservaFromDB(getContext(),idSelectedReserva);
        switch (reserva.getEstado()) {
            case Reserva.ESTADO_ACTIVO:
                tvEstado.setText("");
                break;
            case Reserva.ESTADO_CANCELADO:
                String msgCancelado = "CANCELADO";
                if(reserva.getFechaCancelacion()!=null && !reserva.getFechaCancelacion().isEmpty()){
                    msgCancelado += "\n("+reserva.getFechaCancelacion().substring(0,5)+")";
                }
                tvEstado.setText(msgCancelado);
                break;
            case Reserva.ESTADO_DEVUELTO:
                String msgDevuelto = "DEVUELTO";
                String fechaDev = "";
                double importeDev = reserva.getImporteDevuelto();
                if(reserva.getFechaDevolucion()!=null && !reserva.getFechaDevolucion().isEmpty()){
                    fechaDev = reserva.getFechaDevolucion().substring(0,5) + " ";
                }
                if(!fechaDev.isEmpty() || importeDev>0){
                    // + fechaDev + "-" + reserva.getImporteDevuelto() + ")"
                    msgDevuelto += "\n("+fechaDev;
                    if(!fechaDev.isEmpty() && importeDev>0){
                        msgDevuelto += " - ";
                    }
                    if(importeDev>0){
                        msgDevuelto += importeDev;
                    }
                    msgDevuelto += ")";
                }
                tvEstado.setText(msgDevuelto);
                break;
        }
        Objects.requireNonNull(getActivity()).invalidateOptionsMenu();
    }

    private void getReadyForNextTE() {
        String teAnterior = etNumeroTE.getText().toString();
        getReadyForNextTE(teAnterior);
    }

    private void getReadyForNextTE(String teAnterior) {
        limpiarCampos();
        String nextTE = getNextTE(teAnterior);
        if(nextTE != null && !nextTE.isEmpty()){
            etNumeroTE.setText(nextTE);
        }
        checkBoxIncluirRepVenta.setChecked(true);
        tvFechaRepVenta.setText(tvFechaConfeccion.getText().toString());
        actvNombreExcursion.requestFocus();
    }

    private String getNextTE(String lastTE){
        if(lastTE == null || lastTE.isEmpty()){return lastTE;}
        try {
            int newTE = Integer.parseInt(lastTE) + 1;
            StringBuilder newTEString = new StringBuilder(String.valueOf(newTE));
            int difTamano = lastTE.length() - newTEString.length();
            if (difTamano > 0) {
                for (int i = 0; i < difTamano; i++) {
                    newTEString.insert(0, "0");
                }
            }
            return newTEString.toString();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return lastTE;
        }
    }

    private void predictPrice(Excursion excursion) {
        if (excursion == null || myCallBack.getEstadoFragmentReservar() == MisConstantes.Estado.EDITAR ||
                !MySharedPreferences.getPredecirPrecio(Objects.requireNonNull(getContext()))) {
            return;
        }
        float precioTotal = 0;
        int adultos = 0;
        int menores = 0;
        int acompanantes = 0;
        if (!etAdultos.getText().toString().isEmpty()) {
            adultos += Integer.parseInt(etAdultos.getText().toString());
        }
        if (!etMenores.getText().toString().isEmpty()) {
            menores += Integer.parseInt(etMenores.getText().toString());
        }
        if (!etAcompanante.getText().toString().isEmpty()) {
            acompanantes += Integer.parseInt(etAcompanante.getText().toString());
        }
        precioTotal += acompanantes * excursion.getPrecioAcomp();
        switch (excursion.getTipoPrecio()) {
            case Excursion.PRECIO_X_PAX:
                precioTotal += adultos * excursion.getPrecioAd() + (menores * excursion.getPrecioMenor());
                break;
            case Excursion.PRECIO_X_RANGO:
                if (excursion.getRangoHasta() < 1) {
                    return;
                }
                if (adultos > 0) {
                    precioTotal += excursion.getPrecioRango();
                }
                if (adultos - excursion.getRangoHasta() > 0) {
                    precioTotal += (adultos - excursion.getRangoHasta()) * excursion.getPrecioAd();
                    precioTotal += menores * excursion.getPrecioMenor();
                } else {
                    if ((adultos + menores) - excursion.getRangoHasta() > 0) {
                        precioTotal += ((adultos + menores) - excursion.getRangoHasta()) * excursion.getPrecioMenor();
                    }
                }
                break;
        }
        if(precioTotal>0) {
            etPrecio.setText(String.valueOf(precioTotal));
        }
    }

    private void predictPrice() {
        if (getExcursionIfMatch() != null) {
            predictPrice(getExcursionIfMatch());
        }
    }

    private Excursion getExcursionIfMatch() {
        String excursionSelected = actvNombreExcursion.getText().toString();
        for (Excursion excursion : excursionesList) {
            if (excursion.getNombre().equals(excursionSelected)) {
                return excursion;
            }
        }
        return null;
    }

    private void setOnTextChangedListener(EditText et) {
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

    private void limpiarCampos() {
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

    private Reserva getNuevaReserva() {
        Reserva reserva;
        if(myCallBack.getEstadoFragmentReservar() == MisConstantes.Estado.EDITAR){
            reserva = ReservaBDHandler.getReservaFromDB(getContext(),idSelectedReserva);
        } else {
            reserva = new Reserva();
        }
        reserva.setFechaConfeccion(tvFechaConfeccion.getText().toString());
        reserva.setNoTE(etNumeroTE.getText().toString());
        reserva.setExcursion(actvNombreExcursion.getText().toString());
        reserva.setCliente(etNombreCliente.getText().toString());
        if (!etAdultos.getText().toString().isEmpty()) {
            reserva.setAdultos(Integer.parseInt(etAdultos.getText().toString()));
        } else {
            reserva.setAdultos(0);
        }
        if (!etMenores.getText().toString().isEmpty()) {
            reserva.setMenores(Integer.parseInt(etMenores.getText().toString()));
        } else {
            reserva.setMenores(0);
        }
        if (!etInfantes.getText().toString().isEmpty()) {
            reserva.setInfantes(Integer.parseInt(etInfantes.getText().toString()));
        } else {
            reserva.setInfantes(0);
        }
        if (!etAcompanante.getText().toString().isEmpty()) {
            reserva.setAcompanante(Integer.parseInt(etAcompanante.getText().toString()));
        } else {
            reserva.setAcompanante(0);
        }
        if(tvFechaEjecucion.getText().toString().equals("Fecha")){
            reserva.setFechaEjecucion("");
        } else {
            reserva.setFechaEjecucion(tvFechaEjecucion.getText().toString());
        }
        reserva.setIncluirEnRepVenta(checkBoxIncluirRepVenta.isChecked());
        if(!tvFechaRepVenta.getText().toString().equals("fecha")) {
            reserva.setFechaReporteVenta(tvFechaRepVenta.getText().toString());
        }
        reserva.setAgencia(actvAgencia.getText().toString());
        reserva.setHotel(actvHotel.getText().toString());
        reserva.setNoHab(etNoHab.getText().toString());
        if (!etPrecio.getText().toString().isEmpty()) {
            reserva.setPrecio(Double.parseDouble(etPrecio.getText().toString()));
        } else {
            reserva.setPrecio(0);
        }
        reserva.setIdioma(actvIdioma.getText().toString());
        reserva.setObservaciones(etObservaciones.getText().toString());
        return reserva;
    }

    private boolean isvalid() {
        String falta = "Faltó:";
        boolean makeToastAtEnd = false;

        if(myCallBack.getEstadoFragmentReservar()== MisConstantes.Estado.EDITAR){
            if(ReservaBDHandler.getReservaFromDB(getContext(),idSelectedReserva).getEstado()==Reserva.ESTADO_CANCELADO){
                Toast.makeText(getContext(),"Debe activar la reserva",Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        //obligatorio
        if (etNumeroTE.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Falta número de TE", Toast.LENGTH_SHORT).show();
            return false;
        }

        //obligatorio
        if (actvNombreExcursion.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Falta nombre de excurción", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etNombreCliente.getText().toString().isEmpty()) {
            falta += "\n- nombre de cliente";
            makeToastAtEnd = true;
        }

        //obligatorio
        if (etAdultos.getText().toString().isEmpty()) {
            //Toast.makeText(getContext(),"Falta al menos 1 adulto",Toast.LENGTH_SHORT).show();
            //return false;
            falta += "\n- tiene 0 clientes adultos";
        }

        //obligatorio
        if (tvFechaEjecucion.getText().toString().equals("Fecha")) {
            Toast.makeText(getContext(), "Falta la fecha de la excursión", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (actvAgencia.getText().toString().isEmpty()) {
            falta += "\n- agencia";
            makeToastAtEnd = true;
        }

        //obligatorio
        if (actvHotel.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Falta el hotel", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etNoHab.getText().toString().isEmpty()) {
            falta += "\n- número de habitación";
            makeToastAtEnd = true;
        }
        if (etPrecio.getText().toString().isEmpty()) {
            falta += "\n- precio";
            makeToastAtEnd = true;
        }
        if (actvIdioma.getText().toString().isEmpty()) {
            if (getExcursionIfMatch() != null) {
                if (getExcursionIfMatch().getIdiomaNecesario() == Excursion.IDIOMA_NECESARIO) {
                    Toast.makeText(getContext(), "Falta el idioma", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        if (makeToastAtEnd) {
            Toast.makeText(getContext(), falta, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void setActivo() {
        setEstado(Reserva.ESTADO_ACTIVO);
        showEstado();
    }

    private void setCancelado() {
        if(myCallBack.getEstadoFragmentReservar()== MisConstantes.Estado.EDITAR) {
            setEstado(Reserva.ESTADO_CANCELADO);
            showEstado();
        }else if(myCallBack.getEstadoFragmentReservar()== MisConstantes.Estado.NUEVO){
            Reserva reserva = getNuevaReserva();
            if(reserva.getNoTE().isEmpty()){
                Toast.makeText(getContext(),"Debe tener al menos un numero de ticket",Toast.LENGTH_SHORT).show();
                return;
            }
            reserva.setFechaReporteVenta("");
            reserva.setEstado(Reserva.ESTADO_CANCELADO);
            reserva.setFechaCancelacion(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
            String msgHistorial = reserva.getFechaCancelacion()+" CANCELADO";
            reserva.addToHistorial(msgHistorial);
            registrar(reserva);
        }
    }

    private void setEstado(int estado) {
        Reserva reserva = ReservaBDHandler.getReservaFromDB(getContext(),idSelectedReserva);
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ReservaBDHandler.CAMPO_ESTADO, estado);
        String msgToast = "";
        String msgHistorial = DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR);
        switch (estado) {
            case Reserva.ESTADO_ACTIVO:
                msgToast = "Activada";
                msgHistorial += " ACTIVADO";
                break;
            case Reserva.ESTADO_CANCELADO:
                msgToast = "Cancelado";
                msgHistorial += " CANCELADO";
                values.put(ReservaBDHandler.CAMPO_FECHA_CANCELACION,DateHandler.getToday(MisConstantes.FormatoFecha.ALMACENAR));
                break;
        }
        reserva.addToHistorial(msgHistorial);
        values.put(ReservaBDHandler.CAMPO_HISTORIAL,reserva.getHistorial());
        db.update(ReservaBDHandler.TABLE_NAME, values, ReservaBDHandler.CAMPO_NUMERO_TE+"=?", new String[]{idSelectedReserva});
        Toast.makeText(getContext(), msgToast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void udInfoEstado() {
        showEstado();
    }

    private void confirmarEliminar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Seguro que desea eliminar esta reserva?");
        builder.setPositiveButton("Eliminar", (dialogInterface, i) -> eliminarReserva());
        builder.setNegativeButton("Cancelar", (dialogInterface, i) -> {});
        AlertDialog confirmationDialog = builder.create();
        confirmationDialog.show();
    }

    private void eliminarReserva() {
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.delete(ReservaBDHandler.TABLE_NAME, ReservaBDHandler.CAMPO_NUMERO_TE+"=?", new String[]{idSelectedReserva});
        Toast.makeText(getContext(), "Eliminado correctamente", Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    private void showDialogDevolver(){
        DialogFragmentDevolver dialog = new DialogFragmentDevolver();
        dialog.setTargetFragment(this,0);
        //dialog.show(getChildFragmentManager(),DialogFragmentDevolver.TAG);
        dialog.show(getFragmentManager(),DialogFragmentDevolver.TAG);
    }

    private void showHistorial(){
        DialogFragmentHistorial dialog = new DialogFragmentHistorial();
        dialog.setTargetFragment(this,0);
        //dialog.show(getChildFragmentManager(),DialogFragmentHistorial.TAG);
        dialog.show(getFragmentManager(),DialogFragmentHistorial.TAG);
    }

    @Override
    public String getIdReserva() {
        return idSelectedReserva;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            menu.clear();
        }
        if (myCallBack.getEstadoFragmentReservar() == MisConstantes.Estado.EDITAR) {
            switch (ReservaBDHandler.getReservaFromDB(getContext(), idSelectedReserva).getEstado()) {
                case Reserva.ESTADO_ACTIVO:
                    inflater.inflate(R.menu.menu_frag_reservar_activo, menu);
                    break;
                case Reserva.ESTADO_CANCELADO:
                    inflater.inflate(R.menu.menu_frag_reservar_cancelado, menu);
                    break;
                case Reserva.ESTADO_DEVUELTO:
                    inflater.inflate(R.menu.menu_frag_reservar_devuelto, menu);
                    break;
            }
        } else if (myCallBack.getEstadoFragmentReservar() == MisConstantes.Estado.NUEVO) {
            inflater.inflate(R.menu.menu_frag_reservar_nuevo, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_item_set_activo) {
            setActivo();
        } else if (itemId == R.id.menu_item_set_cancelado) {
            setCancelado();
        } else if (itemId == R.id.menu_item_set_devolver) {
            showDialogDevolver();
        } else if (itemId == R.id.menu_item_eliminar_reserva) {
            confirmarEliminar();
        } else if (itemId == R.id.menu_item_historial_reserva) {
            showHistorial();
        }
        return super.onOptionsItemSelected(item);
    }

    public interface MyCallBack {
        MisConstantes.Estado getEstadoFragmentReservar();
        void udUI(String tag);
    }

}
