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

}
