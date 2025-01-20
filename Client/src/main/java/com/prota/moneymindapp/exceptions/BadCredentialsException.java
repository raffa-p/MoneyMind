package com.prota.moneymindapp.exceptions;

import com.prota.moneymindapp.ErrorType;

/**
 *
 * @author Prota Raffaele
 */
public class BadCredentialsException extends Exception {
    private ErrorType type;
        
    public BadCredentialsException(){ this(ErrorType.NOT_RECOGNIZED); }
    
    public BadCredentialsException(ErrorType type){
        super(type.toString());
        this.type = type;  
    }
    
    public ErrorType getType(){ return type; }
    
    public void setType(ErrorType type){ this.type = type; }
}
