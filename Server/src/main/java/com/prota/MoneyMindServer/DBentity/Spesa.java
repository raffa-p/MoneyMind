package com.prota.MoneyMindServer.DBentity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author Prota Raffaele
 */
@Entity
@Table(name="spesa", schema="635650")
public class Spesa implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long ID;
    
    @Column(name="costo")
    private double costo;
    
    @Column(name="categoria")
    private String categoria;
    
    @Column(name="username")
    private String username;
    
    @Column(name="ricorrente")
    private boolean ricorrente;
    
    @Column(name="timestamp")
    private Timestamp timestamp;
    
    @Column(name="ricorrenza")
    private String ricorrenza;
    
    @Column(name="prossimopagamento")
    private Timestamp prossimoPagamento;
    
    @Column(name="ricorrenzemese")
    private int ricorrenzeMese;
    
    public Spesa(){}

    public Spesa(double costo, String categoria, String username, boolean ricorrente){ 
        this(costo, categoria, username, ricorrente, null, new Timestamp(System.currentTimeMillis()), null, 0);
    }
    
    public Spesa(double costo, String categoria, String username, boolean ricorrente, String ricorrenza, Timestamp prossimoPagamento){ 
        this(costo, categoria, username, ricorrente, null, new Timestamp(System.currentTimeMillis()), prossimoPagamento, 0);
    }
    
    public Spesa(double costo, String categoria, String username, boolean ricorrente, String ricorrenza, Timestamp timestamp, Timestamp prossimoPagamento, int ricorrenzeMese){ 
        this.costo = costo; 
        this.categoria = categoria; 
        this.username = username;
        this.ricorrente = ricorrente;
        this.timestamp = timestamp;
        this.ricorrenza = ricorrenza;
        this.prossimoPagamento = prossimoPagamento;
        this.ricorrenzeMese = ricorrenzeMese;
    }

    public Long getID(){ return ID; }
    
    public double getCosto(){ return costo; }
    
    public String getCategoria(){ return categoria; }
    
    public String getUsername(){ return username; }
    
    public boolean isRicorrente(){ return ricorrente; }
    
    public Timestamp getTimestamp(){ return timestamp; }
    
    public String getRicorrenza(){ return ricorrenza; }
    
    public Timestamp getProssimoPagamento(){ return prossimoPagamento; }
    
    public int getRicorrenzeMese(){ return ricorrenzeMese; }

    public void setID(Long ID){ this.ID = ID; }

    public void setCosto(double costo){ this.costo = costo; }

    public void setCategoria(String categoria){ this.categoria = categoria; }

    public void setUsername(String username){ this.username = username; }

    public void setRicorrente(boolean ricorrente){ this.ricorrente = ricorrente; }
    
    public void setTimestamp(Timestamp timestamp){ this.timestamp = timestamp; }
    
    public void setRicorrenza(String ricorrenza){ this.ricorrenza = ricorrenza; }
    
    public void setProssimoPagamento(Timestamp prossimoPagamento){ this.prossimoPagamento = prossimoPagamento; }
    
    public void setRicorrenzeMese(int ricorrenzeMese){ this.ricorrenzeMese = ricorrenzeMese; }
}
