package controller.back;
import entities.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import service.CommandeService;
import service.PanierProduitService;
import utils.DataSource;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class BackPanierCommandeController implements Initializable {
    @FXML
    private TableColumn<Commande, Date> dateCommandeBack;

    @FXML
    private TableColumn<PanierProduit, String> produitPanierBack;

    @FXML
    private TableView<Commande> tableCommandeBack;

    @FXML
    private TableView<PanierProduit> tablePanierBack;

    @FXML
    private TableColumn<PanierProduit,String> userPanierBack;
    @FXML
    private TableColumn<PanierProduit, String> imageProduitPanierBack;
    String uploads = "C:/Users/Hp/Desktop/pidev/public/images/";


    @FXML
    private TableColumn<Commande,String> utilisateurCommandeBack;
    private PanierProduitService pp= new PanierProduitService();
    private CommandeService cs =new CommandeService();
    ObservableList<PanierProduit> list = FXCollections.observableArrayList();
    ObservableList<Commande> list1 = FXCollections.observableArrayList();
    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());

    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showPanierProduitAll();
showCommandeAll();

    }
    public void showPanierProduitAll() {
        userPanierBack.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PanierProduit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PanierProduit, String> param) {
                String nomPanierUser = param.getValue().getPanier().getUtilisateur().getNomUser();
                return new SimpleStringProperty(nomPanierUser);
            }
        });

        produitPanierBack.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PanierProduit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PanierProduit, String> param) {
                String nomProduit = param.getValue().getProduit().getNomProduit();
                return new SimpleStringProperty(nomProduit);
            }
        });

        imageProduitPanierBack.setCellFactory(column -> new TableCell<PanierProduit, String>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);

                if (empty || imagePath == null) {
                    setGraphic(null);
                } else {
                    // Charger et afficher l'image
                    Image image = new Image("file:///" + uploads + imagePath);
                    imageView.setImage(image);
                    imageView.setFitWidth(120); // Réglez la largeur de l'image selon vos besoins
                    imageView.setFitHeight(100); // Réglez la hauteur de l'image selon vos besoins
                    setGraphic(imageView);
                }
            }
        });
        imageProduitPanierBack.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PanierProduit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PanierProduit, String> param) {
                String nomProduit = param.getValue().getProduit().getImageProduit();
                return new SimpleStringProperty(nomProduit);
            }
        });

        list = pp.getAllProduitsPanier();
        tablePanierBack.setItems(list);

    }

    public void showCommandeAll()
    {

       dateCommandeBack.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));

        utilisateurCommandeBack.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Commande, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Commande, String> param) {
                String userCommande = param.getValue().getPanier().getUtilisateur().getNomUser();
                return new SimpleStringProperty(userCommande);
            }
        });
        list1 = cs.readAllCommande();
        tableCommandeBack.setItems(list1);

    }

    @FXML
    public void switchToProduitFromPanier(ActionEvent actionEvent)throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AjouterProduit.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        AjouterProduitController apc=fxmlLoader.getController();
        apc.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }

    public void switchToCategorieFromPanier(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AjouterCategorie.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        AjouterCategorieController bcc=fxmlLoader.getController();
        bcc.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }

    @FXML
    public void retourMenu(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/BackMenu.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        BackMenuController bm=fxmlLoader.getController();
        bm.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }
}
