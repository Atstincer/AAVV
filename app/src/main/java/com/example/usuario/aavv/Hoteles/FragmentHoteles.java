package com.example.usuario.aavv.Hoteles;

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

public class FragmentHoteles extends Fragment implements DialogFragmentInfoHotel.MyCallBack, HotelesRVAdapter.MyCallBack{

    public static final String TAG = "FragmentHoteles";

    private HotelesRVAdapter adapter;
    private List<Hotel> hotelesList;
    private long idHotelSelected;

    private MyCallBack callBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hoteles,container,false);
        idHotelSelected = 0;
        bindComponents(v);
        callBack.udUI(FragmentHoteles.TAG);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        callBack = (MyCallBack)context;
        super.onAttach(context);
    }

    private void bindComponents(View v){
        FloatingActionButton btnAddHotel = v.findViewById(R.id.btn_add_hotel);
        RecyclerView rvHoteles = v.findViewById(R.id.rv_hoteles);
        udHotelesList();
        adapter = new HotelesRVAdapter(hotelesList,this);
        rvHoteles.setAdapter(adapter);
        rvHoteles.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHoteles.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    btnAddHotel.hide();
                }else if(dy<0){
                    btnAddHotel.show();
                }
            }
        });
        btnAddHotel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                idHotelSelected = 0;
                showHotelDialog();
            }
        });
    }

    void udHotelesList(){
        if(hotelesList != null){hotelesList.clear();}
        hotelesList = HotelBDHandler.getAllHotelesfromDB(getContext());
        Collections.sort(hotelesList,Hotel.nameAscending);
    }

    private void showHotelDialog(){
        DialogFragmentInfoHotel dialog = new DialogFragmentInfoHotel();
        dialog.setTargetFragment(this,0);
        dialog.show(getChildFragmentManager(),DialogFragmentInfoHotel.TAG);
    }

    @Override
    public long getHotelId() {
        return idHotelSelected;
    }

    @Override
    public void infoChanged() {
        udHotelesList();
        adapter.setHotelesList(hotelesList);
    }

    @Override
    public void itemClick(int position) {
        //Toast.makeText(getContext(),"Click on item "+position,Toast.LENGTH_SHORT).show();
        idHotelSelected = hotelesList.get(position).getId();
        showHotelDialog();
    }

    public interface MyCallBack{
        void udUI(String tag);
    }
}
