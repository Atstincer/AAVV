package com.example.usuario.aavv.Hoteles;

import java.util.Comparator;

/**
 * Created by usuario on 30/09/2023.
 */

public class Hotel {

    private String nombre;
    private long id;

    public Hotel(String nombre) {
        this.nombre = nombre;
    }

    public Hotel() {
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

    static Comparator<Hotel> nameAscending = new Comparator<Hotel>() {
        @Override
        public int compare(Hotel hotel1, Hotel hotel2) {
            return hotel1.getNombre().toLowerCase().compareTo(hotel2.getNombre().toLowerCase());
        }
    };
}
