/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;

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

    /**
     * The PostalCodeController constructor takes in the filepath of the CSV file, and then associates it to its
     * variable. It then takes that and parses it using CSVReader and puts all of that data in a HashMap of types
     * String, PostalCode. The contructor will catch the NullPointerException, if any, that would be thrown from the
     * parse() method.
     *
     * @param csvFilePath the project filepath to the csv file.
     */

    public PostalCodeController(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        try {
            postalCodes = this.parse();
        } catch (NullPointerException e) {
            System.out.println("NullPointerException occurred while parsing the CSV file.\n" + e);
        }
    }

    /**
     * This method parses through the csv file using CSVReader. For that, it gets the filepath through the resources'
     * directory of the project. Using the CSVreader along with the FileReader, this method retrieves the data from the
     * CSV file.
     *
     * @return A HashMap of the PostalCodes with values with the postalCode string as keys.
     * @throws NullPointerException in the event that getPath tries to get the filepath to a null csvFilePath.
     */
    public HashMap<String, PostalCode> parse() throws NullPointerException {
        try {
            //  1) Gathers the data from the CSV file.
            String csvPath = getClass().getResource(csvFilePath).getPath();
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvPath)).build();

            /*  2) Reads through each line of the CSV file, each line being an array of data;
             *   Position 0 is the Id;
             *   Position 1 is the Country;
             *   Position 2 is the Postal Code;
             *   Positions 3, 4 are skipped;
             *   Positions 5, 6, 7 are the provinces if the array is of length 7, 8, and 9 respectively;
             *   Positions 6, 7, 8 are the longitudes if the array is of length 7, 8, and 9 respectively;
             *   Positions 7, 8, 9 are the latitudes if the array is of length 7, 8, and 9 respectively/
             */

            /*  3)
             *   From there, the method simply uses the setter to set the values of a new instance of PostalCode.
             *   It is to note that a new instance of PostalCode is made for every new array.
             *   Each PostalCode instance is then added to the postalCodes HashMap, with the key being the actual postal
             *   code. In the case that the CSV file is corrupted to begin with or the filePath doesn't exist, an
             *   exception will be thrown and managed.
             */
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                PostalCode pc = new PostalCode();
                pc.setId(Integer.parseInt(nextLine[0]));
                pc.setCountry(nextLine[1]);
                pc.setPostalCode(nextLine[2]);
                if (nextLine.length == 7) {
                    pc.setProvince(nextLine[4]);
                    pc.setLatitude(Float.parseFloat(nextLine[5]));
                    pc.setLongitude(Float.parseFloat(nextLine[6]));
                    this.getPostalCodes().put(nextLine[2], pc);
                } else if (nextLine.length == 8) {
                    {
                        pc.setProvince(nextLine[5]);
                        pc.setLatitude(Float.parseFloat(nextLine[6]));
                        pc.setLongitude(Float.parseFloat(nextLine[7]));
                        this.getPostalCodes().put(nextLine[2], pc);
                    }
                } else if (nextLine.length == 9) {
                    pc.setProvince(nextLine[6]);
                    pc.setLatitude(Float.parseFloat(nextLine[7]));
                    pc.setLongitude(Float.parseFloat(nextLine[8]));
                    this.getPostalCodes().put(nextLine[2], pc);
                }
            }
            return this.getPostalCodes();
        } catch (CsvValidationException | IOException e) {
            System.out.println("An error occurred in PostalCodeController.parse() \n" + e);
        }
        return null;
    }

    /**
     * This method takes two PostalCode instances: the initial and final locations. The method will the use the
     * haversine formula to calculate the real world distance between them using their longitude and latitude.
     *
     * @param from is the Postal Code instance of the initial position.
     * @param to   is the Postal Code instance of the final destination.
     * @return the distance between the two postal codes as a double.
     */
    public double distanceTo(PostalCode from, PostalCode to) {
        //  1) Creates an instance of PostalCode controller to access the postal codes' data.
        PostalCodeController pcController = new PostalCodeController("/csv/zipcodes.csv");

        //  2) Application of the Haversine formula.
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
        return radius * c;
    }

    /**
     * This method returns the nearest postal codes to a user-inputted postal code within a user-inputted radius in km.
     * It works by invoking the distanceTo() method between the user-inputted postal code and every single postal code
     * in the postalCodes hashmap in PostalCodeController and stores the Double distance into a distance variable.
     * Then if the distance is lower or equal to the user-inputted radius, the method will return a hashmap with the key
     * being a PostalCode and the value being the distance between the user pc and the pc in the pcc hashmap (more on
     * that later.)
     *
     * @param from is the user-inputted postal code, the point from which we want to find other postal codes within a
     *             given radius.
     * @param radius is the user-defined radius.
     * @return a HashMap of type postal codes, which are the ones within the radius, and the distance between the user
     * inputted postal code and that particular postal code.
     * @throws IllegalArgumentException if for some reason the origin postal code or the radius are incorrect.
     */
    public HashMap<PostalCode, Double> nearbyLocations (PostalCode from, double radius) throws
    IllegalArgumentException {
        //  1) Initialize an instance of PostalCodeController to use the HashMap of postal codes.
        PostalCodeController pcController = new PostalCodeController("/csv/zipcodes.csv");

        // 2) Creates a new hashmap that will host the postal codes that are within distance and the distance.
        HashMap<PostalCode, Double> nearbyPostalMap = new HashMap<>();
        //  3) Iterating through each PostalCode and checking the distance with distanceTo().
        for (PostalCode pc : pcController.getPostalCodes().values()) {
            double distance = distanceTo(from, pc);
            //  4) if the distance is lower or equal to the distance AND the distance is not 0.0, then put the distance
            //  and the corresponding postal code in the hashmap and return that.
            if (distance <= radius && distance != 0.0) {
                nearbyPostalMap.put(pc, distance);
            }
        }
        return nearbyPostalMap;
    }

    // Getter for the postal code HashMap. It will allow us to iterate through every postal code in Canada.
    public HashMap<String, PostalCode> getPostalCodes () {
        return postalCodes;
    }
}

