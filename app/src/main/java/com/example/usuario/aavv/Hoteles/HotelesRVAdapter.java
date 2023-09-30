package com.example.usuario.aavv.Hoteles;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.usuario.aavv.R;
import com.example.usuario.aavv.TTOO.TTOO;

import java.util.List;

/**
 * Created by usuario on 22/07/2023.
 */

class HotelesRVAdapter extends RecyclerView.Adapter<HotelesRVAdapter.ViewHolder> {

    private List<Hotel> hotelesList;
    private MyCallBack callBack;

    HotelesRVAdapter(List<Hotel> hotelesList, MyCallBack callBack){
        this.hotelesList = hotelesList;
        this.callBack = callBack;
    }

    void setHotelesList(List<Hotel> hotelesList){
        this.hotelesList = hotelesList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_hotel,parent,false);
        return new ViewHolder(view,callBack);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindHolder(position);
    }

    @Override
    public int getItemCount() {
        return hotelesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvNombreHotel;
        private MyCallBack callBack;

        private ViewHolder(View itemView, MyCallBack callBack) {
            super(itemView);
            tvNombreHotel = (TextView)itemView.findViewById(R.id.tv_nombre_hotel);
            this.callBack = callBack;
            itemView.setOnClickListener(this);
        }

        private void bindHolder(int position){
            tvNombreHotel.setText(hotelesList.get(position).getNombre());
        }

        @Override
        public void onClick(View view) {
            callBack.itemClick(getAdapterPosition());
        }
    }

    interface MyCallBack{
        void itemClick(int position);
    }
}
