/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.util;

import java.io.Serializable;

/**
 *
 * @author danny_000
 */
public class Config implements Serializable {
    
    String path;
    String authCode;
    private static Config instance = null;
    
    protected Config(){
        
    }
    
    public static Config getInstance(){
        if (instance == null){
            instance = new Config();
        }
        return instance;
    }
    
}
