package com.example.usuario.aavv.Reservas;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.usuario.aavv.R;

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

        private TextView tvTE, tvFechaEjecucion, tvExcursion, tvHotel, tvHab, tvCantPax, tvObs, tvIdioma, tvPrecio;
        private Modo modo;

        private Context ctx;

        ViewHolder(Context ctx,View itemView, Modo modo, final MyCallBack myCallBack) {
            super(itemView);
            this.ctx = ctx;
            this.modo = modo;
            tvTE = (TextView)itemView.findViewById(R.id.tv_te_cv);
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
        }

        void bindHolder(int position){
            String te = "TE "+ reservaList.get(position).getNoTE();
            tvTE.setText(te);
            tvFechaEjecucion.setText(reservaList.get(position).getFechaEjecucion());
            tvExcursion.setText(reservaList.get(position).getExcursion());
            tvHotel.setText(reservaList.get(position).getHotel());
            tvHab.setText(reservaList.get(position).getNoHab());
            String cantPax = "pax: " + reservaList.get(position).getCantPaxs(false);
            tvCantPax.setText(cantPax);
            tvIdioma.setText(reservaList.get(position).getIdioma());
            //tvCantPax.setText(getCantPaxs(position));
            tvObs.setText(reservaList.get(position).getObservaciones());
            tvPrecio.setText(String.valueOf(reservaList.get(position).getPrecio()));
            if(reservaList.get(position).getPrecio()==0){
                tvPrecio.setTextColor(ContextCompat.getColor(ctx,R.color.atencion));
            }

            if(modo == Modo.GENERAL){
                //oculta precio, si observaciones si dice algo
                tvPrecio.setVisibility(View.GONE);
                showObsIfExist();
            }else if(modo == Modo.EXC_SALIENDO_EL_DIA){
                tvFechaEjecucion.setVisibility(View.GONE);
                tvPrecio.setVisibility(View.GONE);
                showObsIfExist();
            }else if(modo == Modo.POR_AGENCIA){
                tvObs.setText("");
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
    }

    enum Modo{
        GENERAL,
        LIQUIDACION,
        POR_AGENCIA,
        EXC_SALIENDO_EL_DIA
    }
}
