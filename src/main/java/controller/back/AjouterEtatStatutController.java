package controller.back;


import entities.Dons;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import service.DonsService;

import java.sql.SQLException;

public class AjouterEtatStatutController {

    private DonsService donsService;
    private Dons don;

    @FXML
    private ComboBox<String> etatComboBox;

    @FXML
    private Button ajouterEtatButton;

    public void initialize() {
        // Initialiser les options de la ComboBox d'état

        this.donsService = new DonsService();
        if (!etatComboBox.getItems().contains("Reçu")) {
            etatComboBox.getItems().add("Reçu");
        }
        if (!etatComboBox.getItems().contains("En cours")) {
            etatComboBox.getItems().add("En cours");
        }

    }

    // Méthode pour définir le don sélectionné
    public void setDon(Dons don) {
        this.don = don;
    }

    // Méthode appelée lors du clic sur le bouton "Ajouter Etat"
    @FXML
    private void handleAjouterEtat() {

        String selectedEtat = etatComboBox.getValue();

        if (don != null && selectedEtat != null) {
            int idDons = don.getIdDons();
            try {
                // Ajouter l'état du statut au don sélectionné
                donsService.addEtatStatutDons(idDons, selectedEtat);
                System.out.println("État du statut de don ajouté avec succès pour le don avec l'ID " + idDons);
                // Fermer la fenêtre après avoir ajouté l'état du statut
                ajouterEtatButton.getScene().getWindow().hide();
            } catch (SQLException e) {
                e.printStackTrace();
                // Afficher une alerte en cas d'erreur
            }
        } else {
            // Afficher un message d'avertissement si le don ou l'état n'est pas sélectionné
        }
    }

    // Méthode pour définir le service des dons
    public void setDonsService(DonsService donsService) {
        this.donsService = donsService;
    }
}
