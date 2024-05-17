package controller.front;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ListeUtilisateursController {

    @FXML
    private TextField listTF;

    @FXML
    private TextField nomUserTF;

    @FXML
    private TextField prenomUserTF;

    public void setListTF(String listTF) {
        System.out.println("Setting listTF: " + listTF); // Debug print
        this.listTF.setText(listTF);
    }

    public void setNomUserTF(String nomUserTF) {
        System.out.println("Setting nomUserTF: " + nomUserTF); // Debug print
        this.nomUserTF.setText(nomUserTF);
    }

    public void setPrenomUserTF(String prenomUserTF) {
        System.out.println("Setting prenomUserTF: " + prenomUserTF); // Debug print
        this.prenomUserTF.setText(prenomUserTF);
    }
}
