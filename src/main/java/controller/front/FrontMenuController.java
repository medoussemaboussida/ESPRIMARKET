package controller.front;
import entities.Utilisateur;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;

public class FrontMenuController {

    @FXML
    private Button donsMenu;

    @FXML
    private Button eventMenu;

    @FXML
    private Button produitMenu;

    @FXML
    private Button pubMenu;
    @FXML
    private Label labelUserName;
    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bienvenue");
        alert.setHeaderText("Client  " + userData.getNomUser());
        alert.setContentText("Vous êtes connecté !\n"
                + "Email: " + userData.getEmailUser());
        alert.showAndWait();
        labelUserName.setText(String.format(userData.getNomUser()));
    }
    public void goToProduit(ActionEvent event)throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FrontProduit.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        FrontProduitController fp =fxmlLoader.getController();
        fp.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }

    public void goToPub(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FrontPublication.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        FrontPublicationController fpc =fxmlLoader.getController();
        fpc.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }

    public void goToDons(ActionEvent event)throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FaireDons.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        FaireDonsController fd =fxmlLoader.getController();
        fd.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }

    public void goToEvent(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/afficherevenement.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        AfficherevenementController avc =fxmlLoader.getController();
        avc.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }


    public void updateProfile(MouseEvent mouseEvent) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UpdateUtilisateur.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
      EditerleprofilController epc =fxmlLoader.getController();
        epc.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) mouseEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }

    @FXML
    public void goToDemandeDons(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/demande_dons.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        DemandeDonsController dd =fxmlLoader.getController();
        dd.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }

    public void toOffre(ActionEvent event)throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FrontProduitOffre.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        FrontProduitOffreController fpo =fxmlLoader.getController();
        fpo.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }

    public void back(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Seconnecter.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }
}
