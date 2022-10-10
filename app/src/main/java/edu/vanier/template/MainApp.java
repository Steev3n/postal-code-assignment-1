package edu.vanier.template;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.UIManager;


/**
 * This is a JavaFX project template to be used for creating GUI applications.
 * JavaFX 18 is already linked to this project in the build.gradle file.
 * @link: https://openjfx.io/javadoc/18/
 * @see: Build Scripts/build.gradle
 * @author Sleiman Rabah.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage mainStage) throws Exception {
        UIManager uimanager = new UIManager();
        uimanager.mainWindow(mainStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}