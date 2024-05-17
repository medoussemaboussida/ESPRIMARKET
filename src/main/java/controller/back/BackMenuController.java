package controller.back;
import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class BackMenuController {
    @FXML
    private Button donsMenu;

    @FXML
    private Button eventMenu;

    @FXML
    private Button offreMenu;

    @FXML
    private Button produitMenu;

    @FXML
    private Button pubMenu;

    @FXML
    private Button userMenu;
    private Utilisateur userData;
    @FXML
    private Label labelUserName;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bienvenue");
        alert.setHeaderText("Admin  " + userData.getNomUser());
        alert.setContentText("Vous êtes connecté avec succès!\n"
                + "Email: " + userData.getEmailUser());
        alert.showAndWait();
        labelUserName.setText(String.format("Admin " +userData.getNomUser()));


    }

    @FXML
    public void goToUser(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UtilisateurBack.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        UtilisateurBackController ubc=fxmlLoader.getController();
        ubc.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }
    @FXML
    public void goToProduit(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AjouterProduit.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        AjouterProduitController apc=fxmlLoader.getController();
        apc.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }
    @FXML
    public void goToPub(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ListPublication.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        ListPublication lb=fxmlLoader.getController();
        lb.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }
    @FXML
    public void goToDons(ActionEvent event)throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AfficherDons.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        AfficherDonsController adc=fxmlLoader.getController();
        adc.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }
    @FXML
    public void goToEvent(ActionEvent event)  throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Ajouterevenement.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        AjouterevenementController av=fxmlLoader.getController();
av.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }
    @FXML
    public void goToOffre(ActionEvent event)  throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AfficherOffre.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        AfficherOffreController aoc=fxmlLoader.getController();
        aoc.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }

    @FXML
    public void goToDemandeDons(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/GestionDemandesDons.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        GestionDemandeDonsController gdd=fxmlLoader.getController();
        gdd.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }

    public void toCodepromo(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CodePromoAfficher.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        AfficherCpController acc=fxmlLoader.getController();
        acc.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }

    public void back(ActionEvent event)  throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SeConnecterAdmin.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }
}
