package controller.front;

import controller.back.UtilisateurBackController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.UtilisateurService;
import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;

public class EditerleprofilController {
    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());
        nomUser.setText(userData.getNomUser());
        prenomUser.setText(userData.getPrenomUser());
        emailUser.setText(userData.getEmailUser());
        mdp.setText(userData.getMotDePasse());
        numTel.setText(String.valueOf(userData.getNumeroTel()));
        nbPoints.setText(String.valueOf(userData.getNbPoints()));
        role.setText(userData.getRole());

    }

    @FXML
    private TextField emailUser;

    @FXML
    private int idUser;

    @FXML
    private TextField mdp;

    @FXML
    private TextField nbPoints;

    @FXML
    private TextField nomUser;

    @FXML
    private TextField numTel;

    @FXML
    private TextField prenomUser;

    @FXML
    private TextField role;
    private Utilisateur utilisateur;
    @FXML
    private TextField idTextField;

// ...




    public void initialize() {


    }

    @FXML
    public void update(ActionEvent event) {
        try {
            String nom = nomUser.getText();
            String prenom = prenomUser.getText();
            String email = emailUser.getText();
            String mdpValue = mdp.getText();
            int nbPointsValue = Integer.parseInt(nbPoints.getText());
            int numTelValue = Integer.parseInt(numTel.getText());
            String roleValue = role.getText();

            Utilisateur utilisateur = new Utilisateur(nom, prenom, email, mdpValue, numTelValue, nbPointsValue, roleValue);

            UtilisateurService utilisateurService = new UtilisateurService(); // Créez une instance de UtilisateurService
            utilisateurService.update(utilisateur);

            showAlert("Success!", "Profile updated successfully", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid input. Please enter valid numeric values.", Alert.AlertType.ERROR);
        }
    }


    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void retourMenufront(ActionEvent event)throws IOException {
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

    public void deleteAccount(ActionEvent event) {
        try {
            UtilisateurService utilisateurService = new UtilisateurService();
            utilisateurService.delete(userData); // Assuming userData contains the user to be deleted

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("do you really want to delete your account ?");
            alert.showAndWait();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Seconnecter.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();
            stage.show();


        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Format numero  non valide, must contain 8 digits");
            alert.showAndWait();
        }
    }
}






