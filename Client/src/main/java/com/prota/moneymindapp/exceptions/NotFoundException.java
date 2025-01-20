package com.prota.moneymindapp.exceptions;

import com.prota.moneymindapp.ErrorType;

/**
 *
 * @author Prota Raffaele
 */
public class NotFoundException extends Exception {
        
    public NotFoundException(){ 
        super(ErrorType.NO_ITEMS_FOUND.toString());
    }
    
}
