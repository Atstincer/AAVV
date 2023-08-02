package com.example.usuario.aavv.TTOO;

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
import java.util.Comparator;
import java.util.List;

/**
 * Created by usuario on 22/07/2023.
 */

public class FragmentTouroperadores extends Fragment implements DialogFragmentInfoTTOO.MyCallBack, TTOORVAdapter.MyCallBack{

    public static final String TAG = "FragmentTouroperadores";

    //private FloatingActionButton btnAddTTOO;
    //private RecyclerView rvTTOOs;

    private TTOORVAdapter adapter;
    private List<TTOO> ttooList;
    private long idTTOOSelected;

    private MyCallBack callBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_touroperadores,container,false);
        idTTOOSelected = 0;
        bindComponents(v);
        callBack.udUI(FragmentTouroperadores.TAG);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        callBack = (MyCallBack)context;
        super.onAttach(context);
    }

    private void bindComponents(View v){
        FloatingActionButton btnAddTTOO = (FloatingActionButton)v.findViewById(R.id.btn_add_ttoo);
        RecyclerView rvTTOOs = (RecyclerView)v.findViewById(R.id.rv_touroperadores);
        udTTOOList();
        adapter = new TTOORVAdapter(ttooList,this);
        rvTTOOs.setAdapter(adapter);
        rvTTOOs.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAddTTOO.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                idTTOOSelected = 0;
                showTTOODialog();
            }
        });
    }

    void udTTOOList(){
        if(ttooList != null){ttooList.clear();}
        ttooList = TTOOBDHandler.getAllTTOOfromDB(getContext());
        Collections.sort(ttooList,TTOO.nameAscending);
    }

    private void showTTOODialog(){
        DialogFragmentInfoTTOO dialog = new DialogFragmentInfoTTOO();
        dialog.setTargetFragment(this,0);
        dialog.show(getChildFragmentManager(),DialogFragmentInfoTTOO.TAG);
    }

    @Override
    public long getTTOOId() {
        return idTTOOSelected;
    }

    @Override
    public void infoChanged() {
        udTTOOList();
        adapter.setTtooList(ttooList);
    }

    @Override
    public void itemClick(int position) {
        //Toast.makeText(getContext(),"Click on item "+position,Toast.LENGTH_SHORT).show();
        idTTOOSelected = ttooList.get(position).getId();
        showTTOODialog();
    }

    public interface MyCallBack{
        void udUI(String tag);
    }
}
