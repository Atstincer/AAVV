package com.example.usuario.aavv.Excursiones;

import java.util.Comparator;

/**
 * Created by usuario on 30/09/2023.
 */

public class Excursion {

    public static final int PRECIO_X_PAX = 0;
    public static final int PRECIO_X_RANGO = 1;
    public static final int IDIOMA_NO_NECESARIO = 0;
    public static final int IDIOMA_NECESARIO = 1;

    private String nombre;
    private long id;
    private int tipoPrecio, rangoHasta, idiomaNecesario;
    private float precioAd, precioMenor, precioAcomp, precioRango;

    Excursion() {
    }

    public Excursion(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getPrecioAd() {
        return precioAd;
    }

    public void setPrecioAd(float precioAd) {
        this.precioAd = precioAd;
    }

    public float getPrecioMenor() {
        return precioMenor;
    }

    public void setPrecioMenor(float precioMenor) {
        this.precioMenor = precioMenor;
    }

    public float getPrecioAcomp() {
        return precioAcomp;
    }

    public void setPrecioAcomp(float precioAcomp) {
        this.precioAcomp = precioAcomp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTipoPrecio() {
        return tipoPrecio;
    }

    public void setTipoPrecio(int tipoPrecio) {
        this.tipoPrecio = tipoPrecio;
    }

    public int getRangoHasta() {
        return rangoHasta;
    }

    public void setRangoHasta(int rangoHasta) {
        this.rangoHasta = rangoHasta;
    }

    public float getPrecioRango() {
        return precioRango;
    }

    public void setPrecioRango(float precioRango) {
        this.precioRango = precioRango;
    }

    public int getIdiomaNecesario() {
        return idiomaNecesario;
    }

    public void setIdiomaNecesario(int idiomaNecesario) {
        this.idiomaNecesario = idiomaNecesario;
    }

    @Override
    public String toString() {
        return nombre;
    }

    static Comparator<Excursion> nameAscending = new Comparator<Excursion>() {
        @Override
        public int compare(Excursion excursion1, Excursion excursion2) {
            return excursion1.getNombre().toLowerCase().compareTo(excursion2.getNombre().toLowerCase());
        }
    };
}
