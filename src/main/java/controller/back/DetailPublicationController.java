package controller.back;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entities.commentaire;
import entities.Utilisateur;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import service.CommentaireService;
import service.PublicationService;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DetailPublicationController implements Initializable {

    @FXML
    private TableView<commentaire> CommentaireTable;

    @FXML
    private TableColumn<commentaire, String> commentairefx;

    @FXML
    private TableColumn<commentaire, String> edit;

    @FXML
    private TableColumn<commentaire, String> idFx;

    @FXML
    private TableColumn<commentaire, String> nomUser;

    @FXML
    private TableColumn<commentaire, String> prenomUser;

    @FXML
    private ImageView imageDetail;

    @FXML
    private TextField titreDetail;

    @FXML
    private ComboBox<String> sortCommentaireBox;

    private List<commentaire> temp;
    private int idPublication;
    private String htdocsPath = "C:/Users/Hp/Desktop/pidev/public/images/";

    private CommentaireService commentaireService;
    private PublicationService publicationService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        commentaireService = new CommentaireService();
        publicationService = new PublicationService();

        // Rendre le champ de titre en gras et désactivé


    }

    void setIdPublication(int idPublication) {
        this.idPublication = idPublication;
    }

    void loadCommentairesData(int idPublication) {
        List<commentaire> commentairesList = commentaireService.readAll(idPublication);
        ObservableList<commentaire> observableCommentairesList = FXCollections.observableArrayList(commentairesList);

        commentairefx.setCellValueFactory(new PropertyValueFactory<>("descriptionCommentaire"));

        nomUser.setCellValueFactory(cellData -> {
            SimpleStringProperty property = new SimpleStringProperty();
           Utilisateur user = cellData.getValue().getIdUser();
            if (user != null) {
                property.setValue(user.getNomUser());
            }
            return property;
        });

        prenomUser.setCellValueFactory(cellData -> {
            SimpleStringProperty property = new SimpleStringProperty();
            Utilisateur user = cellData.getValue().getIdUser();
            if (user != null) {
                property.setValue(user.getPrenomUser());
            }
            return property;
        });

        CommentaireTable.setItems(observableCommentairesList);

        Callback<TableColumn<commentaire, String>, TableCell<commentaire, String>> cellFactory =
                (TableColumn<commentaire, String> param) -> new TableCell<commentaire, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                            deleteIcon.setStyle("-fx-cursor: hand; -glyph-size:28px; -fx-fill:#ff1544;");

                            deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                                commentaire comment = getTableView().getSelectionModel().getSelectedItem();
                                if (comment != null) {
                                    showDeleteConfirmation(comment);
                                }
                            });

                            HBox manageBtn = new HBox(deleteIcon);
                            manageBtn.setStyle("-fx-alignment:center");
                            HBox.setMargin(deleteIcon, new Insets(2, 2, 0, 3));

                            setGraphic(manageBtn);
                            setText(null);
                        }
                    }
                };

        edit.setCellFactory(cellFactory);
    }

    public void sortCommentaire(ActionEvent actionEvent) {
        String selected = sortCommentaireBox.getSelectionModel().getSelectedItem();

        if (selected.equals("Trier  ↑")) {
            temp = commentaireService.sortProduitPrixAsc(19);
        } else if (selected.equals("Trier  ↓")) {
            temp = commentaireService.sortProduitPrixDESC(19);
        }

        ObservableList<commentaire> updatedList = FXCollections.observableArrayList(temp);
        CommentaireTable.setItems(updatedList);
    }

    @FXML
    private TextField RechercherCommentaire;

    @FXML
    public void searchCommentaire(KeyEvent keyEvent) {
        FilteredList<commentaire> filter = new FilteredList<>(CommentaireTable.getItems(), p -> true);

        RechercherCommentaire.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate(commentaire -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                Utilisateur user = commentaire.getUser();
                if (user != null) {
                    String userFullName = user.getNomUser() + " " + user.getPrenomUser();
                    return userFullName.toLowerCase().contains(lowerCaseFilter)
                            || commentaire.getDescriptionCommentaire().toLowerCase().contains(lowerCaseFilter);
                }
                return false;
            });
        });

        SortedList<commentaire> sort = new SortedList<>(filter);
        sort.comparatorProperty().bind(CommentaireTable.comparatorProperty());
        CommentaireTable.setItems(sort);
    }

    public void setFields(String idPublication, String description, String titrePublication, String imagePublication, Date datePublication) {
        titreDetail.setText(titrePublication);

        try {
            File file = new File(htdocsPath + imagePublication);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageDetail.setImage(image);
            } else {
                System.out.println("Le fichier spécifié n'existe pas : " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showDeleteConfirmation(commentaire comment) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Confirmation de suppression");
        confirmationDialog.setContentText("Êtes-vous sûr de vouloir supprimer ce commentaire ?");

        ButtonType ouiButton = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
        ButtonType nonButton = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationDialog.getButtonTypes().setAll(ouiButton, nonButton);

        Optional<ButtonType> result = confirmationDialog.showAndWait();

        if (result.isPresent() && result.get() == ouiButton) {
            commentaireService.deleteCommentaire(comment.getIdCommentaire());
            ObservableList<commentaire> observableList = CommentaireTable.getItems();
            observableList.remove(comment);
            CommentaireTable.setItems(observableList);
        }
    }
}
