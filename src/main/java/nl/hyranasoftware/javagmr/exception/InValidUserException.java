/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.exception;

/**
 *
 * @author danny_000
 */
public class InValidUserException extends Exception {
    public InValidUserException () {

    }

    public InValidUserException (String message) {
        super (message);
    }

    public InValidUserException (Throwable cause) {
        super (cause);
    }

    public InValidUserException (String message, Throwable cause) {
        super (message, cause);
    }
    
}
