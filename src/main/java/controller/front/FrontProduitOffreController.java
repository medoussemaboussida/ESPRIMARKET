package controller.front;
import entities.*;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import service.*;

import java.awt.*;
import java.awt.ScrollPane;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.scene.control.agenda.Agenda;
public class FrontProduitOffreController implements Initializable {
    private final ProduitService ps = new ProduitService();
    String filepath = null, filename = null, fn = null;
    String uploads = "C:/Users/Hp/Desktop/pidev/public/images/";
    FileChooser fc = new FileChooser();
    ObservableList<Produit> list = FXCollections.observableArrayList();
    public int idProduit;
    PanierService pns = new PanierService();

    public int getIdProduit() {
        return getIdProduit();
    }

    public void setIdProduit(int id) {
        this.idProduit = id;
    }

    private ObservableList<Produit> produitList;

    private ObservableList<Produit> originalProduitList;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<Categorie> ComboProduitC;
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
        searchField.setOnAction(event -> search()); // Appelle la méthode search() lorsque "Entrée" est pressé
        // Appelle la méthode search() chaque fois que le texte change

        produitList = FXCollections.observableArrayList();

        setCombo();
        ComboProduitC.setOnAction(this::filtrerProduit);
        // showProduitFront();
        showProduitFrontp();


    }


    public void setCombo() {
        CategorieService tabC = new CategorieService();
        List<Categorie> tabList = tabC.readCategorie();
        ArrayList<Categorie> cats = new ArrayList<>();
        for (Categorie c : tabList) {
            Categorie cat = new Categorie();
            cat.setIdCategorie(c.getIdCategorie());
            cat.setNomCategorie(c.getNomCategorie());
            cats.add(cat);
        }

        ObservableList<Categorie> choices = FXCollections.observableArrayList(cats);
        ComboProduitC.setItems(choices);

        ComboProduitC.setConverter(new StringConverter<Categorie>() {
            @Override
            public String toString(Categorie categorie) {
                if (categorie == null) {
                    return null;
                } else {
                    return categorie.getNomCategorie();
                }
            }

            @Override
            public Categorie fromString(String string) {
                // Vous pouvez implémenter cette méthode si nécessaire
                return null;
            }
        });
    }

    private List<Produit> temp;
    private List<PanierProduit> temp1;

    @FXML
    public void filtrerProduit(ActionEvent actionEvent) {
        Categorie selectedCategorie = ComboProduitC.getValue();
        int categorieId = selectedCategorie.getIdCategorie();
        temp = ps.readProduitByCategorie(categorieId);
        ObservableList<Produit> updatedList = FXCollections.observableArrayList(temp);

        // Clear the existing content in ListView
        listView.getItems().clear();

        // Add each product to ListView as GridPane
        for (int i = 0; i < updatedList.size(); i += 4) {
            GridPane productGridPane = createProductGridPane(
                    (i < updatedList.size()) ? updatedList.get(i) : null,
                    (i + 1 < updatedList.size()) ? updatedList.get(i + 1) : null,
                    (i + 2 < updatedList.size()) ? updatedList.get(i + 2) : null,
                    (i + 3 < updatedList.size()) ? updatedList.get(i + 3) : null
            );
            listView.getItems().add(productGridPane);
        }
    }

    @FXML
    private ImageView PanierImage;

    public void checkPanier(MouseEvent mouseEvent)  throws IOException {
        if (userData != null) {
            // Print or display user information as needed
            System.out.println("User ID: " + userData.getIdUser());
            System.out.println("User Name: " + userData.getNomUser());
            System.out.println("User Email: " + userData.getEmailUser());
            // Add more fields as needed
            // Load the new scene and pass user data

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FrontPanierCommandeOffre.fxml"));
            Parent root1 = null;
            try {
                root1 = fxmlLoader.load();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            FrontPanierCommandeOffreController fpco = fxmlLoader.getController();
            fpco.setUserData(userData);

            Stage stage = new Stage();
            stage.setTitle("Votre Panier");
            stage.setScene(new Scene(root1));
            Node source = (Node) mouseEvent.getSource();
            stage.show();
            // Close the current scene if needed

            System.out.println("Login successful!");
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Email ou mot de passe incorrect.");
            alert.showAndWait();
        }
    }



    @FXML
    private ListView<GridPane> listView;


    public void showProduitFrontp() {
        // Clear the existing content in ListView
        listView.getItems().clear();

        // Fetch the list of products with offers from your service
        ProduitService offreProduitService = new ProduitService();
        ObservableList<Produit> produitsOffre = offreProduitService.getAllProduits();

        originalProduitList = FXCollections.observableArrayList(produitsOffre);

        // Add each product to ListView
        for (int i = 0; i < produitsOffre.size(); i += 4) {
            GridPane gridPane = createProductGridPane(
                    produitsOffre.get(i),
                    (i + 1 < produitsOffre.size()) ? produitsOffre.get(i + 1) : null,
                    (i + 2 < produitsOffre.size()) ? produitsOffre.get(i + 2) : null,
                    (i + 3 < produitsOffre.size()) ? produitsOffre.get(i + 3) : null
            );
            listView.getItems().add(gridPane);
            //css
            gridPane.getStyleClass().add("grid-pane-product");
        }
    }

    private GridPane createProductGridPane(Produit produit1, Produit produit2, Produit produit3, Produit produit4) {
        GridPane gridPane = new GridPane();

        // Create and set up UI components for each product
        VBox vbox1 = createProductBox(produit1);
        VBox vbox2 = (produit2 != null) ? createProductBox(produit2) : new VBox(); // Empty VBox if no second product
        VBox vbox3 = (produit3 != null) ? createProductBox(produit3) : new VBox(); // Empty VBox if no third product
        VBox vbox4 = (produit4 != null) ? createProductBox(produit4) : new VBox(); // Empty VBox if no fourth product

        // Add components to GridPane
        gridPane.add(vbox1, 0, 0);
        gridPane.add(vbox2, 1, 0);
        gridPane.add(vbox3, 2, 0);
        gridPane.add(vbox4, 3, 0);

        // Set horizontal gap between columns
        gridPane.setHgap(10);

        return gridPane;
    }
    private VBox createProductBox(Produit produit) {
        VBox vbox = new VBox();
        float prix = produit.getPrix();
        float prixReduction = prix * (100 - ps.getReduction(produit.getIdProduit())) / 100;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.000"); // Format avec trois chiffres après la virgule
        String prixFormate = decimalFormat.format(prix);
        String prixReductionFormate = decimalFormat.format(prixReduction);

        // Create and set up UI components for each product
        ImageView imageView = new ImageView();
        loadAndSetImage(imageView, produit.getImageProduit());

        Label nameLabel = new Label(produit.getNomProduit());
        Label priceLabel = new Label("Prix: " + prixFormate);
        //Label quantityLabel = new Label("Quantité en stock: " + produit.getQuantite());
        Label reductionLabel = new Label("Réduction: " + ps.getReduction(produit.getIdProduit()) + "%");
        Label priceReductionLabel = new Label("Prix aprés reduction: " + prixReductionFormate);

        Button addButton = new Button("+");
        addButton.getStyleClass().add("addbuttonPanier");
        nameLabel.getStyleClass().add("product-label");
        priceLabel.getStyleClass().add("product-label");
        reductionLabel.getStyleClass().add("product-label");
        priceReductionLabel.getStyleClass().add("product-label");
        // Add components to VBox
        vbox.getChildren().addAll(imageView, nameLabel, priceLabel, priceReductionLabel, addButton);

        // Set spacing and alignment as needed
        vbox.setSpacing(11);
        vbox.setAlignment(Pos.CENTER);


        addButton.setOnAction(event -> {

            Panier panier = pns.selectPanierParUserId(userData.getIdUser());
            Panier panierExistant = pns.selectPanierParUserId(userData.getIdUser());

            if (panierExistant != null) {
                PanierProduitService panierProduitService = new PanierProduitService();
                panierProduitService.ajouterProduitAuPanier(panier, produit.getIdProduit());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Avertissement");
                alert.setHeaderText(null);
                alert.setContentText("Produit ajouté dans votre panier .");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Avertissement");
                alert.setHeaderText(null);
                alert.setContentText("Vous n'avez pas de panier. Veuillez créer un panier d'abord.");
                alert.showAndWait();
                pns.ajouterPanier(userData.getIdUser());
            }
            // Rafraîchir la vue du produit
            showProduitFrontp();
        });



        return vbox;
    }
    private void loadAndSetImage(ImageView imageView, String imagePath) {
        // Charger et afficher l'image en utilisant le chemin complet du fichier
        File imageFile = new File(uploads + imagePath);
        if (imageFile.exists()) {
            Image image = new Image("file:///" + imageFile.getAbsolutePath());
            imageView.setImage(image);
            imageView.setFitWidth(190);
            imageView.setFitHeight(190);
        } else {
            // Gérer le cas où le fichier d'image n'existe pas
            System.out.println("Le fichier d'image n'existe pas : " + imagePath);
            imageView.setImage(null);
        }
    }
    @FXML
    void search() {
        String keyword = searchField.getText().trim().toLowerCase();
        List<Produit> filteredList = originalProduitList.stream()
                .filter(produit -> produit.getNomProduit().toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        // Mettre à jour la liste des produits affichés dans la vue
        listView.getItems().clear();
        for (int i = 0; i < filteredList.size(); i += 4) {
            GridPane gridPane = createProductGridPane(
                    filteredList.get(i),
                    (i + 1 < filteredList.size()) ? filteredList.get(i + 1) : null,
                    (i + 2 < filteredList.size()) ? filteredList.get(i + 2) : null,
                    (i + 3 < filteredList.size()) ? filteredList.get(i + 3) : null
            );
            listView.getItems().add(gridPane);
            //css
            gridPane.getStyleClass().add("grid-pane-product");
        }
    }

    public void retour(ActionEvent event)throws IOException {
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

    public void showCallender(ActionEvent event) {
        Agenda agenda = new Agenda();

        loadPromosIntoAgenda(agenda);

        Stage calendarStage = new Stage();
        calendarStage.setScene(new Scene(agenda, 800, 600));
        calendarStage.setTitle("Calendrier des Offres Pour Cette Semaines");
        calendarStage.show();
    }


    OffreService offreService = new OffreService();

    private void loadPromosIntoAgenda(Agenda agenda) {
        // Fetch offres from your data source
        List<Offre> offres = offreService.getAllOffres();

        // Add offres to the agenda
        for (Offre offre : offres) {

            // Convert java.sql.Date to java.util.Date
            Date dateDebut = new Date(offre.getDateDebut().getTime());
            Date dateFin = new Date(offre.getDateFin().getTime());

            // Convertir les dates en LocalDateTime avec l'heure fixée à minuit
            LocalDateTime startDateTime = dateDebut.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
            LocalDateTime endDateTime = dateFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();

            // Créer un rendez-vous avec la date de début et de fin
            Agenda.Appointment appointment = new Agenda.AppointmentImplLocal()
                    .withStartLocalDateTime(startDateTime)
                    .withEndLocalDateTime(endDateTime)
                    .withSummary(offre.getNomOffre());

            // Ajouter le rendez-vous à l'agenda
            agenda.appointments().add(appointment);
        }
    }

}






