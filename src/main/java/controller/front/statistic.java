/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.front;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *
 * @author HP
 */
public class statistic extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
             FXMLLoader loader = new FXMLLoader(getClass().getResource("Statistics.fxml"));
          
            Parent root = loader.load(); 
            Scene scene = new Scene(root);
            primaryStage.setTitle("*******");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
