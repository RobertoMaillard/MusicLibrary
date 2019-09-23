package com.robertomaillard.musiclibrary;

import com.robertomaillard.musiclibrary.model.Datasource;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        if(!Datasource.getInstance().open()) {
            System.out.println("FATAL ERROR: Couldn't connect to database");
            Platform.exit();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
        Parent root = fxmlLoader.load();

        // LOADS THE ListView ObservableList BEFORE SETTING THE SCENE
        Controller controller = fxmlLoader.getController();
        controller.listartist();

        // SETS THE SCENE
        primaryStage.setTitle("Music Library");
        primaryStage.setScene(new Scene(root, 809, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Datasource.getInstance().close();
    }
}
