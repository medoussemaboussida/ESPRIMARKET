package controller.back;

import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import service.DonsService;
import service.UtilisateurService;

import java.util.List;


public class AjouterDonsController {

    @FXML
    private ComboBox<Utilisateur> userComboBox;

    @FXML
    private TextField donPointsField;

    private DonsService donsService;
    private UtilisateurService userService;

    public AjouterDonsController() {
        donsService = new DonsService();
        userService = new UtilisateurService();
    }

    @FXML
    void initialize() {
        // Charger la liste des utilisateurs et les afficher dans le ComboBox
        List<Utilisateur> userList = userService.getAllUsers();
        ObservableList<Utilisateur> observableUserList = FXCollections.observableArrayList(userList);
        userComboBox.setItems(observableUserList);

        // Définir la manière dont les informations de l'utilisateur sont affichées dans le ComboBox
        userComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Utilisateur user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getNomUser() + " " + user.getPrenomUser() + " - " + user.getEmailUser() + " - Points: " + user.getNbPoints());
                }
            }
        });

        // Définir la manière dont l'utilisateur est affiché lorsqu'il est sélectionné dans le ComboBox
        userComboBox.setConverter(new StringConverter<Utilisateur>() {
            @Override
            public String toString(Utilisateur user) {
                if (user == null) {
                    return "";
                } else {
                    return user.getNomUser() + " " + user.getPrenomUser() + " - " + user.getEmailUser() + " - Points: " + user.getNbPoints();
                }
            }

            @Override
            public Utilisateur fromString(String string) {
                return null; // Nous n'avons pas besoin de cela pour l'instant
            }
        });
    }

    @FXML
    private void handleAjouterDon() {
        Utilisateur selectedUser = userComboBox.getValue();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un utilisateur.");
            return;
        }

        int donPoints;
        try {
            donPoints = Integer.parseInt(donPointsField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir un nombre entier pour les points de don.");
            return;
        }

        donsService.addDonsWithStatus(selectedUser, donPoints);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Don ajouté avec succès pour l'utilisateur " + selectedUser.getIdUser());

        donPointsField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

