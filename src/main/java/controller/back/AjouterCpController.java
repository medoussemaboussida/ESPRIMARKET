package controller.back;

import service.CodePromoService;
import entities.CodePromo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class AjouterCpController {
    @FXML
    private TextField codeTF;

    @FXML
    private DatePicker debutTF;

    @FXML
    private DatePicker finTF;

    @FXML
    private TextField reductionTF;

    private final CodePromoService cps = new CodePromoService();

    @FXML
    void initialize() {

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
    void ajouter(ActionEvent event) throws IOException {

        String code = codeTF.getText();
        LocalDate dateDebut = debutTF.getValue();
        LocalDate dateFin = finTF.getValue();
        int reduction = Integer.parseInt(reductionTF.getText());

        System.out.println(reductionTF.getText().isEmpty());

        if (code.isEmpty() || dateDebut == null || dateFin == null || reductionTF.getText().isEmpty()) {
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


        CodePromo nouvelleCode = new CodePromo(reduction, code, Date.valueOf(dateDebut), Date.valueOf(dateFin));
        cps.addCodePromo(nouvelleCode);
        //
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Succès");
        a.setContentText("Code Promo Ajoutée");
        a.showAndWait();

        // Fermer la fenêtre actuelle après l'ajout de l'offre
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
    @FXML
    void annuler(ActionEvent event) {
        Stage stage = (Stage) codeTF.getScene().getWindow();
        stage.close();
    }

}
