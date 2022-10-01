/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import controllers.PostalCodeController;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import models.PostalCode;

/**
 *
 * @author steve
 */
public class Driver {
    
    public static void main(String[] args) {
        Driver tester = new Driver();
        tester.testParse();
    }
    
    void testParse(){
        try{
            String csvPath = getClass().getResource("/csv/zipcodes.csv").getPath();
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvPath)).build();
            PostalCodeController pcController = new PostalCodeController(csvPath);
            
            String[] nextLine;
            while((nextLine = reader.readNext()) != null){
                PostalCode pc = new PostalCode();
                pc.setId(Integer.valueOf(nextLine[0]));
                pc.setLatitude(Float.parseFloat(nextLine[5]));
                pc.setLongitude(Float.parseFloat(nextLine[6]));
                pc.setCountry(nextLine[1]);
                pc.setProvince(nextLine[4]);
                pc.setPostalCode(nextLine[2]);
                pcController.getPostalCodes().put(nextLine[2], pc);
            }
            System.out.println(pcController.getPostalCodes().get("H1Z"));
            
        }
        catch(CsvValidationException | IOException e){
            System.out.println("An error occured " + e);
        }
    }
    
    void testDistanceTo(String postalCode){
    }
    
    void testNearbyLocations(String postalCode){
    }
}
