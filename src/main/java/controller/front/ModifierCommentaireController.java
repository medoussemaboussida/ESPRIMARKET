package controller.front;

import entities.commentaire;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.CommentaireService;

public class ModifierCommentaireController {

    @FXML
    private TextField descriptionModifierCM;

    private commentaire commentaire;
    private CommentaireService commentaireService;

    public void initData(commentaire commentaire) {
        this.commentaire = commentaire;
        descriptionModifierCM.setText(commentaire.getDescriptionCommentaire());
        commentaireService = new CommentaireService();
    }

    @FXML
    void modifier(ActionEvent event) {
        // Récupérer la nouvelle description du commentaire
        String newDescription = descriptionModifierCM.getText();

        // Vérifier si la nouvelle description n'est pas vide
        if (newDescription.isEmpty()) {
            // Afficher une alerte si la description est vide
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer une description pour le commentaire.");
            alert.showAndWait();
            return;
        }

        // Mettre à jour la description du commentaire
        commentaire.setDescriptionCommentaire(newDescription);

        // Appeler le service pour mettre à jour le commentaire
        commentaireService.updateCommentaire(commentaire);

        // Afficher une confirmation à l'utilisateur
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Le commentaire a été mis à jour avec succès.");
        alert.showAndWait();

        // Fermer la fenêtre de modification après la mise à jour
        Stage stage = (Stage) descriptionModifierCM.getScene().getWindow();
        stage.close();
    }

    @FXML
    void clear(ActionEvent event) {
        // Effacer le champ de texte
        descriptionModifierCM.clear();
    }

}
