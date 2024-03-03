package com.example.usuario.aavv.Reservas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by usuario on 30/07/2023.
 */

public class Reserva {

    public static final int ESTADO_ACTIVO = 0;
    public static final int ESTADO_CANCELADO = 1;
    public static final int ESTADO_DEVUELTO = 2;

    static final int INFO_REPORTE_VENTA = 3;
    static final int INFO_LIQUIDACION = 4;

    private long id;
    private String noTE, excursion, agencia, noHab, cliente, hotel, fechaConfeccion, fechaEjecucion, fechaReporteVenta, fechaDevolucion,
            fechaOriginalEjecucion, idioma, Observaciones;
    private int adultos, menores, infantes, acompanante, estado;
    private double precio, importeDevuelto;

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
        String cantPaxs = "";
        if(getAdultos()>0) {
            cantPaxs += String.valueOf(getAdultos());
            if (extendido) {
                if (getAdultos() == 1) {
                    cantPaxs += " adulto";
                } else {
                    cantPaxs += " adultos";
                }
            }
        }
        if(getMenores()>0){
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
        if(getInfantes()>0){
            if(extendido){
                if(getInfantes()==1){
                    cantPaxs += " + " + getInfantes() + " menor free";
                }else {
                    cantPaxs += " + " + getInfantes() + " menores free";
                }
            }else {
                cantPaxs += "+" + getInfantes() + " inf";
            }
        }
        if(getAcompanantes()>0){
            if(getAdultos()>0 || getMenores()>0 || getInfantes()>0){
                cantPaxs += " + ";
            }
            cantPaxs += getAcompanantes();
            if(extendido){
                cantPaxs += " acomp";
            }
        }
        return cantPaxs;
    }

    public String getFechaOriginalEjecucion() {
        return fechaOriginalEjecucion;
    }

    public void setFechaOriginalEjecucion(String fechaOriginalEjecucion) {
        this.fechaOriginalEjecucion = fechaOriginalEjecucion;
    }

    public String getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(String fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public double getImporteDevuelto() {
        return importeDevuelto;
    }

    public void setImporteDevuelto(double importeDevuelto) {
        this.importeDevuelto = importeDevuelto;
    }

    public boolean esPosibleDevolver(double aDevolver){
        return aDevolver <= getPrecio();
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

    public String getFechaReporteVenta() {
        return fechaReporteVenta;
    }

    public void setFechaReporteVenta(String fechaReporteVenta) {
        this.fechaReporteVenta = fechaReporteVenta;
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

    boolean isDevTotal(){
        if(getEstado()==ESTADO_DEVUELTO && getImporteDevuelto()==getPrecio()){
            return true;
        }
        return false;
    }

    public boolean isDevParcial(){
        if(getEstado()==ESTADO_DEVUELTO && getImporteDevuelto()<getPrecio()){
            return true;
        }
        return false;
    }

    public double getSaldoFinal(){
        double saldo = 0;
        if(getEstado()==ESTADO_ACTIVO){
            saldo = getPrecio();
        } else if(getEstado()==ESTADO_DEVUELTO && isDevParcial()){
            saldo = getPrecio() - getImporteDevuelto();
        }
        return saldo;
    }

    static List<Reserva> getSoloActivas(List<Reserva> list){
        List<Reserva> resultado = new ArrayList<>();
        for(Reserva reserva: list){
            if(reserva.getEstado()==Reserva.ESTADO_ACTIVO ||
                    reserva.getEstado()==Reserva.ESTADO_DEVUELTO && reserva.getImporteDevuelto() < reserva.getPrecio()
                            && reserva.getImporteDevuelto() > 0){
                resultado.add(reserva);
            }
        }
        return resultado;
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
