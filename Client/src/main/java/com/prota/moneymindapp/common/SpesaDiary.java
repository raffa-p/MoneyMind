package com.prota.moneymindapp.common;

/**
 *
 * @author Prota Raffaele
 */
public class SpesaDiary {
    
    private Long ID;
    
    private double costo;
    
    private String categoria;
    
    private boolean ricorrente;
    
    public SpesaDiary(){}
    
    public SpesaDiary(Long ID, double costo, String categoria, boolean ricorrente){
        this.ID = ID;
        this.costo = costo;
        this.categoria = categoria;
        this.ricorrente = ricorrente;
    }
    
    public Long getID(){ return ID; }
    
    public double getCosto(){ return costo; }
    
    public String getCategoria(){ return categoria; }
    
    public boolean getRicorrente(){ return ricorrente; }
    
    public void setID(Long ID){ this.ID = ID; }
    
    public void setCosto(double costo){ this.costo = costo; }
    
    public void setCategoria(String categoria){ this.categoria = categoria; }
    
    public void setRicorrente(boolean ricorrente){ this.ricorrente = ricorrente; }
}
