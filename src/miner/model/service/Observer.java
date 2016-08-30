/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.model.service;

/**
 *
 * @author carloseduardoxp
 */
public interface Observer {
    
    public void sendStatusMessage(String message);
    public void sendCurrentPercent(Integer percent);    
    
}
