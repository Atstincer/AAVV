package com.example.usuario.aavv.Excursiones;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.usuario.aavv.R;

import java.util.List;

/**
 * Created by usuario on 1/10/2023.
 */

public class ExcursionRVAdapter extends RecyclerView.Adapter<ExcursionRVAdapter.ViewHolder> {

    private List<Excursion> excursionesList;
    private MyCallBack callBack;

    public ExcursionRVAdapter(List<Excursion> excursionesList, MyCallBack callBack) {
        this.excursionesList = excursionesList;
        this.callBack = callBack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_excursion,parent,false);
        return new ViewHolder(v,callBack);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindHolder(position);
    }

    @Override
    public int getItemCount() {
        return excursionesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvNombre;
        private MyCallBack callBack;

        public ViewHolder(View itemView, MyCallBack callBack) {
            super(itemView);
            this.callBack = callBack;
            tvNombre = (TextView)itemView.findViewById(R.id.tv_nombre_excursion_cv);
            itemView.setOnClickListener(this);
        }

        void bindHolder(int posicion){
            tvNombre.setText(excursionesList.get(posicion).getNombre());
        }

        @Override
        public void onClick(View view) {
            callBack.itemClicked(getAdapterPosition());
        }
    }

    interface MyCallBack{
        void itemClicked(int position);
    }
}
