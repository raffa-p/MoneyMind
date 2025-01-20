package com.prota.MoneyMindServer.DBentity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Prota Raffaele
 */
@Entity
@Table(name="user", schema="635650")
public class User implements Serializable {
    
    @Id
    @Column(name="username")
    private String username;
    
    @Column(name="password")
    private String password;

    public User(){}
    
    public User(String username){ this(username, ""); }
    
    public User(String username, String password){ this.username = username; this.password = password; }

    public String getUsername(){ return username; }

    public String getPassword(){ return password; }

    public void setUsername(String username){ this.username = username; }

    public void setPassword(String password){ this.password = BCrypt.hashpw(password, BCrypt.gensalt()); }
}
