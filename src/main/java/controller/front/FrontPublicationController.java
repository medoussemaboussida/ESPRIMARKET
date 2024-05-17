package controller.front;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entities.Publication;
import entities.commentaire;
import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.CommentaireService;
import service.PublicationService;
import service.UtilisateurService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FrontPublicationController implements Initializable {

    @FXML
    private ListView<VBox> listView;
    @FXML
    private FontAwesomeIconView emp;

    private PublicationService publicationService;
    private CommentaireService commentaireService;
    private UtilisateurService userService;

    private Utilisateur utilisateur;

    private String htdocsPath = "C:/Users/Hp/Desktop/pidev/public/images/";
    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());
        loadPublications();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        publicationService = new PublicationService();
        commentaireService = new CommentaireService();

    }

    private void loadPublications() {
        ObservableList<VBox> publicationBoxes = FXCollections.observableArrayList();

        List<Publication> publications = publicationService.readAll();

        for (Publication publication : publications) {
            VBox publicationBox = new VBox();
            publicationBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: black; -fx-border-radius: 10px; -fx-padding: 10px;");

            Label titleLabel = new Label(publication.getTitrePublication());
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-family: 'Arial'; -fx-font-size: 24px;");
            titleLabel.setAlignment(Pos.CENTER);

            ImageView imageView = new ImageView();
            try {
                Image image = new Image("file:///" + htdocsPath + publication.getImagePublication());
                imageView.setImage(image);
                imageView.setFitWidth(400);
                imageView.setFitHeight(300);
                VBox.setMargin(imageView, new Insets(10, 0, 10, 0));
            } catch (Exception e) {
                System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
            }

            Label descriptionLabel = new Label(publication.getDescription());
            descriptionLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-wrap-text: true;");
            descriptionLabel.setAlignment(Pos.CENTER);
            VBox.setMargin(descriptionLabel, new Insets(0, 10, 10, 10));

            List<commentaire> commentaires = commentaireService.readAll(publication.getIdPublication());

            VBox commentairesBox = new VBox();
            commentairesBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: black; -fx-border-radius: 10px; -fx-padding: 10px;");
            for (commentaire commentaire : commentaires) {
                Utilisateur commentaireUser = commentaire.getIdUser();

                Label commentaireLabel = new Label(commentaireUser.getNomUser() + " " + commentaireUser.getPrenomUser() + " : " + commentaire.getDescriptionCommentaire());
                commentaireLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-wrap-text: true;");
                VBox.setMargin(commentaireLabel, new Insets(5, 0, 5, 0));

                if (commentaireUser.getIdUser() == userData.getIdUser()) {
                    FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                    deleteIcon.setStyle("-fx-cursor: hand; -glyph-size: 20px; -fx-fill: #ff1544;");
                    deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmationDialog.setTitle("Confirmation de suppression");
                        confirmationDialog.setHeaderText("Voulez-vous vraiment supprimer ce commentaire ?");
                        Optional<ButtonType> result = confirmationDialog.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            commentaireService.deleteCommentaire(commentaire.getIdCommentaire());
                            loadPublications();
                        }
                    });

                    FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);
                    editIcon.setStyle("-fx-cursor: hand; -glyph-size: 20px; -fx-fill: #00E676;");
                    editIcon.setOnMouseClicked((MouseEvent event) -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCommentaire.fxml"));
                            Parent root = loader.load();
                            ModifierCommentaireController controller = loader.getController();
                            controller.initData(commentaire);
                            Scene scene = new Scene(root);
                            Stage stage = new Stage();
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    HBox commentBox = new HBox(commentaireLabel, deleteIcon, editIcon);
                    commentBox.setSpacing(10);
                    commentBox.setAlignment(Pos.CENTER_LEFT);
                    commentairesBox.getChildren().add(commentBox);
                } else {
                    commentairesBox.getChildren().add(commentaireLabel);
                }
            }

            Button addCommentButton = new Button("Ajouter un commentaire");
            addCommentButton.setStyle(
                    "-fx-background-color: #f58c1e; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px;" // Optionnel : ajustez la taille de la police si nécessaire
            );
            addCommentButton.setAlignment(Pos.CENTER);
            addCommentButton.setOnAction(e -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCommentaire.fxml"));
                try {
                    Parent root = loader.load();
                    AjouterCommentaireController controller = loader.getController();
                    controller.initData(userData, publication);
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });



            publicationBox.getChildren().addAll(titleLabel, imageView, descriptionLabel, commentairesBox, addCommentButton);
            publicationBox.setAlignment(Pos.CENTER);
            publicationBox.setSpacing(10);

            publicationBoxes.add(publicationBox);
        }

        listView.setItems(publicationBoxes);
    }

    @FXML
    void refresh(MouseEvent event) {
        loadPublications();
    }

    @FXML
    void emp(MouseEvent event) {
        // L'URL de l'emplacement que vous souhaitez afficher sur Google Maps
        String locationURL = "https://www.google.com/maps/place/ESB+:+Esprit+School+of+Business/@36.8512328,10.1282324,13z/data=!4m10!1m2!2m1!1sESPRIT!3m6!1s0x12e2cb745e5c6f1b:0xf69a51ee3c65c12e!8m2!3d36.8992352!4d10.189445!15sCgZFU1BSSVQiA4gBAZIBCnVuaXZlcnNpdHngAQA!16s%2Fg%2F11cs3ytq00?entry=ttu";

        try {
            java.awt.Desktop.getDesktop().browse(new URL(locationURL).toURI());
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer les erreurs, par exemple une URL incorrecte ou une exception d'ouverture de navigateur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'ouvrir Google Maps. Veuillez vérifier votre connexion Internet.");
            alert.showAndWait();
        }
    }

    @FXML
    public void retourMenuFront(ActionEvent event)throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FrontMenu.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        FrontMenuController fm =fxmlLoader.getController();
        fm.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }
}
