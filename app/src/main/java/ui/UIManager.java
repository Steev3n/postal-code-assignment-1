package ui;

import controllers.PostalCodeController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.PostalCode;

import java.util.*;


public class UIManager {

    // All temporary, but necessary, data holders to use as a medium of communication between the methods.
    static PostalCodeController pcc = new PostalCodeController("/csv/zipcodes.csv");
    static PostalCode pcFrom, pcTo;
    static String postal1Text, postal2Text, stringInputRadius;
    static double inputRadius;

    /*
        Where all the magic happens. This mainWindow method, when run, launches the main stage. Upon doing so,
        2 buttons are shown:
        1) Compute Distance: Launches the computeDistanceWindow() method (more on that later).
        2) Find Locations Within a Radius: Launches the nearbyLocationsWindow() method (more on that later).

        Once the buttons are pressed and the methods are done running, a specific set of events happen for each button:
        1) Compute Distance *after*: Checks if the first postal code is invalid, for which it will launch the
        alertErrorDIalog() method that shows an error popup to tell the user that they messed up. Also checks if the
        second postal code is invalid, for which the same thing happens. Otherwise, an alert popup will show, this time
        showing the distance between the two postal codes.

        2) Find Locations Within a Radius *after*: Checks if the first postal code is invalid, for which the same error
        dialog appears. Otherwise, the program will try to convert the stringInputRadius into double, which will be
        caught as an exception if no double value was entered. Now, a new HashMap is created along with an ArrayList.
        What happens after is the sorting of the hashmap obtained from the nearbyLocations() method (sorted by distance
        from user-inputted postal code). Iff all of this has happened, the tableViewWindow() method will run, which
        launches the method that displays the results as a TableView.
     */
    public void mainWindow(Stage mainStage){
        mainStage.setTitle("Postal Code Distance Calculator");

        //  1) The mainRoot and Root from which the window grows!
        BorderPane mainRoot = new BorderPane();
        VBox root = new VBox();
        root.setSpacing(5.0);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(mainRoot, 700, 600);

        //  2) Button 1, everything that has to do with computing distance between two postal codes.
        Button button1 = new Button("Compute Distance");
        button1.setOnAction((e) -> {
            computeDistanceWindow(mainStage);
            //  2a) Check if postal code 1 is valid.
            if(pcc.getPostalCodes().get(pcFrom.getPostalCode()) == null){
                alertErrorDialog(mainStage, "Oops <:(", "Postal Code 1 is invalid! Please try again.");
            }
            //  2b) Check if postal code 2 is valid.
            else if(pcc.getPostalCodes().get(pcTo.getPostalCode()) == null){
                alertErrorDialog(mainStage, "Oops <:(", "Postal Code 2 is invalid! Please try again.");
            }
            //  2c) Otherwise, display distance.
            else{
                Alert distance = new Alert(Alert.AlertType.INFORMATION);
                double d = pcc.distanceTo(pcFrom, pcTo);
                distance.setHeaderText("Success!");
                distance.setContentText("The distance from " + pcFrom.getPostalCode() + " to " + pcTo.getPostalCode() + " is " + d + "km.");
                distance.showAndWait();
            }
            });

        //  3) Button 2, everything that has to do with finding postal codes within a given radius.
        Button button2 = new Button("Find Locations Within a Radius");
        button2.setOnAction((e) -> {

            nearbyLocationsWindow(mainStage);

            //  3a) Check if postal code 1 is valid.
            if(pcc.getPostalCodes().get(pcFrom.getPostalCode()) == null){
                alertErrorDialog(mainStage, "Oops <:(", "Postal Code is invalid! Please try again!");
            }
            else{
                try {
                    //  3b) Try to convert user-inputted radius into double, otherwise catch the exception and show
                    //  error.
                    inputRadius = Double.parseDouble(stringInputRadius);
                    HashMap<PostalCode, Double> unsortedMap = pcc.nearbyLocations(pcc.getPostalCodes().get(pcFrom.getPostalCode()), inputRadius);

                    //  3c) Sorting algorithm.
                    ArrayList<Double> sortedDistances = new ArrayList<>(unsortedMap.values());
                    Collections.sort(sortedDistances);
                    LinkedHashMap<PostalCode, Double> sortedMap = new LinkedHashMap<>();
                    for (Double d : sortedDistances) {
                        for (Map.Entry<PostalCode, Double> entry : unsortedMap.entrySet()) {
                            if (Objects.equals(entry.getValue(), d)) {
                                sortedMap.put(entry.getKey(), d);
                            }
                        }
                    }
                    //  3d) Show the results using tableViewWindow().
                    tableViewWindow(mainStage,sortedMap);

                }
                catch(Exception b){
                    alertErrorDialog(mainStage, "Oops <:(", "The entered radius is incorrect! Please try again!");
                }
            }
        });

        //  4) Little touch-ups needed to make the main window look nice.
        Text title = new Text("Postal Code Distance Calculator");
        Text bottomTitle = new Text("Powered by Canada Post");
        bottomTitle.setFont(new Font("Serif",8));
        title.setFont(new Font("Serif",18));

        BorderPane.setAlignment(title,Pos.CENTER);
        mainRoot.setCenter(root);
        root.getChildren().addAll(button1, button2);
        mainRoot.setTop(title);
        mainRoot.setBottom(bottomTitle);

        //  5) Show the window.
        mainStage.setScene(scene);
        mainStage.sizeToScene();
        mainStage.show();
    }

    /**
     * This method simply shows the dialog window in which the user will write the origin and destination postal codes.
     * Once the button is pressed, the postal code Strings will be saved in temporary Postal Code instances, which only
     * have postalCode as their variables set.
     *
     * @param owner is the owner of this dialog window (mainStage).
     */
    public void computeDistanceWindow(Stage owner){
        Stage stage = stageMaker(owner, "Distance Computer");

        //  1) The mainRoot and Root from which the window grows!
        BorderPane mainRoot = new BorderPane();
        VBox root = new VBox();
        root.setSpacing(4.0);
        root.setMaxWidth(20);
        mainRoot.setCenter(root);

        //  2) The title, TextFields and Button of the window.
        Text title = new Text("Welcome to the Distance Computer!");
        TextField postal1 = new TextField();
        postal1.setPromptText("Enter Postal Code 1");
        TextField postal2 = new TextField();
        postal2.setPromptText("Enter Postal Code 2");
        Button button = new Button("Compute Distance!");

        //  2a) Positional adjustments.
        title.setFont(new Font("sanserif",14));
        mainRoot.setTop(title);
        BorderPane.setAlignment(title,Pos.CENTER);
        root.getChildren().addAll(title, postal1, postal2,button);
        root.setAlignment(Pos.CENTER);
        pcFrom = new PostalCode();
        pcTo = new PostalCode();

        //  3) Button action listener.
        button.setOnAction((e)->{
            postal1Text = postal1.getText().strip().toUpperCase();
            pcFrom.setPostalCode(postal1Text);
            postal2Text = postal2.getText().strip().toUpperCase();
            pcTo.setPostalCode(postal2Text);
            stage.close();
        });

        Scene scene = new Scene(mainRoot,  400, 300);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * This method shows the dialog window in which the user will put the origin postal code as well as the radius in
     * km. Similar to the computeDistanceWindow() method, a temporary Postal Code instance is made just to store the
     * postalCode.
     *
     * @param owner is the owner of this dialog window (mainStage).
     */
    public void nearbyLocationsWindow(Stage owner){
        Stage stage = stageMaker(owner, "Nearby Locations Calculator");

        //  1) The mainRoot and the Root from which the window grows!
        BorderPane mainRoot = new BorderPane();
        VBox root = new VBox();
        root.setSpacing(4.0);
        root.setMaxWidth(20);
        mainRoot.setCenter(root);

        //  2) The Title, TextFields, and button of the window.
        Text title = new Text("Welcome to the Nearby Locations Finder!");
        TextField postal1 = new TextField();
        postal1.setPromptText("Enter Postal Code 1");
        TextField radius = new TextField();
        radius.setPromptText("Enter a Radius in km");
        Button button = new Button("Find Locations!");

        //  2a) Positional Adjustments
        title.setFont(new Font("sanserif",14));
        mainRoot.setTop(title);
        BorderPane.setAlignment(title,Pos.CENTER);
        root.getChildren().addAll(title, postal1, radius, button);
        root.setAlignment(Pos.CENTER);
        pcFrom = new PostalCode();

        //  3) Button action listener.
        button.setOnAction((e)->{
            postal1Text = postal1.getText().strip().toUpperCase();
            pcFrom.setPostalCode(postal1Text);
            stringInputRadius = radius.getText().strip();
            stage.close();
        });

        Scene scene = new Scene(mainRoot,  400, 300);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * This method displays the results from the nearbyLocations process. It displays in a TableView the relevant
     * information for the user in order of distance from the original postal code.
     *
     * @param owner is the owner of this dialog window (mainStage by extension)
     * @param inputMap is the LinkedHashMap that hosts the sorted results.
     */
    public void tableViewWindow(Stage owner, HashMap<PostalCode, Double> inputMap){
        Stage stage = stageMaker(owner, "Nearest Locations to " + pcFrom.getPostalCode());

        //  1) The table... from which the window grows!
        TableView<PostalCode> table = new TableView<>();

        //  2) Initialization of columns that will host the id, postalCode, and province.
        TableColumn<PostalCode, Integer> column1id = new TableColumn<>();
        column1id.setCellValueFactory(new PropertyValueFactory<>("id"));
        column1id.setMinWidth(Control.USE_COMPUTED_SIZE);
        column1id.setText("Id");
        TableColumn<PostalCode, String> column2postalCode = new TableColumn<>();
        column2postalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        column2postalCode.setMinWidth(Control.USE_COMPUTED_SIZE);
        column2postalCode.setText("Postal Code");
        TableColumn<PostalCode, String> column3province = new TableColumn<>();
        column3province.setCellValueFactory(new PropertyValueFactory<>("province"));
        column3province.setMinWidth(Control.USE_COMPUTED_SIZE);
        column3province.setText("Province");

        //  3) Adding all columns to the table.
        table.getColumns().addAll(column1id,column2postalCode,column3province);
        for(Map.Entry<PostalCode, Double> entry : inputMap.entrySet()){
            table.getItems().add(entry.getKey());
            System.out.println(entry.getKey().getPostalCode());
        }

        Scene scene = new Scene(table,400,300);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Useful to call when an error dialog is needed to prevent repeated code.
     *
     * @param owner is the owner of this dialog.
     * @param header is the message that is displayed as the "title" of the error.
     * @param message is the error message.
     */
    public void alertErrorDialog(Stage owner, String header, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(owner);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Similar to alertErrorDialog, stageMaker prevents repeated code by serving as a "constructor" for stages.
     *
     * @param owner is the owner of this dialog stage.
     * @param title is the title of the dialog stage.
     * @return a stage.
     */
    public Stage stageMaker(Stage owner, String title){
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }

}
