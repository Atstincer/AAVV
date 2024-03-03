package com.example.usuario.aavv.Reservas;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.Util;

import java.security.PrivilegedAction;
import java.util.List;

/**
 * Created by usuario on 30/07/2023.
 */

class ReservaRVAdapter extends RecyclerView.Adapter<ReservaRVAdapter.ViewHolder> {

    private List<Reserva> reservaList;
    private MyCallBack myCallBack;
    private Context context;
    private Modo modo;

    ReservaRVAdapter(Context ctx,List<Reserva> reservaList, Modo modo,MyCallBack myCallBack) {
        context = ctx;
        this.reservaList = reservaList;
        this.modo = modo;
        this.myCallBack = myCallBack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_reserva,parent,false);
        return new ViewHolder(context,v,modo,myCallBack);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindHolder(position);
    }

    @Override
    public int getItemCount() {
        return reservaList.size();
    }

    void setReservaList(List<Reserva> lista){
        reservaList = lista;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTE, tvFechaEjecucion, tvExcursion, tvHotel, tvHab, tvCantPax, tvObs, tvIdioma, tvPrecio, tvEstado;
        private Modo modo;

        private Context ctx;

        ViewHolder(final Context ctx, View itemView, Modo modo, final MyCallBack myCallBack) {
            super(itemView);
            this.ctx = ctx;
            this.modo = modo;
            tvTE = (TextView)itemView.findViewById(R.id.tv_te_cv);
            tvEstado = (TextView)itemView.findViewById(R.id.tv_estado);
            tvFechaEjecucion = (TextView)itemView.findViewById(R.id.tv_fecha_ejecucion_cv);
            tvExcursion = (TextView)itemView.findViewById(R.id.tv_nombre_excursion_cv);
            tvHotel = (TextView)itemView.findViewById(R.id.tv_hotel_cv);
            tvHab = (TextView)itemView.findViewById(R.id.tv_hab_cv);
            tvCantPax = (TextView)itemView.findViewById(R.id.tv_cant_pax_cv);
            tvObs = (TextView)itemView.findViewById(R.id.tv_obs_cv);
            tvIdioma = (TextView)itemView.findViewById(R.id.tv_idioma_cv);
            tvPrecio = (TextView)itemView.findViewById(R.id.tv_precio_cv);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    myCallBack.itemClicked(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Util.copyToClipBoard(ctx,Reserva.toString(reservaList.get(getAdapterPosition()),Reserva.INFO_REPORTE_VENTA));
                    return true;
                }
            });
        }

        void bindHolder(int position){
            Reserva reserva = reservaList.get(position);
            String te = "TE "+ reserva.getNoTE();
            tvTE.setText(te);
            if(reserva.getEstado()!=Reserva.ESTADO_ACTIVO){
                if(reserva.getEstado()==Reserva.ESTADO_CANCELADO){
                    tvEstado.setText("CANCELADO");
                }else if(reserva.getEstado()==Reserva.ESTADO_DEVUELTO){
                    tvEstado.setText("DEVUELTO");
                }
            }else {
                tvEstado.setText("");
            }
            if(reserva.getFechaOriginalEjecucion()==null || reserva.getFechaOriginalEjecucion().equals("")) {
                tvFechaEjecucion.setText(reserva.getFechaEjecucion());
            }else {
                if(modo == Modo.POR_AGENCIA) {
                    tvFechaEjecucion.setText(reserva.getFechaOriginalEjecucion());
                }else {
                    tvFechaEjecucion.setText(reserva.getFechaEjecucion());
                }
            }
            showInfoIfExist(tvExcursion,reserva.getExcursion());
            showInfoIfExist(tvHotel,reserva.getHotel());
            showInfoIfExist(tvHab,reserva.getNoHab());
            String cantPax = "";
            if(!reserva.getCantPaxs(false).equals("")){
                cantPax = "pax: " + reserva.getCantPaxs(false);
            }
            showInfoIfExist(tvCantPax,cantPax);
            showInfoIfExist(tvIdioma,reserva.getIdioma());
            tvObs.setText(reserva.getObservaciones());
            tvPrecio.setText(String.valueOf(reserva.getPrecio()));
            tvPrecio.setTextColor(ContextCompat.getColor(ctx,android.R.color.holo_green_dark));
            if(reserva.getPrecio()==0){
                tvPrecio.setTextColor(ContextCompat.getColor(ctx,R.color.atencion));
            }

            if(this.modo == Modo.GENERAL){
                //oculta precio, si observaciones si dice algo
                tvPrecio.setVisibility(View.GONE);
                showObsIfExist();
            }else if(this.modo == Modo.EXC_SALIENDO_EL_DIA){
                tvFechaEjecucion.setVisibility(View.GONE);
                tvPrecio.setVisibility(View.GONE);
                showObsIfExist();
            }else if(this.modo == Modo.POR_AGENCIA){
                tvObs.setText("");
                if(reserva.getEstado()==Reserva.ESTADO_DEVUELTO){
                    double saldo = reserva.getPrecio()-reserva.getImporteDevuelto();
                    tvPrecio.setText(String.valueOf(saldo));
                }
            }else if(this.modo == Modo.LIQUIDACION) {
                if(reserva.getEstado()==Reserva.ESTADO_DEVUELTO){
                    if(myCallBack.getFechaLiquidacion() != null
                            && myCallBack.getFechaLiquidacion().equals(reserva.getFechaDevolucion())) {
                        tvPrecio.setTextColor(ContextCompat.getColor(ctx, R.color.atencion));
                        String importe = "-" + String.valueOf(reserva.getImporteDevuelto());
                        tvPrecio.setText(importe);
                    }
                }
            }
        }

        private void showInfoIfExist(TextView tv,String info){
            if(info.equals("")){
                tv.setVisibility(View.GONE);
            }else {
                tv.setVisibility(View.VISIBLE);
                tv.setText(info);
            }
        }

        private void showObsIfExist(){
            if(tvObs.getText().toString().equals("")){
                tvObs.setVisibility(View.GONE);
            }
        }
    }

    interface MyCallBack{
        void itemClicked(int position);
        String getFechaLiquidacion();
    }

    enum Modo{
        GENERAL,
        LIQUIDACION,
        POR_AGENCIA,
        EXC_SALIENDO_EL_DIA
    }
}
