package controller.back;

import entities.Publication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.PublicationService;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class ModifierPublicationController {
    @FXML
    private TextField idTF;

    @FXML
    private TextField descriptionTF;

    @FXML
    private TextField titreTF;

    @FXML
    private ImageView imageView;

    String filepath = null, filename = null, fn = null;

    String htdocsPath = "C:/Users/Hp/Desktop/pidev/public/images/";

    FileChooser fc = new FileChooser();

    private final PublicationService publicationService = new PublicationService();

    public void initialize(URL location, ResourceBundle resources) {
        // Désactiver l'édition du champ de texte idTF
        idTF.setEditable(false);
    }

    @FXML
    void modifierImagePublication(ActionEvent event) throws SQLException, FileNotFoundException, IOException {
        File file = fc.showOpenDialog(null);
        // Shows a new file open dialog.
        if (file != null) {
            // URI that represents this abstract pathname
            imageView.setImage(new Image(file.toURI().toString()));

            filename = file.getName();
            filepath = file.getAbsolutePath();

            fn = filename;

            FileChannel source = new FileInputStream(filepath).getChannel();
            FileChannel dest = new FileOutputStream(htdocsPath + filename).getChannel();
            dest.transferFrom(source, 0, source.size());
        } else {
            System.out.println("Fichier invalide!");
        }
    }

    @FXML
    void modifier(ActionEvent event) {
        // Récupérer l'identifiant de la publication à partir du champ de texte
        int idPublication = Integer.parseInt(idTF.getText());

        // Récupérer la publication à partir de la base de données en fonction de l'identifiant
        Publication publication = publicationService.readById(idPublication);

        // Vérifier si la publication existe
        if (publication != null) {
            // Validation des champs de saisie
            String description = descriptionTF.getText();
            String titre = titreTF.getText();

            if (!isValidInput(description, titre)) {
                return;
            }

            // Mettre à jour les champs modifiés de la publication
            publication.setDescription(description);
            publication.setTitrePublication(titre);

            // Vérifier si le champ d'image n'est pas vide
            if (filename != null && !filename.isEmpty()) {
                publication.setImagePublication(filename);
            }

            // Appeler la méthode de service pour mettre à jour la publication
            publicationService.updatePublication(publication);

            // Afficher une confirmation à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Publication mise à jour avec succès.");

            alert.showAndWait();

            // Effacer les champs après la mise à jour
            descriptionTF.clear();
            titreTF.clear();
            Stage stage = (Stage) titreTF.getScene().getWindow();
            stage.close();
        } else {
            // Afficher un message d'erreur si la publication n'est pas trouvée
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("La publication n'a pas été trouvée.");
            alert.showAndWait();
        }
    }

    private boolean isValidInput(String description, String titre) {
        if (titre == null || titre.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Le titre est requis.");
            return false;
        }
        if (description == null || description.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "La description est requise.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void clear(ActionEvent event) {
        // Effacer le contenu de tous les champs de texte
        descriptionTF.clear();
        titreTF.clear();
    }

    public void setFields(String idPublication, String description, String titrePublication, String imagePublication, Date datePublication) {
        // Convertir l'identifiant en chaîne de caractères
        idTF.setText(idPublication);

        // Les autres champs sont définis de la même manière que précédemment
        titreTF.setText(titrePublication);
        descriptionTF.setText(description);

        // Chargement de l'image
        try {
            // Charger l'image à partir du fichier et l'afficher dans l'ImageView
            File file = new File(htdocsPath + imagePublication);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            } else {
                System.out.println("Le fichier spécifié n'existe pas : " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace(); // Afficher l'erreur dans la console
        }

        // Formater et afficher la date comme précédemment
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(datePublication);
        // datePublicationTF.setText(formattedDate);
    }
}
