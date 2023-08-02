package com.example.usuario.aavv.TTOO;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.usuario.aavv.R;

import java.util.List;

/**
 * Created by usuario on 22/07/2023.
 */

class TTOORVAdapter extends RecyclerView.Adapter<TTOORVAdapter.ViewHolder> {

    private List<TTOO> ttooList;
    private MyCallBack callBack;

    TTOORVAdapter(List<TTOO> ttooList, MyCallBack callBack){
        this.ttooList = ttooList;
        this.callBack = callBack;
    }

    void setTtooList(List<TTOO> ttooList){
        this.ttooList = ttooList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_ttoo,parent,false);
        return new ViewHolder(view,callBack);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindHolder(position);
    }

    @Override
    public int getItemCount() {
        return ttooList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvNombreTTOO;
        private MyCallBack callBack;

        private ViewHolder(View itemView, MyCallBack callBack) {
            super(itemView);
            tvNombreTTOO = (TextView)itemView.findViewById(R.id.tv_nombre_ttoo);
            this.callBack = callBack;
            itemView.setOnClickListener(this);
        }

        private void bindHolder(int position){
            tvNombreTTOO.setText(ttooList.get(position).getNombre());
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
