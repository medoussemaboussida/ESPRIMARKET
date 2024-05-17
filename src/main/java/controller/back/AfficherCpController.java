package controller.back;

import entities.Utilisateur;
import service.CodePromoService;
import entities.CodePromo;
import entities.Offre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.sql.ConnectionPoolDataSource;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AfficherCpController implements Initializable {
    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());

    }
    private CodePromoService codePromoService =new CodePromoService();

    @FXML
    private TableColumn<CodePromo, Void> actionCol;

    @FXML
    private TableColumn<CodePromo, String> rCode;

    @FXML
    private TableColumn<CodePromo, Date> rDebut;

    @FXML
    private TableColumn<CodePromo, Date> rFin;

    @FXML
    private TableColumn<CodePromo, Integer> rId;

    @FXML
    private TableView<CodePromo> rList;

    @FXML
    private TableColumn<CodePromo, Integer> rReduction;
    @FXML
    private TextField searchField;

    @FXML
    private Button triReduction;

    @FXML
    private Button trieDate;

    private ObservableList<CodePromo> originalCodePromoList;
    private ObservableList<CodePromo> codePromosList;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {



            // Récupérer les données des code promo depuis le service
            CodePromoService codePromoService = new CodePromoService();
            List<CodePromo> codePromos = codePromoService.readCodePromo();
            configureActionColumn();

            // Créer une ObservableList à partir des code promos récupérées
            ObservableList<CodePromo> codePromoList = FXCollections.observableArrayList(codePromos);

            originalCodePromoList = FXCollections.observableArrayList(codePromos);
            codePromosList = FXCollections.observableArrayList(codePromos);

            // Associer les propriétés des objets code promos aux colonnes de la TableView
            rId.setCellValueFactory(new PropertyValueFactory<>("idCode"));
            rCode.setCellValueFactory(new PropertyValueFactory<>("code"));
            rReduction.setCellValueFactory(new PropertyValueFactory<>("reductionAssocie"));
            rDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
            rFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));

            // Définir les données de la TableView
            rList.setItems(codePromoList);

            // Définir les données de la TableView
            rList.setItems(codePromosList);
            searchField.setOnAction(event -> search()); // Appelle la méthode search() lorsque "Entrée" est pressé
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                search(); // Appelle la méthode search() chaque fois que le texte change
            });

        } catch (Exception e) {
            // Gérer les exceptions
            Logger.getLogger(AfficherCpController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    private void configureActionColumn() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox pane = new HBox(editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    CodePromo codePromo = getTableView().getItems().get(getIndex());
                    // Logique pour modifier l'offre
                    openModifierCodePromoDialog(codePromo);
                });
                deleteButton.setOnAction(event -> {
                    CodePromo codePromo = getTableView().getItems().get(getIndex());
                    // Logique pour supprimer l'offre
                    deleteCodePromo(codePromo);
                    // Refresh the list
                    loadCodePromoData();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

            @FXML
            private void openModifierCodePromoDialog(CodePromo codePromo) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/CodePromoModifier.fxml"));
                    Parent root = loader.load();

                    ModifierCpController controller = loader.getController();
                    controller.setCodePromoData(codePromo);
                    controller.setCodePromoService(codePromoService);  // Injecter l'instance de OffreService

                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("Modifier code promo");
                    stage.setScene(new Scene(root));
                    stage.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                    // Gérer l'exception
                }
            }

            @FXML
            private void deleteCodePromo(CodePromo codePromo) {
                try {
                    CodePromoService codePromoService = new CodePromoService();
                    codePromoService.deleteCodePromo(codePromo.getIdCode());

                    // Actualiser la liste des offres après la suppression
                    loadCodePromoData();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Gérer l'exception
                }
            }

            private void loadCodePromoData() {
                try {
                    CodePromoService codePromoService = new CodePromoService();
                    List<CodePromo> codePromos = codePromoService.readCodePromo();

                    ObservableList<CodePromo> codePromoList = FXCollections.observableArrayList(codePromos);
                    rList.setItems(codePromoList);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Gérer l'exception
                }
            }


            @FXML
            void ajouter(MouseEvent event) {
                try {
                    Parent parent = FXMLLoader.load(getClass().getResource("/CodePromoAjouter.fxml"));
                    Scene scene = new Scene(parent);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.initStyle(StageStyle.UTILITY);
                    stage.show();
                } catch (IOException ex) {
                    Logger.getLogger(AfficherCpController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            @FXML
            void close(MouseEvent event)  throws IOException {
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
    ObservableList<CodePromo> ListeCodePromo = FXCollections.observableArrayList();

            @FXML
            void update(MouseEvent event) {
                try {
                    // Clear the existing data in the ObservableList
                    ListeCodePromo.clear();

                    // Retrieve data from the database using your OffreService
                    codePromoService = new CodePromoService();
                    List<CodePromo> codePromos = codePromoService.readCodePromo();

                    // Add the retrieved data to the ObservableList
                    ListeCodePromo.addAll(codePromos);

                    // Set the items of the TableView to the updated data
                    rList.setItems(ListeCodePromo);
                    // Mettez à jour la liste des offres dans la TableView
                    loadCodePromoData();
                } catch (Exception e) {
                    // Handle any exceptions
                    Logger.getLogger(AfficherOffreController.class.getName()).log(Level.SEVERE, null, e);
                }
            }
    @FXML
    void search() {
        String keyword = searchField.getText().trim().toLowerCase();
        List<CodePromo> filteredList = originalCodePromoList.stream()
                .filter(codePromo -> codePromo.getCode().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
        codePromosList.setAll(filteredList); // Mettez à jour codePromosList avec la nouvelle liste filtrée
    }
    @FXML
    void trierParReduction(MouseEvent event) {
        // Tri de la liste des codes promo par réduction
        codePromosList.sort(Comparator.comparingInt(CodePromo::getReductionAssocie));

        // Pas besoin de mettre à jour la TableView, la liste est déjà triée
    }

    @FXML
    void trierParDate(MouseEvent event) {
        // Tri de la liste des codes promo par date de début
        codePromosList.sort(Comparator.comparing(CodePromo::getDateDebut));

        // Pas besoin de mettre à jour la TableView, la liste est déjà triée
    }


}