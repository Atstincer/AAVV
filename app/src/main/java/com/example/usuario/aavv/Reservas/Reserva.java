package com.example.usuario.aavv.Reservas;

import java.util.Comparator;

/**
 * Created by usuario on 30/07/2023.
 */

public class Reserva {

    static final int ESTADO_ACTIVO = 0;
    static final int ESTADO_CANCELADO = 1;
    static final int ESTADO_DEVUELTO = 2;

    static final int INFO_REPORTE_VENTA = 3;
    static final int INFO_LIQUIDACION = 4;

    private long id;
    private String noTE, excursion, agencia, noHab, cliente, hotel, fechaConfeccion, fechaEjecucion, idioma, Observaciones;
    private int adultos, menores, infantes, acompanante, estado;
    private double precio;

    Reserva() {
    }

    public static String toString(Reserva reserva, int formato){
        String text = "TE: " + reserva.getNoTE() + "\n" +
                "Excursion: " + reserva.getExcursion() + "\n" +
                "Fecha: " + reserva.getFechaEjecucion() + "\n" +
                "Cantidad de pax: " + reserva.getCantPaxs(true) + "\n" +
                "Hotel: " + reserva.getHotel() + "\n" +
                "Habitación: " + reserva.getNoHab();
        if(formato == INFO_REPORTE_VENTA) {
            if (!reserva.getIdioma().equals("")) {
                text += "\nIdioma: " + reserva.getIdioma();
            }
            if (!reserva.getObservaciones().equals("")) {
                text += "\nObservaciones: " + reserva.getObservaciones();
            }
        } else if(formato == INFO_LIQUIDACION){
            text += "\nCliente: " + reserva.getCliente();
            text += "\nAgencia: " + reserva.getAgencia();
            text += "\nImporte: " + reserva.getPrecio() + " usd";
        }
        return text;
    }

    //no extended 2+2+1 free
    //extended 2 adultos + 2 menores + 1 menor free
    String getCantPaxs(boolean extendido){
        String cantPaxs = String.valueOf(getAdultos());
        if(extendido){
            if(getAdultos()==1){
                cantPaxs += " adulto";
            }else {
                cantPaxs += " adultos";
            }
        }
        if(getMenores()!=0){
            if(extendido){
                if(getMenores()==1){
                    cantPaxs += " + " + getMenores() + " menor";
                }else {
                    cantPaxs += " + " + getMenores() + " menores";
                }
            }else {
                cantPaxs += "+" + getMenores();
            }
        }
        if(getInfantes()!=0){
            if(extendido){
                if(getInfantes()==1){
                    cantPaxs += " + " + getInfantes() + " menor free";
                }else {
                    cantPaxs += " + " + getInfantes() + " menores free";
                }
            }else {
                cantPaxs += "+" + getInfantes() + " free";
            }
        }
        return cantPaxs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    void setNoTE(String noTE) {
        this.noTE = noTE;
    }

    void setExcursion(String excursion) {
        this.excursion = excursion;
    }

    void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    void setNoHab(String noHab) {
        this.noHab = noHab;
    }

    void setCliente(String cliente) {
        this.cliente = cliente;
    }

    void setHotel(String hotel) {
        this.hotel = hotel;
    }

    void setFechaConfeccion(String fechaConfeccion) {
        this.fechaConfeccion = fechaConfeccion;
    }

    void setFechaEjecucion(String fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    void setObservaciones(String observaciones) {
        Observaciones = observaciones;
    }

    void setAdultos(int adultos) {
        this.adultos = adultos;
    }

    void setMenores(int menores) {
        this.menores = menores;
    }

    void setInfantes(int infantes) {
        this.infantes = infantes;
    }

    void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getNoTE() {
        return noTE;
    }

    public String getExcursion() {
        return excursion;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getNoHab() {
        return noHab;
    }

    public String getCliente() {
        return cliente;
    }

    public String getHotel() {
        return hotel;
    }

    public String getFechaConfeccion() {
        return fechaConfeccion;
    }

    public String getFechaEjecucion() {
        return fechaEjecucion;
    }

    public String getIdioma() {
        return idioma;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public int getAdultos() {
        return adultos;
    }

    public int getMenores() {
        return menores;
    }

    public int getInfantes() {
        return infantes;
    }

    public double getPrecio() {
        return precio;
    }

    public int getAcompanantes() {
        return acompanante;
    }

    void setAcompanante(int acompanante) {
        this.acompanante = acompanante;
    }

    public int getEstado() {
        return estado;
    }

    void setEstado(int estado) {
        this.estado = estado;
    }

    static Comparator<Reserva> ordenarPorTE = new Comparator<Reserva>() {
        @Override
        public int compare(Reserva reserva1, Reserva reserva2) {
            return reserva1.getNoTE().toLowerCase().compareTo(reserva2.getNoTE().toLowerCase());
        }
    };

    static Comparator<Reserva> ordenarPorExc = new Comparator<Reserva>() {
        @Override
        public int compare(Reserva reserva1, Reserva reserva2) {
            return reserva1.getExcursion().toLowerCase().compareTo(reserva2.getExcursion().toLowerCase());
        }
    };

    static Comparator<Reserva> ordenarPorHotel = new Comparator<Reserva>() {
        @Override
        public int compare(Reserva reserva1, Reserva reserva2) {
            return reserva1.getHotel().toLowerCase().compareTo(reserva2.getHotel().toLowerCase());
        }
    };

}
