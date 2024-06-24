package com.example.usuario.aavv.Excursiones;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.usuario.aavv.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by usuario on 22/07/2023.
 */

public class FragmentExcursiones extends Fragment implements ExcursionRVAdapter.MyCallBack{

    public static final String TAG = "FragmentExcursiones";

    private List<Excursion> excursionesList;
    private MyCallBack callBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_excursiones,container,false);
        bindComponents(v);
        callBack.udUI(FragmentExcursiones.TAG);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        callBack = (MyCallBack)context;
        super.onAttach(context);
    }

    private void bindComponents(View v){
        FloatingActionButton btnAddExcursion = v.findViewById(R.id.btn_add_excursion);
        RecyclerView rvExcursiones = v.findViewById(R.id.rv_excursiones);
        udExcursionesList();
        ExcursionRVAdapter adapter = new ExcursionRVAdapter(excursionesList,this);
        rvExcursiones.setAdapter(adapter);
        rvExcursiones.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExcursiones.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    btnAddExcursion.hide();
                }else if(dy<0){
                    btnAddExcursion.show();
                }
            }
        });
        btnAddExcursion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                callBack.setUpNewFragmentExcursion();
            }
        });
    }

    void udExcursionesList(){
        if(excursionesList != null){excursionesList.clear();}
        excursionesList = ExcursionBDHandler.getAllExcursionesfromDB(getContext());
        Collections.sort(excursionesList,Excursion.nameAscending);
    }

    @Override
    public void itemClicked(int position) {
        callBack.setUpFragmentExcursion(excursionesList.get(position).getId());
    }

    public interface MyCallBack{
        void udUI(String tag);
        void setUpNewFragmentExcursion();
        void setUpFragmentExcursion(long id);
    }
}
