package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DataSource;

import java.io.IOException;

public class MainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
       FXMLLoader loader =new FXMLLoader(getClass().getResource("/SeConnecter.fxml"));
     //FXMLLoader loader =new FXMLLoader(getClass().getResource("/SeConnecterAdmin.fxml"));
        Parent root = loader.load();
        Scene scene=new Scene(root);
        primaryStage.setScene(scene);
       primaryStage.setTitle("ESPRIT-MARKET");
       // primaryStage.setTitle("ADMIN DASHBOARD");

        primaryStage.show();

    }
}




