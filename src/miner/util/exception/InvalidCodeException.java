/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.util.exception;

/**
 *
 * @author carloseduardoxp
 */
public class InvalidCodeException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object o;
    
    public InvalidCodeException(String message,Object o) {
        super(message);
        this.o = o;
    }
    
    public InvalidCodeException(String message) {
        super(message);
    }

    public Object getO() {
        return o;
    }
    
    
}
