package controller.back;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import entities.Categorie;
import entities.Produit;
import entities.Utilisateur;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import service.CategorieService;
import service.ProduitService;
import utils.DataSource;

import javax.swing.*;
import java.awt.*;
import java.awt.Label;
import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class AjouterProduitController implements Initializable {
    @FXML
    private PieChart pieChart;
    private Connection conn;
    private PreparedStatement pst;
    private Statement statement;
    private final ProduitService ps = new ProduitService();
    private ProduitService pss = new ProduitService();
    @FXML
    private TextField RechercherProduit;
    @FXML
    private ComboBox<Categorie> ComboProduitC;
    @FXML
    private Button ajouterProduit;
    @FXML
    private Button btImageProduit;
    @FXML
    private Button modifierProduit;
    @FXML
    private Button supprimerProduit;
    @FXML
    private ImageView tfImageP;
    @FXML
    private TextField tfNomProduit;
    @FXML
    private TextField tfPrixProduit;
    @FXML
    private TextField tfQuantiteProduit;
    @FXML
    private TableView<Produit> tabProduit;
    @FXML
    private TableColumn<Produit, String> nomCategorieTab;
    @FXML
    private TableColumn<Produit, Float> nomPrixTab;
    @FXML
    private TableColumn<Produit, String> nomProduitTab;
    @FXML
    private TableColumn<Produit, Integer> nomQuantiteTab;
    @FXML
    private TableColumn<Produit, String> imageProduitTab;
    @FXML
    private TableColumn<Produit, String> OffreProduitTab;
    String filepath = null, filename = null, fn = null;
    String uploads = "C:/Users/Hp/Desktop/pidev/public/images/";
    FileChooser fc = new FileChooser();
    ObservableList<Produit> list = FXCollections.observableArrayList();
    public int idProduit;
    public int getIdProduit() {
        return getIdProduit();
    }
    public void setIdProduit(int id) {
        this.idProduit = id;
    }
    @FXML
    private ComboBox<String> sortProduitBox;
    private List<Produit> temp;
    @FXML
    private ImageView qrcodeProduit;
    @FXML
    private Button pdfProduit;
    @FXML
    private Button excelProduit;
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
        conn = DataSource.getInstance().getCnx();
        sortProduitBox.getItems().removeAll(sortProduitBox.getItems());
        sortProduitBox.getItems().addAll("Trier", "Trier par Prix ↑", "Trier par Prix ↓");
        sortProduitBox.getSelectionModel().select("Trier");
        setCombo();
        showProduit();
        addDataToChart();

    }

    //choisir une image
    public void btn_image_produit_action(ActionEvent actionEvent) throws SQLException, FileNotFoundException, IOException {
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File file = fc.showOpenDialog(null);
        // Shows a new file open dialog.
        if (file != null) {
            // URI that represents this abstract pathname
            tfImageP.setImage(new Image(file.toURI().toString()));

            filename = file.getName();
            filepath = file.getAbsolutePath();

            fn = filename;

            FileChannel source = new FileInputStream(filepath).getChannel();
            FileChannel dest = new FileOutputStream(uploads + filename).getChannel();
            dest.transferFrom(source, 0, source.size());
        } else {
            System.out.println("Fichier invalide!");
        }
    }

    //add et cs
    @FXML
    public void AjouterProduit(ActionEvent actionEvent) throws SQLException {

        if (tfNomProduit.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("nom Produit est vide !");
            a.showAndWait();

        }
        else if(ComboProduitC.getValue()==null)
        {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("il faut sélectionner une categorie !");
            a.showAndWait();
        }

      else if (tfQuantiteProduit.getText().isEmpty())
        {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("quantite est vide !");
            a.showAndWait();
        }
        else if(tfPrixProduit.getText().isEmpty())
        {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("Prix est vide !");
            a.showAndWait();
        }
        else if (fn == null) {

            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("Charger une image svp !.");
            a.showAndWait();

        }

        else {
            String nomProd = tfNomProduit.getText();
            Categorie cat = ComboProduitC.getValue();
            Float prix = Float.parseFloat(tfPrixProduit.getText());
            int quantite = Integer.parseInt(tfQuantiteProduit.getText());
            if(quantite<=0)
            {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Erreur");
                a.setContentText("Quantité supérieur à 0!");
                a.showAndWait();
            }
            else
            if(prix<=0)
            {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Erreur");
                a.setContentText("prix supérieur à 0!");
                a.showAndWait();

            }
            else {

                ps.addProduit(new Produit(nomProd, quantite, prix, cat, filename));
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Succes");
                a.setContentText("Produit Ajoutée");
                a.showAndWait();
            }
        }
    }
    public void ModifierProduit(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if (tfNomProduit.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("nom Produit est vide !");
            a.showAndWait();

        }
        else if(ComboProduitC.getValue()==null)
        {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("il faut sélectionner une categorie !");
            a.showAndWait();
        }

        else if (tfQuantiteProduit.getText().isEmpty())
        {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("quantite est vide !");
            a.showAndWait();
        }
        else if(tfPrixProduit.getText().isEmpty())
        {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("Prix est vide !");
            a.showAndWait();
        }
        else if (fn == null) {

            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("Charger une image svp !.");
            a.showAndWait();

        }
        else {


            String nomProd = tfNomProduit.getText();
            Categorie cat = ComboProduitC.getValue();
            Float prix = Float.parseFloat(tfPrixProduit.getText());
            int quantite = Integer.parseInt(tfQuantiteProduit.getText());
            if(quantite<=0)
            {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Erreur");
                a.setContentText("Quantité supérieur à 0!");
                a.showAndWait();
            }
            else
            if(prix<=0)
            {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Erreur");
                a.setContentText("prix supérieur à 0!");
                a.showAndWait();

            }
            else {

                Produit p = new Produit(idProduit, nomProd, quantite, prix, cat, fn);
                pss.modifyProduit(p);
                Alert a = new Alert(Alert.AlertType.WARNING);

                a.setTitle("Succes");
                a.setContentText("Produit Modifiée");
                a.showAndWait();
                showProduit();
            }
        }
    }

    //delete
    public void SupprimerProduit(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Produit selected = tabProduit.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Voulez-Vous Supprimer ce produit ?");
            alert.setContentText("Supprimer?");
            ButtonType okButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
            alert.showAndWait().ifPresent(type -> {
                if (type == okButton) {
                    pss.deleteProduit(selected.getIdProduit());
                    showProduit();
                    tabProduit.refresh();
                } else if (type == noButton) {
                    showProduit();
                } else {
                    showProduit();
                }
            });
        }
    }


    //affichage du produit
    public void showProduit() {
        nomProduitTab.setCellValueFactory(new PropertyValueFactory<>("nomProduit"));
        nomQuantiteTab.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        nomPrixTab.setCellValueFactory(new PropertyValueFactory<>("prix"));
 //format du prix
        nomPrixTab.setCellFactory(new Callback<TableColumn<Produit, Float>, TableCell<Produit, Float>>() {
            @Override
            public TableCell<Produit, Float> call(TableColumn<Produit, Float> param) {
                return new TableCell<Produit, Float>() {
                    @Override
                    protected void updateItem(Float item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            DecimalFormat decimalFormat = new DecimalFormat("#,##0.000");
                            setText(decimalFormat.format(item));
                        }
                    }
                };
            }
        });
        nomCategorieTab.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Produit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Produit, String> param) {
                String nomCategorie = param.getValue().getCategorie().getNomCategorie();
                return new SimpleStringProperty(nomCategorie);
            }
        });

        imageProduitTab.setCellFactory(column -> new TableCell<Produit, String>() {
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
        imageProduitTab.setCellValueFactory(new PropertyValueFactory<>("imageProduit"));

        list = ps.readProduit();
        tabProduit.setItems(list);

    }

    public void setValue(MouseEvent mouseEvent) throws SQLException, ClassNotFoundException {
        Produit selected = tabProduit.getSelectionModel().getSelectedItem();
        CategorieService tabC = new CategorieService();
        if (selected != null) {
            ComboProduitC.setValue(selected.getCategorie());
            tfNomProduit.setText(selected.getNomProduit());
            tfPrixProduit.setText(String.valueOf(selected.getPrix()));
            tfQuantiteProduit.setText(String.valueOf(selected.getQuantite()));
            idProduit = selected.getIdProduit();
            fn = selected.getImageProduit();
            Image im = new Image("file:" + uploads + selected.getImageProduit());
            tfImageP.setImage(im);
        }
    }



    //combo box du categorie
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


//trier liste des produits
    @FXML
    public void sortProduit(ActionEvent actionEvent) {
        String selected = sortProduitBox.getSelectionModel().getSelectedItem();
        if (selected.equals("Trier par Prix ↑")) {
            temp = pss.sortProduitPrixAsc();

        } else if (selected.equals("Trier par Prix ↓")) {
            temp = pss.sortProduitPrixDesc();

        }
        // Mettez à jour la liste observable utilisée par votre TableView (par exemple, 'list')
        ObservableList<Produit> updatedList = FXCollections.observableArrayList(temp);

        // Mettre à jour la TableView
        tabProduit.setItems(updatedList);
    }



    //qrcode produit
    @FXML
    public void generateQrCodeProduit(ActionEvent actionEvent) {
        Produit selected = tabProduit.getSelectionModel().getSelectedItem();
        // Vérifiez si un élément est sélectionné
        if (selected != null) {
            // Générez la chaîne de données pour le QR code
            String qrData = "Nom: " + selected.getNomProduit() + "Quantite: " + selected.getQuantite() + "Prix: " + selected.getPrix() + "Categorie associée: " + selected.getCategorie();

            // Générez et affichez le QR code
            generateAndDisplayQRCode(qrData);
        } else {
            // Affichez un message d'erreur ou prenez une autre action appropriée
            System.out.println("Aucun Produit sélectionnée.");
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
            qrcodeProduit.setFitWidth(184);
            qrcodeProduit.setFitHeight(199);

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


    //chercher produit selon le nom
    @FXML
    public void searchProduit(KeyEvent keyEvent) {
        FilteredList<Produit> filter = new FilteredList<>(list, ev -> true);

        RechercherProduit.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate(t -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Recherche dans le nom du produit
                boolean nomProduitMatch = t.getNomProduit().toLowerCase().startsWith(lowerCaseFilter);

                // Recherche dans le nom de la catégorie
                boolean nomCategorieMatch = t.getCategorie().getNomCategorie().toLowerCase().contains(lowerCaseFilter);

                // Recherche dans le prix (ajustez selon vos besoins)
                boolean prixMatch = String.valueOf(t.getPrix()).toLowerCase().contains(lowerCaseFilter);

                // Combiner les conditions avec des opérateurs logiques (par exemple, ET (&&) ou OU (||))
                return nomProduitMatch || nomCategorieMatch || prixMatch;
            });
        });

        SortedList<Produit> sort = new SortedList<>(filter);
        sort.comparatorProperty().bind(tabProduit.comparatorProperty());
        tabProduit.setItems(sort);
    }

    //pdf
    @FXML
    public void generatePdfProduit(ActionEvent actionEvent) {
        ObservableList<Produit> data = tabProduit.getItems();
        String outputPath = "C:/Users/Hp/Desktop/produitCategorie/src/main/java/PDF/produits.pdf";
        File file = new File(outputPath);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750); // Ajustez la position selon vos besoins
                contentStream.showText("Liste des Produits");
                contentStream.endText();

                // Définir la police et la taille de la police pour le tableau
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

                // Les marges du tableau
                final float margin = 50;
                // Position de départ Y pour le tableau
                final float startY = page.getMediaBox().getUpperRightY() - margin;
                // Position de départ X pour le tableau
                final float startX = page.getMediaBox().getLowerLeftX() + margin;

                // Hauteur de la ligne
                final float rowHeight = 20;
                // Largeur de la page moins les marges
                final float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
                // Largeur des colonnes
                float[] colWidths = {tableWidth * 0.15f, tableWidth * 0.4f, tableWidth * 0.2f, tableWidth * 0.25f};
                // Nombre total de colonnes
                int cols = colWidths.length;
                // Nombre total de lignes
                int rows = data.size() + 1; // +1 pour l'en-tête du tableau

                // Dessiner l'en-tête du tableau
                String[] headers = new String[]{"ID", "Nom", "Quantité", "Prix"};
                float textX = startX;
                float textY = startY - 15; // Décaler de 15 pour centrer le texte dans la ligne

                for (int i = 0; i < cols; i++) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(textX, textY);
                    contentStream.showText(headers[i]);
                    contentStream.endText();
                    textX += colWidths[i];
                }

                // Dessiner les lignes du tableau
                float nextY = startY;
                for (int i = 0; i <= rows; i++) {
                    contentStream.moveTo(startX, nextY);
                    contentStream.lineTo(startX + tableWidth, nextY);
                    contentStream.stroke();
                    nextY -= rowHeight;
                }

                // Remplir le tableau avec les données
                textY -= rowHeight;
                for (Produit produit : data) {
                    textX = startX;
                    String[] produitData = {
                            String.valueOf(produit.getIdProduit()),
                            produit.getNomProduit(),
                            String.valueOf(produit.getQuantite()),
                            String.valueOf(produit.getPrix())
                    };

                    for (int i = 0; i < cols; i++) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(textX, textY);
                        contentStream.showText(produitData[i]);
                        contentStream.endText();
                        textX += colWidths[i];
                    }

                    textY -= rowHeight;
                }
            }

            document.save(file);
            System.out.println("Le PDF a été généré avec succès.");
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //excel produit par categorie
    @FXML
    public void generateExcelProduit(ActionEvent actionEvent) throws SQLException, FileNotFoundException, IOException {
        // Note : Assurez-vous que la jointure et la requête sont correctes selon votre schéma de base de données
        String req = "SELECT c.nomCategorie, p.idProduit, p.nomProduit, p.quantite, p.prix, SUM(p.quantite) OVER(PARTITION BY c.nomCategorie) AS stock FROM produit p JOIN categorie c ON p.categorie_id = c.idCategorie ORDER BY c.nomCategorie, p.idProduit";
        statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(req);

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Détails produit");
        HSSFRow header = sheet.createRow(0);

        // Ajouter une cellule pour la catégorie et une pour le stock dans l'en-tête
        header.createCell(0).setCellValue("Catégorie");
        header.createCell(1).setCellValue("idProduit");
        header.createCell(2).setCellValue("nomProduit");
        header.createCell(3).setCellValue("quantite");
        header.createCell(4).setCellValue("prix");
        header.createCell(5).setCellValue("Stock totale dans chaque catgorie ");

        int index = 1;
        String currentCategory = "";
        while (rs.next()) {
            String category = rs.getString("nomCategorie");
            if (!category.equals(currentCategory)) {
                // Lorsque la catégorie change, ajoutez une nouvelle ligne pour les détails de la catégorie
                currentCategory = category;
            }

            HSSFRow row = sheet.createRow(index);
            row.createCell(0).setCellValue(category);
            row.createCell(1).setCellValue(rs.getInt("idProduit"));
            row.createCell(2).setCellValue(rs.getString("nomProduit"));
            row.createCell(3).setCellValue(rs.getInt("quantite"));
            row.createCell(4).setCellValue(rs.getDouble("prix"));
            // La dernière cellule de chaque ligne de produit affiche le stock total de la catégorie
            row.createCell(5).setCellValue(rs.getInt("stock"));

            index++;
        }

        FileOutputStream fileOut = new FileOutputStream("C:/Users/Hp/Desktop/produitCategorie/src/main/java/EXCEL/produit.xls");
        wb.write(fileOut);
        fileOut.close();

        JOptionPane.showMessageDialog(null, "Exportation 'EXCEL' effectuée avec succés");
        rs.close();
        statement.close();
    }



    //stat produit par categorie
    private void addDataToChart() {
        // Efface les données existantes
        pieChart.getData().clear();

        // Récupère les statistiques des prix
        int produitsEntre10000Et20000 = getProduitsCountByCategory("Boissons");
        int produitsEntre30000Et50000 = getProduitsCountByCategory("Pattes");
        int totalProduits = getTotalProduitsCount();

        // Créez une liste observable de données PieChart
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                new PieChart.Data("Boissons", produitsEntre10000Et20000),
                new PieChart.Data("Pattes", produitsEntre30000Et50000),
                new PieChart.Data("Autres Catégories", totalProduits - produitsEntre10000Et20000 - produitsEntre30000Et50000)
        );

        // Affectez la liste de données à votre PieChart
        pieChart.setData(data);

        // Autres propriétés du PieChart
        pieChart.setTitle("Activities");
        pieChart.setClockwise(true);
        pieChart.setLabelLineLength(10);
        pieChart.setLabelsVisible(true);
        pieChart.setStartAngle(360);

    }

    private int getProduitsCountByCategory(String category) {
        try {
            String query = "SELECT COUNT(*) FROM produit p JOIN categorie c on p.categorie_id=c.idCategorie WHERE nomCategorie = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, category);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private int getTotalProduitsCount() {
        try {
            String query = "SELECT COUNT(*) FROM produit";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    //refresh interface
    @FXML
    public void refreshProduit(ActionEvent actionEvent)throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AjouterProduit.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }


    // navbar vers categorie
    @FXML
    public void switchToCategorie(ActionEvent actionEvent) throws IOException {
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


//navbar to paniercommande
@FXML
    public void switchToPanierCommande(ActionEvent actionEvent) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/BackPanierCommande.fxml"));
    Parent root1 = (Parent) fxmlLoader.load();
    BackPanierCommandeController bpc=fxmlLoader.getController();
    bpc.setUserData(userData);
    Stage stage = new Stage();
    stage.setScene(new Scene(root1));
    Node source = (Node) actionEvent.getSource();
    Stage currentStage = (Stage) source.getScene().getWindow();
    currentStage.close();
    stage.show();
}


@FXML
public void retourMenu(ActionEvent event)throws IOException {
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



