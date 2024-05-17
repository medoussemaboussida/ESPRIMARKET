package controller.back;

import controller.front.FrontMenuController;
import controller.front.confirmController;
import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.UtilisateurService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeconnecterAdminController {

    public Label alertLabel;
    public Label alertLabel1;
    @FXML
    private TextField email;

    @FXML
    private TextField mdp;

    @FXML
    private Button se_connecter;

    @FXML
    private Connection conn;
    private Statement ste;
    private PreparedStatement pst;

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@(.+)$";


    @FXML
    void connexion(ActionEvent event) {
        String textemail = email.getText();
        String textmdp = mdp.getText();

        if (textemail.isEmpty()) {
            alertLabel.setVisible(true);
            alertLabel1.setVisible(false);
        } else if (textmdp.isEmpty()) {
            alertLabel1.setVisible(true);
            alertLabel.setVisible(false);
        } else if (!isValidEmail(textemail)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Format email non valide");
            alert.showAndWait();
        }

        UtilisateurService utilisateurService = new UtilisateurService();
        Utilisateur loggedInUser = utilisateurService.loginAdmin(textemail, textmdp);

        if (loggedInUser != null) {
            // Print or display user information as needed
            System.out.println("User ID: " + loggedInUser.getIdUser());
            System.out.println("User Name: " + loggedInUser.getNomUser());
            System.out.println("User Email: " + loggedInUser.getEmailUser());
            System.out.println("User Email: " + loggedInUser.getRole());
            // Add more fields as needed
            // Load the new scene and pass user data
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BackMenu.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Access the controller and set user data
         //   profileController homeInterfaceController = loader.getController();
           // homeInterfaceController.setUserData(loggedInUser);
          BackMenuController bm =loader.getController();
            bm.setUserData(loggedInUser);


            // Show the new scene
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current scene if needed
            ((Stage) mdp.getScene().getWindow()).close();

            // You can also pass this information to another scene or controller if needed
            System.out.println("Login successful!");
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Email ou mot de passe incorrect.");
            alert.showAndWait();
        }
    }


    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void navigateToSignup(ActionEvent actionEvent) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterAdmin.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //AjouterUtilisateurController lu = loader.getController();



        mdp.getScene().setRoot(root);
    }

    public void ResetPassword(ActionEvent actionEvent) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Email.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        confirmController lu = loader.getController();



        mdp.getScene().setRoot(root);
    }

    public void goToClient(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Seconnecter.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        mdp.getScene().setRoot(root);
    }
}
