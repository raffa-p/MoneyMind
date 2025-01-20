package com.prota.MoneyMindServer.DBentity;

import com.google.gson.annotations.Expose;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author Prota Raffaele
 */
@Entity
@Table(name="categoria", schema="635650")
public class Categoria implements Serializable {
    
    @Expose
    @Id
    @Column(name="nome")
    private String nome;
    
    @Expose
    @Column(name="descrizione")
    private String descrizione;
    
    public Categoria(){}
    
    public Categoria(String nome, String descrizione){ this.nome = nome; this.descrizione = descrizione; }

    public String getNome(){ return nome; }

    public String getDescrizione(){ return descrizione; }
    
    public void setNome(String nome){ this.nome = nome; }

    public void setDescrizione(String descrizione){ this.descrizione = descrizione; }
    
}
