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

    static PostalCodeController pcc = new PostalCodeController("/csv/zipcodes.csv");
    static PostalCode pcFrom, pcTo;
    static String postal1Text, postal2Text, stringInputRadius;
    static double inputRadius;

    public void mainWindow(Stage mainStage){
        mainStage.setTitle("Postal Code Distance Calculator");

        BorderPane mainRoot = new BorderPane();
        VBox root = new VBox();
        root.setSpacing(5.0);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(mainRoot, 700, 600);

        Button button1 = new Button("Compute Distance");
        button1.setOnAction((e) -> {
            computeDistanceWindow(mainStage);
            if(pcc.getPostalCodes().get(pcFrom.getPostalCode()) == null){
                Alert invalid = new Alert(Alert.AlertType.ERROR);
                invalid.setHeaderText("Oops <:(");
                invalid.setContentText("Postal Code 1 is invalid! Please try again.");
                invalid.showAndWait();
            }
            else if(pcc.getPostalCodes().get(pcTo.getPostalCode()) == null){
                Alert invalid = new Alert(Alert.AlertType.ERROR);
                invalid.setHeaderText("Oops <:(");
                invalid.setContentText("Postal Code 2 is invalid! Please try again.");
                invalid.showAndWait();
            }
            else{
                Alert distance = new Alert(Alert.AlertType.INFORMATION);
                double d = pcc.distanceTo(pcFrom, pcTo);
                distance.setHeaderText("Success!");
                distance.setContentText("The distance from " + pcFrom.getPostalCode() + " to " + pcTo.getPostalCode() + " is " + d + "km.");
                distance.showAndWait();
            }
            });

        Button button2 = new Button("Find Locations Within a Radius");
        button2.setOnAction((e) -> {

            nearbyLocationsWindow(mainStage);

            if(pcc.getPostalCodes().get(pcFrom.getPostalCode()) == null){
                Alert invalid = new Alert(Alert.AlertType.ERROR);
                invalid.setHeaderText("Oops <:(");
                invalid.setContentText("Postal Code is invalid! Please try again.");
                invalid.showAndWait();
            }
            else{
                try {
                    inputRadius = Double.parseDouble(stringInputRadius);
                    HashMap<PostalCode, Double> unsortedMap = pcc.nearbyLocations(pcc.getPostalCodes().get(pcFrom.getPostalCode()), inputRadius);

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
                    tableViewWindow(mainStage,sortedMap);

                }
                catch(Exception b){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initOwner(mainStage);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setHeaderText("Oops <:(");
                    alert.setContentText("The entered radius is incorrect!");
                    alert.showAndWait();
                }
            }
        });

        Text title = new Text("Postal Code Distance Calculator");
        Text bottomTitle = new Text("Powered by Canada Post");
        bottomTitle.setFont(new Font("Serif",8));
        title.setFont(new Font("Serif",18));

        BorderPane.setAlignment(title,Pos.CENTER);
        mainRoot.setCenter(root);
        root.getChildren().addAll(button1, button2);
        mainRoot.setTop(title);
        mainRoot.setBottom(bottomTitle);

        mainStage.setScene(scene);
        mainStage.sizeToScene();
        mainStage.show();

    }

    public void computeDistanceWindow(Stage owner){
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.setTitle("Distance Computer");
        stage.initModality(Modality.APPLICATION_MODAL);

        BorderPane mainRoot = new BorderPane();
        VBox root = new VBox();
        root.setSpacing(4.0);
        root.setMaxWidth(20);
        mainRoot.setCenter(root);

        Text title = new Text("Welcome to the Distance Computer!");
        TextField postal1 = new TextField();
        postal1.setPromptText("Enter Postal Code 1");
        TextField postal2 = new TextField();
        postal2.setPromptText("Enter Postal Code 2");
        Button button = new Button("Compute Distance!");

        title.setFont(new Font("sanserif",14));
        mainRoot.setTop(title);
        BorderPane.setAlignment(title,Pos.CENTER);
        root.getChildren().addAll(title, postal1, postal2,button);
        root.setAlignment(Pos.CENTER);
        pcFrom = new PostalCode();
        pcTo = new PostalCode();
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

    public void nearbyLocationsWindow(Stage owner) throws NumberFormatException{
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.setTitle("Nearby Locations");
        stage.initModality(Modality.APPLICATION_MODAL);

        BorderPane mainRoot = new BorderPane();
        VBox root = new VBox();
        root.setSpacing(4.0);
        root.setMaxWidth(20);
        mainRoot.setCenter(root);

        Text title = new Text("Welcome to the Nearby Locations Finder!");
        TextField postal1 = new TextField();
        postal1.setPromptText("Enter Postal Code 1");
        TextField radius = new TextField();
        radius.setPromptText("Enter a Radius in km");
        Button button = new Button("Find Locations!");

        title.setFont(new Font("sanserif",14));
        mainRoot.setTop(title);
        BorderPane.setAlignment(title,Pos.CENTER);
        root.getChildren().addAll(title, postal1, radius, button);
        root.setAlignment(Pos.CENTER);
        pcFrom = new PostalCode();
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

    public void tableViewWindow(Stage owner, HashMap<PostalCode, Double> inputMap){
        Stage stage = new Stage();
        stage.setTitle("Nearest Locations to " + pcFrom.getPostalCode());
        stage.initOwner(owner);

        TableView<PostalCode> table = new TableView<>();
        TableColumn<PostalCode, Integer> column1id = new TableColumn<>();
        column1id.setCellValueFactory(new PropertyValueFactory<>("id"));
        column1id.setMinWidth(12);
        column1id.setText("Id");
        TableColumn<PostalCode, String> column2postalCode = new TableColumn<>();
        column2postalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        column2postalCode.setMinWidth(16);
        column2postalCode.setText("Postal Code");
        TableColumn<PostalCode, String> column3province = new TableColumn<>();
        column3province.setCellValueFactory(new PropertyValueFactory<>("province"));
        column3province.setMinWidth(12);
        column3province.setText("Province");

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

}
