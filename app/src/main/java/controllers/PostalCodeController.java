/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.HashMap;
import models.PostalCode;

/**
 *
 * @author steve
 */
public class PostalCodeController {
    private HashMap<String, PostalCode> postalCodes = new HashMap<>();
    String csvFilePath;

    public PostalCodeController(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }
    
    public static HashMap<String, PostalCode> parse(){
        return null;
    }
    
    public static double distanceTo(PostalCode from, PostalCode to){
        return 0.0;
    }
    
    public static HashMap<String, PostalCode> nearbyLocations(PostalCode from){
        return null;
    }

    public HashMap<String, PostalCode> getPostalCodes() {
        return postalCodes;
    }

    public void setPostalCodes(HashMap<String, PostalCode> postalCodes) {
        this.postalCodes = postalCodes;
    }
    
    
}
