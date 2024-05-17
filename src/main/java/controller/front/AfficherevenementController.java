/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.front;

import entities.Utilisateur;
import entities.evenement;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.evenementService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
/**
 * FXML Controller class
 *
 * @author asus
 */
public class AfficherevenementController implements Initializable {

    @FXML
    private GridPane gridabonn;

    evenementService ab=new evenementService();
    @FXML
    private TextField chercherabonnField;
    @FXML
    private Button ajouter;
    @FXML
    private Button mailButton;
    /**
     * Initializes the controller class.
     */
    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());
        afficherevenement();

    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }


    @FXML
    private void ajouterevenement(ActionEvent abonn) {
        try {
            //navigation
            Parent loader = FXMLLoader.load(getClass().getResource("/ajouterevenement.fxml"));
            chercherabonnField.getScene().setRoot(loader);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void afficherevenement(){
        try {
            List<evenement> evenements = ab.recupererevenement();
            gridabonn.getChildren().clear();
            int row = 0;
            int column = 0;
            for (int i = 0; i < evenements.size(); i++) {
                //chargement dynamique d'une interface
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/evenement.fxml"));
                AnchorPane pane = loader.load();
                //passage de parametres
                evenementController controller = loader.getController();
                controller.setUserData(userData);
                controller.setevenement(evenements.get(i));
                controller.setIdabonn(evenements.get(i).getId_ev());
                gridabonn.add(pane, column, row);
                column++;
                if (column > 1) {
                    column = 0;
                    row++;
                }
                if(evenements.get(i).getCode_participant()<=0)
                {
                    // ab.supprimerevenement(evenements.get(i));
                    controller.arreterabonn();
                }
            }
        } catch (SQLException | IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void rechercherevenement(KeyEvent abonn) {
        try {
            List<evenement> evenements = ab.chercherabonn(chercherabonnField.getText());
            gridabonn.getChildren().clear();
            int row = 0;
            int column = 0;
            for (int i = 0; i < evenements.size(); i++) {
                //chargement dynamique d'une interface
                FXMLLoader loader = new FXMLLoader(getClass().getResource("evenement.fxml"));
                AnchorPane pane = loader.load();
                //passage de parametres
                evenementController controller = loader.getController();
                controller.setUserData(userData);
                controller.setevenement(evenements.get(i));
                controller.setIdabonn(evenements.get(i).getId_ev());
                gridabonn.add(pane, column, row);
                column++;
                if (column > 1) {
                    column = 0;
                    row++;
                }
                if(evenements.get(i).getCode_participant()<=0)
                {
                    // ab.supprimerevenement(evenements.get(i));
                    controller.arreterabonn();
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void mailingg(ActionEvent abonn) throws MessagingException, AddressException, IOException, URISyntaxException {

        String link = "https://mail.google.com/mail/u/0/?tab=cm&zx=6vpa7piztdtn#chat/space/AAAACkhBi5Q";
        Desktop.getDesktop().browse(new URI(link));
    }

    @FXML
    private void trierevenement(ActionEvent abonn) throws SQLException {
        try {
            List<evenement> evenements = ab.trierabonn();
            gridabonn.getChildren().clear();
            int row = 0;
            int column = 0;
            for (int i = 0; i < evenements.size(); i++) {
                //chargement dynamique d'une interface
                FXMLLoader loader = new FXMLLoader(getClass().getResource("evenement.fxml"));
                AnchorPane pane = loader.load();
                //passage de parametres
                evenementController controller = loader.getController();
                controller.setUserData(userData);

                controller.setevenement(evenements.get(i));
                controller.setIdabonn(evenements.get(i).getId_ev());
                gridabonn.add(pane, column, row);
                column++;
                if (column > 1) {
                    column = 0;
                    row++;
                }
                if(evenements.get(i).getCode_participant()<=0)
                {
                    // ab.supprimerEvenement(evenements.get(i));
                    controller.arreterabonn();
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void retour(ActionEvent event)throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FrontMenu.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        FrontMenuController fm =fxmlLoader.getController();
        fm.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }
}
