package controller.back;

import service.CodePromoService;
import entities.CodePromo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class ModifierCpController implements Initializable {

    private CodePromoService codePromoService;
    private CodePromo codePromo;

    private final CodePromoService cps = new CodePromoService();
    @FXML
    private TextField codeTF;

    @FXML
    private DatePicker debutTF;

    @FXML
    private DatePicker finTF;

    @FXML
    private TextField reductionTF;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Enforce numeric input
        reductionTF.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!keyEvent.getCharacter().matches("\\d")) { // Regex to allow only digits
                keyEvent.consume();
            }
        });
    }
    public void processInput() {
        try {
            int intValue = Integer.parseInt(reductionTF.getText());
            // Use intValue as needed
            System.out.println("Integer value: " + intValue);
        } catch (NumberFormatException e) {
            // Handle case where input is not a valid integer
            System.err.println("Input is not a valid integer.");
        }
    }

    @FXML
    void annuler(ActionEvent event) {
        // Fermer simplement la fenêtre
        Stage stage = (Stage) codeTF.getScene().getWindow();
        stage.close();
    }

    @FXML
    void modifier(ActionEvent event) throws IOException {
        // Récupérer les nouvelles valeurs des champs de texte et des autres contrôles
        String nouveauCodePromo = codeTF.getText();
        LocalDate nouvelleDateDebut = debutTF.getValue();
        LocalDate nouvelleDateFin = finTF.getValue();
        int nouvelleReductionOffre = Integer.parseInt(reductionTF.getText());

        // Vérifier que les champs obligatoires sont remplis
        if (nouveauCodePromo.isEmpty() || nouvelleDateDebut == null || nouvelleDateFin == null || nouvelleReductionOffre == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }
        // Mettre à jour les propriétés de l'offre à modifier avec les nouvelles valeurs
        codePromo.setCode(nouveauCodePromo);
        codePromo.setDateDebut(Date.valueOf(nouvelleDateDebut));
        codePromo.setDateFin(Date.valueOf(nouvelleDateFin));
        codePromo.setReductionAssocie(nouvelleReductionOffre);
        cps.modifyCodePromo(codePromo);
        // Afficher une boîte de dialogue pour informer l'utilisateur que l'offre a été modifiée avec succès
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Succès");
        a.setContentText("Code Promo Modifiée");
        a.showAndWait();

        // Recharger la vue AfficherOffre pour refléter les modifications
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/CodePromoAfficher.fxml"));
        Parent root = loader.load();
        AfficherCpController controller = loader.getController();
        controller.update(null); // Mettez à jour la liste des offres dans la vue AfficherOffre
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();

        // Fermer la fenêtre actuelle après la modification de l'offre
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }


        public void setCodePromoData(CodePromo codePromo) {
        this.codePromo = codePromo;
        if (codePromo != null) {
            codeTF.setText(codePromo.getCode());
            java.util.Date dateDebutUtil = new java.util.Date(codePromo.getDateDebut().getTime());
            java.util.Date dateFinUtil = new java.util.Date(codePromo.getDateFin().getTime());
            reductionTF.setText(String.valueOf(codePromo.getReductionAssocie()));
            // Convertir java.util.Date en LocalDate
            debutTF.setValue(dateDebutUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            finTF.setValue(dateFinUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
    }
    public void setCodePromoService(CodePromoService codePromoService) {
        this.codePromoService = codePromoService;
    }

}