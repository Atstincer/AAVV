package com.example.usuario.aavv.Reservas;

import java.util.Comparator;

/**
 * Created by usuario on 30/07/2023.
 */

class Reserva {

    private long id;
    private String noTE, excursion, agencia, noHab, cliente, hotel, fechaConfeccion, fechaEjecucion, idioma, Observaciones;
    private int adultos, menores, infantes;
    private double precio;

    Reserva() {
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

    String getNoTE() {
        return noTE;
    }

    String getExcursion() {
        return excursion;
    }

    String getAgencia() {
        return agencia;
    }

    String getNoHab() {
        return noHab;
    }

    String getCliente() {
        return cliente;
    }

    String getHotel() {
        return hotel;
    }

    String getFechaConfeccion() {
        return fechaConfeccion;
    }

    String getFechaEjecucion() {
        return fechaEjecucion;
    }

    String getIdioma() {
        return idioma;
    }

    String getObservaciones() {
        return Observaciones;
    }

    int getAdultos() {
        return adultos;
    }

    int getMenores() {
        return menores;
    }

    int getInfantes() {
        return infantes;
    }

    double getPrecio() {
        return precio;
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
