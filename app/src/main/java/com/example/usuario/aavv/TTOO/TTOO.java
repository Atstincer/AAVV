package com.example.usuario.aavv.TTOO;

import java.util.Comparator;

/**
 * Created by usuario on 18/07/2023.
 */

public class TTOO {

    private long id;
    private String nombre;

    public TTOO(){}

    TTOO(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getNombre();
    }

    static Comparator<TTOO> nameAscending = new Comparator<TTOO>() {
        @Override
        public int compare(TTOO ttoo1, TTOO ttoo2) {
            return ttoo1.getNombre().toLowerCase().compareTo(ttoo2.getNombre().toLowerCase());
        }
    };
}
