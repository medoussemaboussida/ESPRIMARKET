package controller.front;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import entities.*;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
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
import java.util.*;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class FrontProduitController implements Initializable {
    private final ProduitService ps = new ProduitService();
    @FXML
    public TextField searchProductFront;
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
        setCombo();
        ComboProduitC.setOnAction(this::filtrerProduit);

        showProduitFrontp();

    }

    //combobox categorie
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

    //filtrage selon categorie choisi
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

    //clique sur logo panier pour ouvrir interface panier
    public void checkPanier(MouseEvent mouseEvent) throws IOException {
        if (userData != null) {
            // Print or display user information as needed
            System.out.println("User ID: " + userData.getIdUser());
            System.out.println("User Name: " + userData.getNomUser());
            System.out.println("User Email: " + userData.getEmailUser());
            // Add more fields as needed
            // Load the new scene and pass user data

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FrontPanierCommande.fxml"));
            Parent root1 = null;
            try {
                root1 = fxmlLoader.load();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            FrontPanierCommandeController fpc = fxmlLoader.getController();
            fpc.setUserData(userData);

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

    //affichage de la liste pour user
    public void showProduitFrontp() {
        // Clear the existing content in ListView
        listView.getItems().clear();

        // Fetch the list of products from your service
        list = ps.readProduit();

        // Add each product to ListView
        for (int i = 0; i < list.size(); i += 4) {
            GridPane gridPane = createProductGridPane(
                    list.get(i),
                    (i + 1 < list.size()) ? list.get(i + 1) : null,
                    (i + 2 < list.size()) ? list.get(i + 2) : null,
                    (i + 3 < list.size()) ? list.get(i + 3) : null
            );
            listView.getItems().add(gridPane);
            //css
            gridPane.getStyleClass().add("grid-pane-product");

        }

    }

    private GridPane createProductGridPane(Produit produit1, Produit produit2, Produit produit3, Produit produit4) {
        GridPane gridPane = new GridPane();

        VBox vbox1 = createProductBox(produit1);
        VBox vbox2 = (produit2 != null) ? createProductBox(produit2) : new VBox();
        VBox vbox3 = (produit3 != null) ? createProductBox(produit3) : new VBox();
        VBox vbox4 = (produit4 != null) ? createProductBox(produit4) : new VBox();

        gridPane.add(vbox1, 0, 0);
        gridPane.add(vbox2, 1, 0);
        gridPane.add(vbox3, 2, 0);
        gridPane.add(vbox4, 3, 0);

        gridPane.setHgap(10);

        return gridPane;
    }
    @FXML
    private ImageView qrcodeProduit;
    private VBox createProductBox(Produit produit) {
        VBox vbox = new VBox();
        float prix = produit.getPrix();
        float reduction = ps.getReduction(produit.getIdProduit());
        float prixReduction = prix * (100 - ps.getReduction(produit.getIdProduit())) / 100;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.000"); // Format avec trois chiffres après la virgule
        String prixFormate = decimalFormat.format(prix);
        String prixReductionFormate = decimalFormat.format(prixReduction);

        // Create and set up UI components for each product
        javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
        loadAndSetImage(imageView, produit.getImageProduit());

        Label nameLabel = new Label(produit.getNomProduit());
        Label priceLabel = new Label("Prix: " + prixFormate);
        Label quantityLabel = new Label("Quantité en stock: " + produit.getQuantite());
        Button addButton = new Button("+");
        Label reductionLabel;
        Label priceReductionLabel;

        if (reduction == 0) {
            reductionLabel = new Label("Ce produit n'a pas une réduction");
            priceReductionLabel = new Label("");
        } else {
            reductionLabel = new Label("Réduction de " + reduction + "%");
            priceReductionLabel = new Label("Prix après réduction: " + prixReductionFormate);
        }

        addButton.getStyleClass().add("addbuttonPanier");
        nameLabel.getStyleClass().add("product-label");
        priceLabel.getStyleClass().add("product-label");
        reductionLabel.getStyleClass().add("product-label");
        quantityLabel.getStyleClass().add("product-label");
        priceReductionLabel.getStyleClass().add("product-label");
        Button qrCodeButton = new Button("QR code");
        qrCodeButton.getStyleClass().add("addbuttonPanier");

        // Add components to VBox
        vbox.getChildren().addAll(imageView, nameLabel, priceLabel, quantityLabel, reductionLabel, priceReductionLabel, addButton, qrCodeButton);

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

        qrCodeButton.setOnAction(event ->
                {
                    String qrData = "Nom: " + produit.getNomProduit() + "\t Quantite: " + produit.getQuantite() + "\n Prix: " + produit.getPrix() + "\t Categorie associée: " + produit.getCategorie().getNomCategorie();

                    // Générez et affichez le QR code
                    generateAndDisplayQRCode(qrData);
                }


        );

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

    //generate qrcode et l'afficher
    private void generateAndDisplayQRCode(String qrData) {
        try {
            // Configuration pour générer le QR code
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            // Générer le QR code avec ZXing
            BitMatrix matrix = new MultiFormatWriter().encode(qrData, BarcodeFormat.QR_CODE, 184, 199, hints);
// Ajuster la taille de l'ImageView
            qrcodeProduit.setFitWidth(100);
            qrcodeProduit.setFitHeight(100);

            // Convertir la matrice en image JavaFX
            Image qrCodeImage = matrixToImage(matrix);

            // Afficher l'image du QR code dans l'ImageView
            qrcodeProduit.setImage(qrCodeImage);
            Alert a = new Alert(Alert.AlertType.WARNING);

            a.setTitle("Succes");
            a.setContentText("qr code generer");
            a.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour convertir une matrice BitMatrix en image BufferedImage
    private Image matrixToImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelColor = matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
                pixelWriter.setArgb(x, y, pixelColor);
            }
        }

        System.out.println("Matrice convertie en image avec succès");

        return writableImage;
    }



    //recherche avance
    @FXML
    public void searchProduct(KeyEvent keyEvent) {
        // Récupérer le texte de recherche
        String searchTerm = searchProductFront.getText().toLowerCase();

        // Effacer la ListView
        listView.getItems().clear();

        // Traiter le produit recherché à part
        Produit searchedProduct = null;
        for (Produit produit : list) {
            if (produit.getNomProduit().toLowerCase().startsWith(searchTerm)) {
                searchedProduct = produit;
                break;  // Sortir de la boucle dès qu'on a trouvé le produit recherché
            }
        }

        // Afficher le produit recherché s'il existe
        if (searchedProduct != null) {
            GridPane searchedProductGridPane = createProductGridPane(searchedProduct, null, null, null);
            listView.getItems().add(searchedProductGridPane);
            // css
            searchedProductGridPane.getStyleClass().add("grid-pane-product");
        }

        // Filtrer et ajouter les autres produits correspondants au texte de recherche
        for (Produit produit : list) {
            // Ignorer le produit recherché car il a déjà été traité
            if (produit.equals(searchedProduct)) {
                continue;
            }

            if (produit.getNomProduit().toLowerCase().contains(searchTerm)) {
                // Ajouter le produit à la ListView
                GridPane gridPane = createProductGridPane(produit, null, null, null);
                listView.getItems().add(gridPane);
                // css
                gridPane.getStyleClass().add("grid-pane-product");
            }
        }
    }


    @FXML
    public void retourMenuFront(ActionEvent event) throws IOException {
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






