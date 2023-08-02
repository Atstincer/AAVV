package com.example.usuario.aavv.Reservas;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.usuario.aavv.R;

import java.util.List;

/**
 * Created by usuario on 30/07/2023.
 */

class ReservaRVAdapter extends RecyclerView.Adapter<ReservaRVAdapter.ViewHolder> {

    private List<Reserva> reservaList;
    private MyCallBack myCallBack;

    ReservaRVAdapter(List<Reserva> reservaList, MyCallBack myCallBack) {
        this.reservaList = reservaList;
        this.myCallBack = myCallBack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_reserva,parent,false);
        return new ViewHolder(v,myCallBack);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindHolder(position);
    }

    @Override
    public int getItemCount() {
        return reservaList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTE, tvFechaEjecucion, tvExcursion, tvHotel, tvHab, tvCantPax, tvObs;

        ViewHolder(View itemView, final MyCallBack myCallBack) {
            super(itemView);
            tvTE = (TextView)itemView.findViewById(R.id.tv_te_cv);
            tvFechaEjecucion = (TextView)itemView.findViewById(R.id.tv_fecha_ejecucion_cv);
            tvExcursion = (TextView)itemView.findViewById(R.id.tv_nombre_excursion_cv);
            tvHotel = (TextView)itemView.findViewById(R.id.tv_hotel_cv);
            tvHab = (TextView)itemView.findViewById(R.id.tv_hab_cv);
            tvCantPax = (TextView)itemView.findViewById(R.id.tv_cant_pax_cv);
            tvObs = (TextView)itemView.findViewById(R.id.tv_obs_cv);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    myCallBack.itemClicked(getAdapterPosition());
                }
            });
        }

        void bindHolder(int position){
            String te = "TE "+reservaList.get(position).getNoTE();
            tvTE.setText(te);
            tvFechaEjecucion.setText(reservaList.get(position).getFechaEjecucion());
            tvExcursion.setText(reservaList.get(position).getExcursion());
            tvHotel.setText(reservaList.get(position).getHotel());
            tvHab.setText(reservaList.get(position).getNoHab());
            tvCantPax.setText(getCantPaxs(position));
            if(reservaList.get(position).getObservaciones().equals("")){
                tvObs.setVisibility(View.GONE);
            }else {
                tvObs.setVisibility(View.VISIBLE);
                tvObs.setText(reservaList.get(position).getObservaciones());
            }
        }

        private String getCantPaxs(int position){
            String cantPaxs = "pax: "+reservaList.get(position).getAdultos();
            if(reservaList.get(position).getMenores()!=0){
                cantPaxs += "+" + reservaList.get(position).getMenores();
            }
            if(reservaList.get(position).getInfantes()!=0){
                cantPaxs += "+" + reservaList.get(position).getInfantes() + " free";
            }
            return cantPaxs;
        }
    }

    interface MyCallBack{
        void itemClicked(int position);
    }
}
