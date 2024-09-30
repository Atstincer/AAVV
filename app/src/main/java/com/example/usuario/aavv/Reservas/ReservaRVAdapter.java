package com.example.usuario.aavv.Reservas;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.Excursiones.Excursion;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.Util;

import java.security.PrivilegedAction;
import java.util.List;

/**
 * Created by usuario on 30/07/2023.
 */

public class ReservaRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int RESERVA_VIEW = 0, EXCURSION_VIEW = 1;

    private List<Object> reservaList;
    private final MyCallBack myCallBack;
    private final Context context;
    private final Modo modo;

    ReservaRVAdapter(Context ctx, List<Object> reservaList, Modo modo, MyCallBack myCallBack) {
        context = ctx;
        this.reservaList = reservaList;
        this.modo = modo;
        this.myCallBack = myCallBack;
    }

    @Override
    public int getItemViewType(int position) {
        if (reservaList.get(position) instanceof Excursion) {
            return EXCURSION_VIEW;
        }
        return RESERVA_VIEW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == EXCURSION_VIEW) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_titulo_excursion, parent, false);
            return new ViewHolderExcursion(v);
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_reserva, parent, false);
        return new ViewHolderReserva(context, v, modo, myCallBack);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == EXCURSION_VIEW) {
            ((ViewHolderExcursion) holder).bindHolder(position);
        } else if (getItemViewType(position) == RESERVA_VIEW) {
            ((ViewHolderReserva) holder).bindHolder(position);
        }
    }


    @Override
    public int getItemCount() {
        return reservaList.size();
    }

    void setReservaList(List<Object> lista) {
        reservaList = lista;
        notifyDataSetChanged();
    }

    private class ViewHolderReserva extends RecyclerView.ViewHolder {

        private final TextView tvTE, tvFechaEjecucion, tvExcursion, tvHotel, tvHab, tvCantPax, tvObs,
                tvIdioma, tvPrecio, tvEstado, tvPrecioCUP;
        private final Modo modo;
        private final Context ctx;

        ViewHolderReserva(final Context ctx, View itemView, Modo modo, final MyCallBack myCallBack) {
            super(itemView);
            this.ctx = ctx;
            this.modo = modo;
            tvTE = itemView.findViewById(R.id.tv_te_cv);
            tvEstado = itemView.findViewById(R.id.tv_estado);
            tvFechaEjecucion = itemView.findViewById(R.id.tv_fecha_ejecucion_cv);
            tvExcursion = itemView.findViewById(R.id.tv_nombre_excursion_cv);
            tvHotel = itemView.findViewById(R.id.tv_hotel_cv);
            tvHab = itemView.findViewById(R.id.tv_hab_cv);
            tvCantPax = itemView.findViewById(R.id.tv_cant_pax_cv);
            tvObs = itemView.findViewById(R.id.tv_obs_cv);
            tvIdioma = itemView.findViewById(R.id.tv_idioma_cv);
            tvPrecio = itemView.findViewById(R.id.tv_precio_cv);
            tvPrecioCUP = itemView.findViewById(R.id.tv_precio_cup_cv);

            itemView.setOnClickListener(view -> myCallBack.itemClicked(getAdapterPosition()));

            itemView.setOnLongClickListener(view -> {
                Util.copyToClipBoard(ctx, Reserva.toString(ctx, (Reserva) reservaList.get(getAdapterPosition()), Reserva.INFO_REPORTE_VENTA));
                return true;
            });
        }

        void bindHolder(int position) {
            Reserva reserva = (Reserva) reservaList.get(position);
            String te = "TE " + reserva.getNoTE();
            tvTE.setText(te);
            switch (reserva.getEstado()) {
                case Reserva.ESTADO_ACTIVO:
                    tvEstado.setText("");
                    break;
                case Reserva.ESTADO_DEVUELTO:
                    tvEstado.setText("DEVUELTO");
                    break;
                case Reserva.ESTADO_CANCELADO:
                    tvEstado.setText("CANCELADO");
                    break;
            }
            if (reserva.getFechaOriginalEjecucion() == null || reserva.getFechaOriginalEjecucion().isEmpty()) {
                tvFechaEjecucion.setText(reserva.getFechaEjecucion());
            } else {
                if (modo == Modo.POR_AGENCIA) {
                    tvFechaEjecucion.setText(reserva.getFechaOriginalEjecucion());
                } else {
                    tvFechaEjecucion.setText(reserva.getFechaEjecucion());
                }
            }
            showInfoIfExist(tvExcursion, reserva.getExcursion());
            showInfoIfExist(tvHotel, reserva.getHotel());
            showInfoIfExist(tvHab, reserva.getNoHab());
            String cantPax = "";
            if (!reserva.getCantPaxs(false).isEmpty()) {
                cantPax = "pax: " + reserva.getCantPaxs(false);
            }
            showInfoIfExist(tvCantPax, cantPax);
            showInfoIfExist(tvIdioma, reserva.getIdioma());
            tvObs.setText(reserva.getObservaciones());
            tvPrecio.setText(String.valueOf(reserva.getPrecio()));
            tvPrecio.setTextColor(ContextCompat.getColor(ctx, android.R.color.holo_green_dark));
            if (reserva.getPrecio() == 0) {
                tvPrecio.setTextColor(ContextCompat.getColor(ctx, R.color.atencion));
            }
            tvPrecioCUP.setVisibility(View.GONE);

            switch (this.modo){
                case GENERAL:
                    //oculta precio, si observaciones si dice algo
                    tvPrecio.setVisibility(View.GONE);
                    showObsIfExist();
                    break;
                case EXC_SALIENDO_EL_DIA:
                    tvFechaEjecucion.setVisibility(View.GONE);
                    tvPrecio.setVisibility(View.GONE);
                    showObsIfExist();
                    break;
                case POR_AGENCIA:
                    tvObs.setText("");
                    if (reserva.getEstado() == Reserva.ESTADO_DEVUELTO) {
                        double saldo = reserva.getPrecio() - reserva.getImporteDevuelto();
                        tvPrecio.setText(String.valueOf(saldo));
                    }
                    break;
                case LIQUIDACION:
                    if (MySharedPreferences.getIncluirPrecioCUP(ctx) && MySharedPreferences.getTasaCUP(ctx) > 0) {
                        tvPrecioCUP.setVisibility(View.VISIBLE);
                        String precioCUP = "(" + (reserva.getPrecio() * MySharedPreferences.getTasaCUP(ctx)) + ")";
                        tvPrecioCUP.setText(precioCUP);
                    }
                    if (reserva.getEstado() == Reserva.ESTADO_DEVUELTO) {
                        if(reserva.getCriterioSeleccion() == Reserva.Criterio_Seleccion.FECHA_DEVOLUCION){
                            tvPrecio.setTextColor(ContextCompat.getColor(ctx, R.color.atencion));
                            String importe = "-" + reserva.getImporteDevuelto();
                            tvPrecio.setText(importe);
                            if (MySharedPreferences.getIncluirPrecioCUP(ctx) &&
                                    MySharedPreferences.getTasaCUP(ctx) > 0) {
                                String importeCUP = "(-" + (reserva.getImporteDevuelto() * MySharedPreferences.getTasaCUP(ctx)) + ")";
                                tvPrecioCUP.setText(importeCUP);
                            }
                        } else if(reserva.getCriterioSeleccion() == Reserva.Criterio_Seleccion.FECHA_CONFECCION){
                            tvEstado.setVisibility(View.GONE);
                        }
                    }
                    break;
                case REP_VENTA:
                    tvPrecio.setVisibility(View.GONE);
                    showObsIfExist();
                    break;
            }

        }

        private void showInfoIfExist(TextView tv, String info) {
            if (info == null || info.isEmpty()) {
                tv.setVisibility(View.GONE);
            } else {
                tv.setVisibility(View.VISIBLE);
                tv.setText(info);
            }
        }

        private void showObsIfExist() {
            if (tvObs.getText().toString().equals("")) {
                tvObs.setVisibility(View.GONE);
            } else {
                tvObs.setVisibility(View.VISIBLE);
            }
        }
    }

    private class ViewHolderExcursion extends RecyclerView.ViewHolder {

        private TextView tvNombre;

        ViewHolderExcursion(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_titulo_excursion);
        }

        void bindHolder(int position) {
            Excursion excursion = (Excursion) reservaList.get(position);
            tvNombre.setText(excursion.getNombre());
        }
    }

    interface MyCallBack {
        void itemClicked(int position);
    }

    enum Modo {
        GENERAL,
        LIQUIDACION,
        POR_AGENCIA,
        EXC_SALIENDO_EL_DIA,
        REP_VENTA
    }
}
