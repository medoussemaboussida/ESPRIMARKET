package controller.back;

import entities.DemandeDons;
import entities.Utilisateur;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import service.DemandeDonsService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GestionDemandeDonsController {
    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());

    }
    @FXML
    private TableView<DemandeDons> demandeDonsTableView;

    @FXML
    private TableColumn<DemandeDons, String> contenuColumn;

    @FXML
    private TableColumn<DemandeDons, String> datePublicationColumn;

    @FXML
    private TableColumn<DemandeDons, Integer> nbPointsColumn;

    @FXML
    private TableColumn<DemandeDons, String> nomUserColumn;

    @FXML
    private TableColumn<DemandeDons, String> prenomUserColumn;

    @FXML
    private ComboBox<String> triComboBox;
    @FXML
    private AnchorPane rootPane;


    private DemandeDonsService demandeDonsService;
    private Stage primaryStage;
    private Utilisateur utilisateurConnecte; // Utilisateur connecté

    public GestionDemandeDonsController() {
        demandeDonsService = new DemandeDonsService();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setUtilisateurConnecte(Utilisateur utilisateurConnecte) {
        this.utilisateurConnecte = utilisateurConnecte;
    }

    @FXML
    void initialize() {
        // Remplissage du ComboBox de tri
        ObservableList<String> options = FXCollections.observableArrayList(" Ordre Croissant", "Ordre Décroissant");
        triComboBox.setItems(options);

        // Configuration des colonnes existantes
        nomUserColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNomUser()));
        prenomUserColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrenomUser()));
        contenuColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContenu()));
        datePublicationColumn.setCellValueFactory(data -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new SimpleStringProperty(dateFormat.format(data.getValue().getDatePublication()));
        });
        nbPointsColumn.setCellValueFactory(new PropertyValueFactory<>("nbPoints"));

        // Écouteur pour le ComboBox de tri
        triComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals(" Ordre Croissant")) {
                    trierParDatePublicationCroissant();
                } else {
                    trierParDatePublicationDecroissant();
                }
            }
        });

        // Création d'une colonne de boutons pour supprimer une demande
        TableColumn<DemandeDons, Void> deleteButtonColumn = new TableColumn<>("Action");
        deleteButtonColumn.setPrefWidth(100);
        deleteButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button();

            {
                // Ajouter une icône de suppression au bouton
                Image image = new Image(getClass().getResourceAsStream("/img/deleteimg.png"));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(16); // Ajustez la taille de l'icône si nécessaire
                imageView.setFitHeight(16);
                deleteButton.setGraphic(imageView);

                // Définir l'action du bouton
                deleteButton.setOnAction((event) -> {
                    DemandeDons demande = getTableView().getItems().get(getIndex());
                    if (demande != null) {
                        deleteDemande(demande);
                    } else {
                        // Gérer le cas où aucun élément n'est sélectionné
                    }
                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        // Création d'une colonne de boutons pour ajouter des points à une demande
        TableColumn<DemandeDons, Void> ajouterPointsColumn = new TableColumn<>("Ajouter Points");
        ajouterPointsColumn.setPrefWidth(100);
        ajouterPointsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button ajouterButton = new Button();

            {
                // Ajouter une icône à bouton
                Image image = new Image(getClass().getResourceAsStream("/img/ajouterp.png"));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(16); // Ajustez la taille de l'icône si nécessaire
                imageView.setFitHeight(16);
                ajouterButton.setGraphic(imageView);

                // Définir l'action du bouton
                ajouterButton.setOnAction(event -> {
                    DemandeDons demande = getTableView().getItems().get(getIndex());
                    if (demande != null) {
                        ajouterPoints();
                    } else {
                        // Gérer le cas où aucun élément n'est sélectionné
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(ajouterButton);
                }
            }
        });

        // Ajout des colonnes après les autres colonnes existantes
        demandeDonsTableView.getColumns().addAll( deleteButtonColumn, ajouterPointsColumn);

        loadDemandes();
    }



    private void loadDemandes() {
        List<DemandeDons> demandes = demandeDonsService.getDemandesAvecUsers();
        ObservableList<DemandeDons> observableList = FXCollections.observableArrayList(demandes);
        demandeDonsTableView.setItems(observableList);
        demandeDonsTableView.getSelectionModel().clearSelection();
    }

    private void deleteDemande(DemandeDons demande) {
        // Confirmer la suppression avec une boîte de dialogue
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation de suppression");
        confirmationDialog.setHeaderText("Voulez-vous vraiment supprimer cette demande ?");
        confirmationDialog.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Appeler le service pour supprimer la demande
            if (demandeDonsService.supprimerDemandes(demande.getIdDemande())) {
                // Recharger les demandes après la suppression
                loadDemandes();
            } else {
                afficherAlerte("Erreur", "Erreur lors de la suppression de la demande.");
            }
        }
    }
    @FXML
    private void ajouterPoints() {
        // Récupérer la demande sélectionnée dans la TableView
        DemandeDons demande = demandeDonsTableView.getSelectionModel().getSelectedItem();

        // Vérifier si une demande est sélectionnée
        if (demande != null) {
            // Créer une boîte de dialogue pour saisir le nombre de points à ajouter
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Ajouter des points");
            dialog.setHeaderText("Saisissez le nombre de points à ajouter à la demande.");
            dialog.setContentText("Nombre de points :");

            // Afficher la boîte de dialogue et attendre la réponse de l'utilisateur
            Optional<String> result = dialog.showAndWait();

            // Traiter la réponse de l'utilisateur
            result.ifPresent(points -> {
                try {
                    // Convertir la saisie en entier
                    int pointsToAdd = Integer.parseInt(points);

                    // Ajouter les points à la demande
                    demande.setNbPoints(demande.getNbPoints() + pointsToAdd);

                    // Mettre à jour la TableView pour refléter les modifications
                    demandeDonsTableView.refresh();

                    // Afficher une confirmation à l'utilisateur
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Points ajoutés");
                    alert.setHeaderText(null);
                    alert.setContentText("Les points ont été ajoutés avec succès à la demande.");
                    alert.showAndWait();
                } catch (NumberFormatException e) {
                    // En cas d'erreur de format de saisie
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Veuillez saisir un nombre valide.");
                    alert.showAndWait();
                }
            });
        } else {
            // Si aucune demande n'est sélectionnée
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune demande sélectionnée");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une demande avant d'ajouter des points.");
            alert.showAndWait();
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void trierParDatePublicationCroissant() {
        demandeDonsTableView.getItems().sort(Comparator.comparing(DemandeDons::getDatePublication));
    }

    private void trierParDatePublicationDecroissant() {
        demandeDonsTableView.getItems().sort(Comparator.comparing(DemandeDons::getDatePublication).reversed());
    }




    @FXML
    private void handleGestionDons() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDons.fxml"));
            Parent root = loader.load();
            AfficherDonsController afficherDonsController = loader.getController();
            afficherDonsController.initialize();
            Scene scene = rootPane.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la vue pour gérer les demandes de dons.");
        }}
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private TextField searchField;

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();

        // Filtrer la liste des demandes de dons en fonction du texte de recherche
        List<DemandeDons> filteredList = demandeDonsService.getDemandesAvecUsers().stream()
                .filter(demande -> demande.getNomUser().toLowerCase().contains(searchText) ||
                        demande.getPrenomUser().toLowerCase().contains(searchText) ||
                        demande.getContenu().toLowerCase().contains(searchText) ||
                        demande.getDatePublication().toString().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        // Effacer la TableView et ajouter la liste filtrée
        demandeDonsTableView.getItems().clear();
        demandeDonsTableView.getItems().addAll(filteredList);
    }


    @FXML
    public void retourMenuBack2(ActionEvent event)throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/BackMenu.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        BackMenuController bm=fxmlLoader.getController();
        bm.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }
}
