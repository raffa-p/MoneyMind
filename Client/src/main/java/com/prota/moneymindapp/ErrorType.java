package com.prota.moneymindapp;

/**
 *
 * @author Prota Raffaele
 */
public enum ErrorType {
    BAD_USERNAME, BAD_USERNAME_SIGNUP, BAD_PASSWORD, BAD_CONFIRM_PASSWORD, NOT_RECOGNIZED, NO_ITEMS_FOUND, VOID_FIELDS;
    
    @Override
    public String toString(){
        switch(this){
            case BAD_USERNAME: return "Username inesistente";
            case BAD_USERNAME_SIGNUP: return "Username gia' in uso";
            case BAD_PASSWORD: return "Password errata";
            case BAD_CONFIRM_PASSWORD: return "Le password devono essere uguali";
            case NO_ITEMS_FOUND: return "No items found";
            case VOID_FIELDS: return "Inserisci tutte le informazioni";
            case NOT_RECOGNIZED: return "Errore generico";
            default: return "X";
        }
    }
}
