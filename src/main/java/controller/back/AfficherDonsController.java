package controller.back;

import entities.Dons;
import entities.Utilisateur;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import service.DonsService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherDonsController {
    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());

    }
    private Dons don;

    private Stage primaryStage;
    private DonsService donsService;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setDonsService(DonsService donsService) {
        this.donsService = donsService;
    }

    public void setDon(Dons don) {
        this.don = don;
    }

    @FXML
    private TableView<Dons> donsTable;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private LineChart<String, Number> donationsChart;

    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<Dons, String> nomUserColumn;

    @FXML
    private TableColumn<Dons, String> prenomUserColumn;

    @FXML
    private TableColumn<Dons, String> emailUserColumn;

    @FXML
    private TableColumn<Dons, String> numTelColumn;

    @FXML
    private TableColumn<Dons, Integer> nbPointsColumn;

    @FXML
    private TableColumn<Dons, String> dateAjoutColumn;

    @FXML
    private TableColumn<Dons, String> etatStatutDonsColumn;
    @FXML
    private BarChart<String, Number> barChart;
    public AfficherDonsController() {
        donsService = new DonsService();
    }



    @FXML
    private void handleAjouterDon() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterDons.fxml"));
            Parent root = loader.load();
            AjouterDonsController ajouterDonsController = loader.getController();
            ajouterDonsController.initialize();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Don");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la vue pour ajouter un don.");
        }
    }



    @FXML
    void initialize() {
        nomUserColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNomUser()));
        prenomUserColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrenomUser()));
        emailUserColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailUser()));
        numTelColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNumTel()));
        nbPointsColumn.setCellValueFactory(new PropertyValueFactory<>("nbPoints"));
        dateAjoutColumn.setCellValueFactory(data -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new SimpleStringProperty(dateFormat.format(data.getValue().getDate_ajout()));
        });
        etatStatutDonsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEtatStatutDons()));
        // Ajout de la colonne "Ajouter Etat Statut"
        TableColumn<Dons, Void> colAjouterEtatStatut = new TableColumn<>("Ajouter Etat Statut");
        Callback<TableColumn<Dons, Void>, TableCell<Dons, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Dons, Void> call(final TableColumn<Dons, Void> param) {
                final TableCell<Dons, Void> cell = new TableCell<>() {
                    private final Button btn = new Button();

                    {

                        Image image = new Image(getClass().getResourceAsStream("/img/ajouter.png"));
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(16); // Ajustez la taille de l'icône si nécessaire
                        imageView.setFitHeight(16);
                        btn.setGraphic(imageView);

                        btn.setOnAction((event) -> {
                            Dons don = getTableView().getItems().get(getIndex());
                            handleAjouterEtatStatut(don); // Appeler la méthode handleAjouterEtatStatut avec le don sélectionné
                        });
                    }


                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        colAjouterEtatStatut.setCellFactory(cellFactory);
        donsTable.getColumns().add(colAjouterEtatStatut);
        addSupprimerButtonToTable();
        loadDons();



    }



    @FXML
    private void handleAjouterEtatStatut(Dons don) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEtatStatut.fxml"));
            Parent root = loader.load();
            AjouterEtatStatutController ajouterEtatStatutController = loader.getController();
            ajouterEtatStatutController.setDon(don); // Pass the 'Dons' object to the AjouterEtatStatutController
            ajouterEtatStatutController.initialize();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Etat statut de dons");
            stage.showAndWait(); // Wait until the window is closed
            loadDons(); // Reload the donations after adding the state or status
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la vue pour ajouter un état ou un statut au don.");
        }
    }



    private void addSupprimerButtonToTable() {
        TableColumn<Dons, Void> colSupprimer = new TableColumn<>("Supprimer");

        // Créer une cellule de table personnalisée avec une icône de suppression
        Callback<TableColumn<Dons, Void>, TableCell<Dons, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Dons, Void> call(final TableColumn<Dons, Void> param) {
                final TableCell<Dons, Void> cell = new TableCell<>() {
                    private final Button btn = new Button();

                    {
                        // Ajouter une icône de suppression au bouton
                        Image image = new Image(getClass().getResourceAsStream("/img/deleteimg.png"));
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(16); // Ajustez la taille de l'icône si nécessaire
                        imageView.setFitHeight(16);
                        btn.setGraphic(imageView);

                        // Définir l'action du bouton
                        btn.setOnAction((event) -> {
                            Dons don = getTableView().getItems().get(getIndex());
                            supprimerDon(don);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        // Définir la cellule de la colonne Supprimer
        colSupprimer.setCellFactory(cellFactory);

        // Ajouter la colonne Supprimer à la TableView
        donsTable.getColumns().add(colSupprimer);

        searchField.setOnAction(event -> handleSearch());

    }



    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadDons() {
        donsTable.getItems().clear();
        List<Dons> donsList = donsService.getAllDonsWithUserDetails();
        donsTable.getItems().addAll(donsList);
    }

    private void supprimerDon(Dons don) {
        boolean success = donsService.supprimerDons(don);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Suppression réussie", "Le don a été supprimé avec succès.");
            loadDons();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur de suppression", "Impossible de supprimer le don.");
        }
    }

    @FXML
    private void handleGestionDemandesDons() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionDemandesDons.fxml"));
            Parent root = loader.load();
            GestionDemandeDonsController gestionDemandeDonsController = loader.getController();
            gestionDemandeDonsController.initialize();
            Scene scene = rootPane.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la vue pour gérer les demandes de dons.");
        }}
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();

        // Filter the list of donations based on the search text
        List<Dons> filteredList = donsService.getAllDonsWithUserDetails().stream()
                .filter(don -> don.getNomUser().toLowerCase().contains(searchText) ||
                        don.getNumTel().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        // Clear the table and add the filtered list
        donsTable.getItems().clear();
        donsTable.getItems().addAll(filteredList);
    }


    @FXML
    public void retourMenuBack(ActionEvent event) throws IOException {
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
