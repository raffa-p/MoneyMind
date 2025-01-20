package com.prota.moneymindapp.common;

import java.sql.Timestamp;

/**
 *
 * @author Prota Raffaele
 */
public class SpesaRicorrente {
    
    public Long ID;
    
    private double costo;
    
    private String categoria;
    
    private String prossimoPagamento;
    
    public SpesaRicorrente(Long ID, double costo, String categoria, String prossimoPagamento){ 
        this.ID = ID;
        this.costo = costo; 
        this.categoria = categoria; 
        this.prossimoPagamento = prossimoPagamento;
    }

    public Long getID(){ return ID; }
    
    public double getCosto(){ return costo; }
    
    public String getCategoria(){ return categoria; }
    
    public String getProssimoPagamento(){ return prossimoPagamento; }
    
    public void setID(Long ID){ this.ID = ID; }

    public void setCosto(double costo){ this.costo = costo; }

    public void setCategoria(String categoria){ this.categoria = categoria; }

    public void setProssimoPagamento(String prossimoPagamento){ this.prossimoPagamento = prossimoPagamento; }
    
}
