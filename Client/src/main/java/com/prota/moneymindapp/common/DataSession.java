package com.prota.moneymindapp.common;

/**
 *
 * @author Prota Raffaele
 */
public class DataSession {
    private static DataSession instance;
    private String username;
    
    public DataSession(){}
    
    public static DataSession getInstance(){
        if(instance == null){ instance = new DataSession(); }
        return instance;
    }
    
    public void setUsername(String username){ this.username = username; }
    
    public String getUsername(){ return username; }
    
    public void resetSession(){ username = null; }
}
