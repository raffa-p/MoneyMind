package com.prota.moneymindapp.common;

/**
 *
 * @author Prota Raffaele
 */
public class SpesaCategoria {
    private String categoria;
    private double spesa;
    
    public SpesaCategoria(){}
    
    public SpesaCategoria(String categoria, double spesa){ this.categoria = categoria; this.spesa = spesa; }

    public String getCategoria(){ return categoria; }

    public double getSpesa(){ return spesa; }

    public void setCategoria(String categoria){ this.categoria = categoria; }

    public void setSpesa(double spesa){ this.spesa = spesa; }
}
