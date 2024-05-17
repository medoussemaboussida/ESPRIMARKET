package controller.front;

import service.UtilisateurService;
import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AjouterUtilisateurController {
    private final UtilisateurService us = new UtilisateurService();
    public MenuButton rolePick;

    @FXML
    private TextField emailUserTF;

    @FXML
    private TextField mdpTF;

    @FXML
    private TextField nbPoints;

    @FXML
    private TextField nomUserTF;

    @FXML
    private TextField numTel;

    @FXML
    private TextField prenomUserTF;

    @FXML
    private TextField role;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_NUMBER_REGEX = "\\d{8}";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z]).{4,}$";



    @FXML
    void add(ActionEvent event) {
        String nom = nomUserTF.getText();
        String prenom = prenomUserTF.getText();
        String email = emailUserTF.getText();
        String pwd = (mdpTF != null) ? mdpTF.getText() : "";
        int tel;

        try {
            tel = Integer.parseInt(numTel.getText());
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid value for Phone Number. Please enter a valid integer.");
            return;
        }

        int nbrPoint = 0;
        String Role = "Client";

        System.out.println(Role);

        Utilisateur user = new Utilisateur(nom, prenom, email, pwd, nbrPoint, tel, Role);

        try {
            if (isValidEmail(email) && isValidPhoneNumber(numTel.getText()) && isValidPassword(pwd)) {
                us.ajouterPersonne(user);
                showAlert("Success!", "New user has been added");
    navigatetoLogin(event);

            } else if (!isValidEmail(email)){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention");
                alert.setHeaderText(null);
                alert.setContentText("Format email  non valide");
                alert.showAndWait();
            } else if (!isValidPhoneNumber(numTel.getText())){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention");
                alert.setHeaderText(null);
                alert.setContentText("Format numero  non valide, must contain 8 digits");
                alert.showAndWait();
            } else if (!isValidPassword(pwd)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention");
                alert.setHeaderText(null);
                alert.setContentText("Password must contain at least one uppercase letter and minimum of 4 characters");
                alert.showAndWait();

            }
        } catch (SQLException e) {
            showAlert("SQL Exception", e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert((title.equals("Error") || title.equals("SQL Exception")) ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateToListeUtilisateurs() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeUtilisateurs.fxml"));
        Parent root = loader.load();
        ListeUtilisateursController lu = loader.getController();

        lu.setNomUserTF(nomUserTF.getText());
        lu.setPrenomUserTF(prenomUserTF.getText());
        lu.setListTF(us.readAll().toString());

        nomUserTF.getScene().setRoot(root);
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches(PHONE_NUMBER_REGEX);
    }

    private boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public void navigatetoLogin(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Seconnecter.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SeconnecterController lu = loader.getController();
        nomUserTF.getScene().setRoot(root);
    }
    }

