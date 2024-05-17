package controller.front;

import entities.Dons;
import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.DonsService;
import service.UtilisateurService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FaireDonsController implements Initializable {

    @FXML
    private Label donsLabel;


    @FXML
    private TableView<Dons> donsTable;

    @FXML
    private TableColumn<Dons, String> nomUserColumn;

    @FXML
    private TableColumn<Dons, String> prenomUserColumn;


    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());
        loadDons(userData.getIdUser());

        // Afficher les points disponibles dans le label
        int nbPointsDisponibles = userData.getNbPoints();
        pointsDisponiblesLabel.setText(String.valueOf(nbPointsDisponibles));
    }

    @FXML
    private TableColumn<Dons, Integer> nbPointsColumn;

    @FXML
    private TableColumn<Dons, String> dateAjoutColumn;

    @FXML
    private TableColumn<Dons, String> etatStatutDonsColumn;


    @FXML
    private Label pointsLabel;

    @FXML
    private TextField donPointsField;

    private DonsService donsService;
    private UtilisateurService userService;





    public FaireDonsController() {
        donsService = new DonsService();
        userService = new UtilisateurService();
    }
    @FXML
    private Label pointsDisponiblesLabel;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
    }




    public void setUser(Utilisateur user) {
        this.userData = user;
        updatePointsLabel();
    }

    private void updatePointsLabel() {
        int pointsUtilisateur = userService.getUserById(userData.getIdUser()).getNbPoints();
        pointsLabel.setText("Points disponibles : " + pointsUtilisateur);
    }

    @FXML
    private void handleSupprimerDon(Dons don) {
        boolean success = donsService.supprimerDons(don);
        if (success) {
            // Mettez à jour l'affichage ou fournissez un retour d'information approprié
            System.out.println("Don supprimé avec succès");
        } else {
            // Gérer le cas où la suppression a échoué
            System.out.println("Erreur lors de la suppression du don");
        }
        loadDons(userData.getIdUser());
    }



    @FXML
    private void handleAjouterDon(ActionEvent event) {
        // Récupérer l'utilisateur avec l'ID 1 (ou autre ID approprié)

        String donPointsText = donPointsField.getText();
        if (donPointsText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Champ vide", "Veuillez saisir le nombre de points.");
            return;
        }

        try {
            int donPoints = Integer.parseInt(donPointsText);

            if (donPoints <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Points invalides", "Veuillez entrer un nombre de points valide.");
                return;
            }

            if (donPoints > userData.getNbPoints()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Points insuffisants", "Vous n'avez pas suffisamment de points pour effectuer ce don.");
                return;
            }

            int remainingPoints = donsService.addDonsWithStatus(userData, donPoints);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Don effectué", "Votre don a été ajouté avec succès.");

            // Mettre à jour l'affichage des points dans l'interface utilisateur
            remainingPoints = userData.getNbPoints() - donPoints;
            pointsDisponiblesLabel.setText(String.valueOf(remainingPoints));
            loadDons(1); // Remplacez 1 par l'ID de l'utilisateur approprié
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture", "Impossible d'ouvrir la vue pour supprimer un don.");
        }
    }


    private void showModifierDialog(Dons don) {
        // Créer une boîte de dialogue pour modifier les points du don
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Modifier les points du don");
        dialog.setHeaderText(null);
        dialog.setContentText("Entrez le nouveau nombre de points:");

        // Afficher la boîte de dialogue et attendre la saisie de l'utilisateur
        Optional<String> result = dialog.showAndWait();

        // Traiter la saisie de l'utilisateur
        result.ifPresent(newPoints -> {
            try {
                int newPointsValue = Integer.parseInt(newPoints);
                // Mettre à jour les points du don dans la base de données
                int oldPoints = don.getNbPoints();
                don.setNbPoints(newPointsValue);
                donsService.updateDons(don); // Mettre à jour le don dans la base de données

                // Mettre à jour les points de l'utilisateur
                Utilisateur user = userService.getUserById(don.getIdUser());
                int updatedPoints = user.getNbPoints() + oldPoints - newPointsValue;
                userService.updateUserPoints(userData.getIdUser(), updatedPoints);

                // Mettre à jour l'affichage des points disponibles
                pointsDisponiblesLabel.setText(String.valueOf(updatedPoints));

                // Mettre à jour l'affichage
                loadDons(userData.getIdUser());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Points invalides", "Veuillez entrer un nombre de points valide.");
            }
        });
    }



    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void loadDons(int userId) {
        if (userData != null) {
            VBox vbox = new VBox(); // Conteneur pour les boutons et le texte

            List<Dons> donsList = donsService.getDonsByUserId(userData.getIdUser());
            ListView<HBox> listView = new ListView<>(); // Créer une ListView pour afficher les HBox

            for (Dons don : donsList) {
                HBox hbox = new HBox(); // Conteneur pour un bouton et le texte du don

                // Créer le texte du don
                Label donLabel = new Label("L'état de votre don de " + don.getNbPoints() +
                        " points effectué le " + don.getDate_ajout() +
                        " est " + don.getEtatStatutDons());
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                if (don.getEtatStatutDons() != null) {
                    Button btnUpdate = new Button(); // Ajouter le bouton "Modifier"

                    // Ajouter une icône au bouton
                    Image image = new Image(getClass().getResourceAsStream("/img/modifier.png"));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(16); // Ajustez la taille de l'icône si nécessaire
                    imageView.setFitHeight(16);
                    btnUpdate.setGraphic(imageView);

                    // Définir l'action du bouton
                    btnUpdate.setOnAction(event -> {
                        showModifierDialog(don);
                    });

                    // Créer un conteneur pour les boutons
                    HBox buttonsContainer = new HBox(btnUpdate);

                    // Ajouter les éléments dans l'ordre souhaité à la HBox
                    hbox.getChildren().addAll(donLabel, spacer, buttonsContainer);
                } else {
                    // Si l'état est "Reçu", n'ajoutez pas le bouton de modification
                    hbox.getChildren().addAll(donLabel, spacer); // Ajouter seulement le label
                }

                VBox.setMargin(hbox, new Insets(0, 0, 10, 0)); // 10 est l'espacement entre les lignes
                vbox.getChildren().add(hbox);

                // Ajouter la HBox actuelle à la ListView
                listView.getItems().add(hbox);
            }

            // Afficher la ListView avec les HBox dans le label "donsLabel"
            donsLabel.setGraphic(listView);
        } else {
            System.out.println("Utilisateur non trouvé");
        }
    }


    @FXML
    public void retourMenuFront(ActionEvent event) throws IOException {
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
