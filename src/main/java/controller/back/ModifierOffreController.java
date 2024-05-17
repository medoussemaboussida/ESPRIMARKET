package controller.back;

import service.OffreService;
import service.ProduitService;
import entities.Offre;
import entities.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ModifierOffreController implements Initializable {

    private OffreService offreService = new OffreService();
    private ProduitService produitService = new ProduitService();

    private Offre offre;

    @FXML
    private ImageView imageView;

    @FXML
    private ListView<Produit> ListeViewAModifier;

    @FXML
    private TextField reductionTF;

    @FXML
    private TextField titreTF;

    @FXML
    private TextField descriptionTF;

    @FXML
    private DatePicker debutTF;

    @FXML
    private DatePicker finTF;

    private String filepath = null;
    private String filename = null;
    private String fn = null;

    private final String htdocsPath = "C:/Users/Hp/Desktop/pidev/public/images/";

    private FileChooser fc = new FileChooser();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<Produit> produits = FXCollections.observableArrayList(produitService.getAllProduits());
        ListeViewAModifier.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ListeViewAModifier.setItems(produits);

        reductionTF.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!keyEvent.getCharacter().matches("\\d")) {
                keyEvent.consume();
            }
        });
    }
    public void setOffreService(OffreService offreService) {
        this.offreService = offreService;
    }
    @FXML
    void modifierImage(ActionEvent event) throws SQLException, IOException {
        File file = fc.showOpenDialog(null);
        if (file != null) {
            imageView.setImage(new Image(file.toURI().toString()));

            filename = file.getName();
            filepath = file.getAbsolutePath();
            fn = filename;

            FileChannel source = new FileInputStream(filepath).getChannel();
            FileChannel dest = new FileOutputStream(htdocsPath + filename).getChannel();
            dest.transferFrom(source, 0, source.size());
            source.close();
            dest.close();
        } else {
            System.out.println("Fichier invalide!");
        }
    }

    @FXML
    void modifier(ActionEvent event) throws IOException {
        String nouveauNomOffre = titreTF.getText();
        String nouvelleDescriptionOffre = descriptionTF.getText();
        LocalDate nouvelleDateDebut = debutTF.getValue();
        LocalDate nouvelleDateFin = finTF.getValue();
        int nouvelleReductionOffre = Integer.parseInt(reductionTF.getText());

        if (nouveauNomOffre.isEmpty() || nouvelleDescriptionOffre.isEmpty() || nouvelleDateDebut == null || nouvelleDateFin == null) {
            afficherAlerteErreur("Veuillez remplir tous les champs.");
            return;
        }

        ObservableList<Produit> produitsSelectionnes = ListeViewAModifier.getSelectionModel().getSelectedItems();

        if (produitsSelectionnes.isEmpty()) {
            afficherAlerteErreur("Veuillez sélectionner au moins un produit.");
            return;
        }

        offre.setNomOffre(nouveauNomOffre);
        offre.setDescriptionOffre(nouvelleDescriptionOffre);
        offre.setDateDebut(Date.valueOf(nouvelleDateDebut));
        offre.setDateFin(Date.valueOf(nouvelleDateFin));
        offre.setReduction(nouvelleReductionOffre);
        offre.setProduits(produitsSelectionnes.stream().collect(Collectors.toList()));
        offre.setImageOffre(fn != null ? fn : offre.getImageOffre());

        offreService.updateOffre(offre);

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Succès");
        a.setContentText("Offre Modifiée");
        a.showAndWait();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherOffre.fxml"));
        Parent root = loader.load();
        AfficherOffreController controller = loader.getController();
        controller.update(null);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    public void setOffreData(Offre offre) {
        this.offre = offre;
        if (offre != null) {
            titreTF.setText(offre.getNomOffre());
            descriptionTF.setText(offre.getDescriptionOffre());
            java.util.Date dateDebutUtil = new java.util.Date(offre.getDateDebut().getTime());
            java.util.Date dateFinUtil = new java.util.Date(offre.getDateFin().getTime());
            reductionTF.setText(String.valueOf(offre.getReduction()));
            debutTF.setValue(dateDebutUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            finTF.setValue(dateFinUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            try {
                File file = new File(htdocsPath + offre.getImageOffre());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                } else {
                    System.out.println("Le fichier spécifié n'existe pas : " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void annuler() {
        Stage stage = (Stage) titreTF.getScene().getWindow();
        stage.close();
    }

    private void afficherAlerteErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
