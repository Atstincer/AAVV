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

        private TextView tvNombre, tvPrecioRango, tvAdultos, tvMenores, tvAcomp, tvIdioma;
        private MyCallBack callBack;

        public ViewHolder(View itemView, MyCallBack callBack) {
            super(itemView);
            this.callBack = callBack;
            tvNombre = (TextView)itemView.findViewById(R.id.tv_nombre_exc_cv);
            tvPrecioRango = (TextView)itemView.findViewById(R.id.tv_precio_rango_exc_cv);
            tvAdultos = (TextView)itemView.findViewById(R.id.tv_adultos_exc_cv);
            tvMenores = (TextView)itemView.findViewById(R.id.tv_menores_exc_cv);
            tvAcomp = (TextView)itemView.findViewById(R.id.tv_acomp_exc_cv);
            tvIdioma = (TextView)itemView.findViewById(R.id.tv_idioma_necesario_exc_cv);
            itemView.setOnClickListener(this);
        }

        void bindHolder(int posicion){
            tvNombre.setText(excursionesList.get(posicion).getNombre());
            String adultos = "";
            if(excursionesList.get(posicion).getTipoPrecio()==Excursion.PRECIO_X_PAX){
                tvPrecioRango.setVisibility(View.GONE);
                adultos = "ad: ";
            }else if(excursionesList.get(posicion).getTipoPrecio()==Excursion.PRECIO_X_RANGO){
                tvPrecioRango.setVisibility(View.VISIBLE);
                String precioRango = "Desde 1 hasta " + excursionesList.get(posicion).getRangoHasta() + " pax: "
                        + excursionesList.get(posicion).getPrecioRango();
                tvPrecioRango.setText(precioRango);
                adultos = "pax adicional: ";
            }
//            adultos += excursionesList.get(posicion).getPrecioAd();
//            tvAdultos.setText(adultos);

            showIfExist(tvAdultos,adultos,excursionesList.get(posicion).getPrecioAd());
            showIfExist(tvMenores,"men: ",excursionesList.get(posicion).getPrecioMenor());
            showIfExist(tvAcomp,"acomp: ",excursionesList.get(posicion).getPrecioAcomp());
            if(excursionesList.get(posicion).getIdiomaNecesario() == Excursion.IDIOMA_NECESARIO){
                tvIdioma.setVisibility(View.VISIBLE);
                tvIdioma.setText("idioma necesario");
            }else {
                tvIdioma.setVisibility(View.GONE);
            }
        }

        private void showIfExist(TextView tv, String encabezado, Float valor){
            if(valor == 0){
                tv.setVisibility(View.GONE);
            }else {
                tv.setVisibility(View.VISIBLE);
                String msg = encabezado + valor;
                tv.setText(msg);
            }
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
