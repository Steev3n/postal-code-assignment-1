/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
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
        postalCodes = this.parse();
    }
    
    public HashMap<String, PostalCode> parse(){
        try{
            String csvPath = getClass().getResource("/csv/zipcodes.csv").getPath();
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvPath)).build();

            String[] nextLine;
            while((nextLine = reader.readNext()) != null){
                PostalCode pc = new PostalCode();
                if(nextLine.length == 7){
                    pc.setId(Integer.parseInt(nextLine[0]));
                    pc.setCountry(nextLine[1]);
                    pc.setPostalCode(nextLine[2]);
                    pc.setProvince(nextLine[4]);
                    pc.setLatitude(Float.parseFloat(nextLine[5]));
                    pc.setLongitude(Float.parseFloat(nextLine[6]));
                    this.getPostalCodes().put(nextLine[2], pc);
                }
                else if(nextLine.length == 8){{
                    pc.setId(Integer.parseInt(nextLine[0]));
                    pc.setCountry(nextLine[1]);
                    pc.setPostalCode(nextLine[2]);
                    pc.setProvince(nextLine[5]);
                    pc.setLatitude(Float.parseFloat(nextLine[6]));
                    pc.setLongitude(Float.parseFloat(nextLine[7]));
                    this.getPostalCodes().put(nextLine[2], pc);
                }
                }
                else if(nextLine.length == 9){
                    pc.setId(Integer.parseInt(nextLine[0]));
                    pc.setCountry(nextLine[1]);
                    pc.setPostalCode(nextLine[2]);
                    pc.setProvince(nextLine[6]);
                    pc.setLatitude(Float.parseFloat(nextLine[7]));
                    pc.setLongitude(Float.parseFloat(nextLine[8]));
                    this.getPostalCodes().put(nextLine[2], pc);
                }
            }
            return this.getPostalCodes();
        }
        catch(CsvValidationException | IOException e){
            System.out.println("An error occurred in PostalCodeController.parse() \n" + e);
        }
        return null;
    }
    
    public static double distanceTo(PostalCode from, PostalCode to){
        PostalCodeController pcController = new PostalCodeController("/csv/zipcodes.csv");
        try {
            final int radius = 6371;
            double lat1 = pcController.getPostalCodes().get(from.getPostalCode()).getLatitude();
            double lat2 = pcController.getPostalCodes().get(to.getPostalCode()).getLatitude();
            double lon1 = pcController.getPostalCodes().get(from.getPostalCode()).getLongitude();
            double lon2 = pcController.getPostalCodes().get(to.getPostalCode()).getLongitude();
            double lat1Rad = Math.toRadians(lat1);
            double lat2Rad = Math.toRadians(lat2);
            double lat1Diff = Math.toRadians(lat2 - lat1);
            double lat2Diff = Math.toRadians(lon2 - lon1);
            double a = Math.sin(lat1Diff / 2) * Math.sin(lat1Diff / 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(lat2Diff / 2) * Math.sin(lat2Diff / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return radius*c;
        }
        catch(Exception e){
            System.out.println("An error occured " + e);
        }
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

    public String getCsvFilePath() {
        return this.csvFilePath;
    }

    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }
}
