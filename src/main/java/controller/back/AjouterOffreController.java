package controller.back;

import entities.Utilisateur;
import service.OffreService;
import service.ProduitService;
import entities.Offre;
import entities.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AjouterOffreController{

    private final OffreService os = new OffreService();
    private ProduitService produitService = new ProduitService();  ;

    @FXML
    private TextField reduction;

    @FXML
    private DatePicker debut;
    @FXML
    private TextField description;
    @FXML
    private DatePicker fin;
    @FXML
    private TextField nom;
    @FXML
    private ImageView imageView;

    @FXML
    private ListView<Produit> produitsListView;

    String filepath = null, filename = null, fn = null;

    String uploads = "C:/Users/ghassen/Desktop/";

    FileChooser fc = new FileChooser();


    @FXML
    void ajouterImage(ActionEvent event) throws IOException {
        // Chemin vers le répertoire htdocs de XAMPP
        String htdocsPath = "C:/Users/Hp/Desktop/pidev/public/images/"; // Changez le chemin selon votre installation

        File file = fc.showOpenDialog(null);
        // Shows a new file open dialog.
        if (file != null) {
            // URI that represents this abstract pathname
            imageView.setImage(new Image(file.toURI().toString()));

            filename = file.getName();
            filepath = file.getAbsolutePath();

            fn = filename;

            // Chemin de destination dans le répertoire htdocs
            String destinationPath = htdocsPath + "/" + filename;

            // Copie du fichier vers le répertoire htdocs
            Files.copy(Paths.get(filepath), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
        } else {
            System.out.println("Fichier invalide!");
        }
    }

    @FXML
    void annuler(ActionEvent event) {
        Stage stage = (Stage) nom.getScene().getWindow();
        stage.close();
    }



    @FXML
    void initialize() {
         //Charger la liste des produits disponibles dans la ListView
        ObservableList<Produit> produits = FXCollections.observableArrayList(produitService.getAllProduits());
        produitsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        produitsListView.setItems(produits);
        // Enforce numeric input
        reduction.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!keyEvent.getCharacter().matches("\\d")) { // Regex to allow only digits
                keyEvent.consume();
            }
        });
    }
    public void processInput() {
        try {
            int intValue = Integer.parseInt(reduction.getText());
            // Use intValue as needed
            System.out.println("Integer value: " + intValue);
        } catch (NumberFormatException e) {
            // Handle case where input is not a valid integer
            System.err.println("Input is not a valid integer.");
        }
    }
    @FXML
    void ajouter(ActionEvent event) throws IOException {

        String nomOffre = nom.getText();
        String descriptionOffre = description.getText();
        LocalDate dateDebut = debut.getValue();
        LocalDate dateFin = fin.getValue();
        System.out.println(reduction.getText().isEmpty());

        if (nomOffre.isEmpty() || descriptionOffre.isEmpty() || dateDebut == null || dateFin == null || reduction.getText().isEmpty() || filename ==null  ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

       /**** condition of date debut et date fin **/
        if (dateDebut.isAfter(dateFin)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("date debut doivent etre avant date fin");
            alert.showAndWait();
            return;
        }
        /**** condition of date debut et date fin **/

        int reductionOffre = Integer.parseInt(reduction.getText());

        // Récupérer les produits sélectionnés par l'utilisateur
        ObservableList<Produit> produitsSelectionnes = produitsListView.getSelectionModel().getSelectedItems();

        if (produitsSelectionnes.isEmpty()) {
            afficherAlerteErreur("Veuillez sélectionner au moins un produit.");
            return;
        }

        ObservableList<Produit> selectedItems = produitsListView.getSelectionModel().getSelectedItems();

        Offre nouvelleOffre = new Offre(nomOffre, descriptionOffre, Date.valueOf(dateDebut), Date.valueOf(dateFin),reductionOffre);
        List<Produit> modifiedList = selectedItems.stream().collect(Collectors.toList());
        nouvelleOffre.setProduits(modifiedList);
        nouvelleOffre.setImageOffre(filename);

        os.addOffre(nouvelleOffre);

        //
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Succès");
        a.setContentText("Offre Ajoutée");
        a.showAndWait();

        // Fermer la fenêtre actuelle après l'ajout de l'offre
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
    private void afficherAlerteErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}



