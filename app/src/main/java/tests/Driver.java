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

import java.util.*;

/**
 *
 * @author steve
 */
public class Driver {

    public static void main(String[] args) {
        Driver tester = new Driver();
        PostalCode pcTest = new PostalCodeController("/csv/zipcodes.csv").getPostalCodes().get("H7L");
        tester.testNearbyLocations(pcTest);
    }

    void testParse(){
        try{
            String csvPath = getClass().getResource("/csv/zipcodes.csv").getPath();
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvPath)).build();
            PostalCodeController pcController = new PostalCodeController(csvPath);

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
                    pcController.getPostalCodes().put(nextLine[2], pc);
                }
                else if(nextLine.length == 8){{
                    pc.setId(Integer.parseInt(nextLine[0]));
                    pc.setCountry(nextLine[1]);
                    pc.setPostalCode(nextLine[2]);
                    pc.setProvince(nextLine[5]);
                    pc.setLatitude(Float.parseFloat(nextLine[6]));
                    pc.setLongitude(Float.parseFloat(nextLine[7]));
                    pcController.getPostalCodes().put(nextLine[2], pc);
                }
                }
                else if(nextLine.length == 9){
                    pc.setId(Integer.parseInt(nextLine[0]));
                    pc.setCountry(nextLine[1]);
                    pc.setPostalCode(nextLine[2]);
                    pc.setProvince(nextLine[6]);
                    pc.setLatitude(Float.parseFloat(nextLine[7]));
                    pc.setLongitude(Float.parseFloat(nextLine[8]));
                    pcController.getPostalCodes().put(nextLine[2], pc);
                }
            }
        }
        catch(CsvValidationException | IOException e){
            System.out.println("An error occured " + e);
        }
    }

    double testDistanceTo(PostalCode from, PostalCode to){
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

    /*
    * This method will test the nearby locations method. However, commented-out code is used to sort out the HashMap. It is to note that HashMaps
    * do not respect the order of insertion. Therefore, the HashMap must be sorted in order to get the closest locations.
     */
    void testNearbyLocations(PostalCode from){
        PostalCodeController pcController = new PostalCodeController("/csv/zipcodes.csv");
        try{
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter a desired radius in km: ");
            double radius = sc.nextDouble();
            HashMap<PostalCode, Double> nearbyPostalMap = new HashMap<>();
//            ArrayList<Double> distances = new ArrayList<>();
//            HashMap<PostalCode, Double> sortedNearbyPostalMap = new HashMap<>();
            for(PostalCode pc : pcController.getPostalCodes().values()){
                double distance = testDistanceTo(from, pc);
                if(distance <= radius && distance != 0.0){
                    nearbyPostalMap.put(pc, distance);
                }
            }

            System.out.println("Nearby postal codes to " + from.getPostalCode() + " are: ");

//            for(Map.Entry<PostalCode, Double> entry : nearbyPostalMap.entrySet()){
//                distances.add(entry.getValue());
//            }
//            Collections.sort(distances);
//            for(Double d : distances){
//                for(Map.Entry<PostalCode, Double> entry : nearbyPostalMap.entrySet()){
//                    if(Objects.equals(entry.getValue(), d)){
//                        sortedNearbyPostalMap.put(entry.getKey(), entry.getValue());
//                        System.out.println("Postal Code: " + entry.getKey().getPostalCode() + " - Distance: " + entry.getValue());
//                    }
//                }
//            }
            System.out.println(nearbyPostalMap);
        }
        catch(InputMismatchException e){
            System.out.println("An error occured " + e);
        }
    }

}
