package controller.back;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import entities.Categorie;
import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import service.CategorieService;
import javafx.scene.image.Image;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.*;
import javax.management.Notification;
import javax.swing.*;
import javax.swing.text.Document;
import java.util.*;
import java.util.List;
import javafx.fxml.Initializable;
import utils.DataSource;


public class AjouterCategorieController implements Initializable {
    ////////Declarer variable
    private Connection conn;
    private PreparedStatement pst;
    private Statement statement;
    @FXML
    private ImageView qrcode;
    @FXML
    private Button qrcodebtn;
    //appel du service crud
    private final CategorieService cs = new CategorieService();
    private CategorieService css = new CategorieService();
    @FXML
    private ComboBox<String> sortCategorieBox;
    @FXML
    private Button ajouterCategorie;
    @FXML
    private Button btnImageC;
    @FXML
    private ImageView tfImage;
    private String ImagePath;
    @FXML
    private TextField tfNomCategorie;
    @FXML
    private TableColumn<Categorie, String> imageCategorieTab;
    @FXML
    private TableColumn<Categorie, String> nomCategorieTab;
    @FXML
    private TableView<Categorie> tabCategorie;

    //le path  et les elements pour stocker les images
    String filepath = null, filename = null, fn = null;
    String uploads = "C:/Users/Hp/Desktop/pidev/public/images/";
    String uploads2 = "C:/Users/Hp/Desktop/produitCategorie/src/main/java/PDF/";
    FileChooser fc = new FileChooser();
    ObservableList<Categorie> list = FXCollections.observableArrayList();
    public int idCategorie;
    public int getIdCategorie() {
        return idCategorie;
    }
    public void setIdCategorie(int id) {
        this.idCategorie = id;
    }

    @FXML
    private Button modifierCategorie;
    @FXML
    private Button supprimerCategorie;
    private List<Categorie> temp;
    @FXML
    private Button excelCategorie;

    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());

    }
    //initialisation de l'interface
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        conn = DataSource.getInstance().getCnx();
        sortCategorieBox.getItems().removeAll(sortCategorieBox.getItems());
        sortCategorieBox.getItems().addAll("Trier", "Trier par Nom ↑", "Trier par Nom ↓");
        sortCategorieBox.getSelectionModel().select("Trier");
        showCategorie();

    }

    @FXML
    private TextField RechercherCategorie;
    @FXML
    private Button pdfCategorie;


    //telecharger une image et mettre dans dossier path
    public void btn_image_action(ActionEvent actionEvent) throws SQLException, FileNotFoundException, IOException {
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File file = fc.showOpenDialog(null);
        // Shows a new file open dialog.
        if (file != null) {
            // URI that represents this abstract pathname
            tfImage.setImage(new Image(file.toURI().toString()));

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


    //ajouter une categorie
    @FXML
    public void AjouterCategorie(javafx.event.ActionEvent actionEvent) throws SQLException {
        if (tfNomCategorie.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("Le nom de la catégorie ne peut pas être vide.");
            a.showAndWait();

        } else if (!estAlphabetique(tfNomCategorie.getText())) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("Le nom dot contenir les lettres alphabetiques uniquement.");
            a.showAndWait();

        } else if (fn == null) {

            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("Charger une image svp !.");
            a.showAndWait();

        } else {
            cs.addCategorie(new Categorie(tfNomCategorie.getText(), filename));
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Succes");
            a.setContentText("Categorie Ajoutée");
            a.showAndWait();
            showCategorie();
        }
    }

    //lettre alpha obligatoire
    private boolean estAlphabetique(String str) {
        return str.matches("^[a-zA-Z]+$");
    }


    //afficher les categories
    public void showCategorie() {
        nomCategorieTab.setCellValueFactory(new PropertyValueFactory<>("nomCategorie"));
        imageCategorieTab.setCellFactory(column -> new TableCell<Categorie, String>() {
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
        imageCategorieTab.setCellValueFactory(new PropertyValueFactory<>("imageCategorie"));
        list = cs.readCategorie();
        tabCategorie.setItems(list);

    }



    //prendre les valeurs du tableView et l'affiche dans textfield quand je clique
    public void SetValue(MouseEvent mouseEvent) throws SQLException, ClassNotFoundException {
        Categorie selected = tabCategorie.getSelectionModel().getSelectedItem();

        if (selected != null) {
            tfNomCategorie.setText(selected.getNomCategorie());
            fn = selected.getImageCategorie();
            idCategorie = selected.getIdCategorie();
            Image im = new Image("file:" + uploads + selected.getImageCategorie());
            tfImage.setImage(im);
        }
    }


    //modifier une categorie
    @FXML
    public void ModifierCategorie(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String nomC = tfNomCategorie.getText();
        Categorie c = new Categorie(idCategorie, nomC, fn);
        if (nomC.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("Le nom de la catégorie ne peut pas être vide.");
            a.showAndWait();

        } else if (!estAlphabetique(nomC)) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("Le nom dot contenir les lettres alphabetiques uniquement.");
            a.showAndWait();

        } else if (fn == null) {

            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Erreur");
            a.setContentText("Charger une image svp !.");
            a.showAndWait();

        } else {
            css.modifyCategorie(c);
            Alert a = new Alert(Alert.AlertType.WARNING);

            a.setTitle("Succes");
            a.setContentText("Catégorie Modifiée");
            a.showAndWait();
            showCategorie();
        }
    }



    //supprimer une categorie
    public void SupprimerCategorie(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Categorie selected = tabCategorie.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Voulez-Vous Supprimer cet Categorie?");
            alert.setContentText("Supprimer?");
            ButtonType okButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
            alert.showAndWait().ifPresent(type -> {
                if (type == okButton) {
                    css.deleteCategorie(selected.getIdCategorie());
                    showCategorie();
                } else if (type == noButton) {
                    showCategorie();
                } else {
                    showCategorie();
                }
            });
        }
    }



    //action chercher categorie par nom
    @FXML
    public void searchCategorie(KeyEvent keyEvent) {
        FilteredList<Categorie> filter = new FilteredList<>(list, ev -> true);

        RechercherCategorie.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate(t -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (String.valueOf(t.getNomCategorie()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Categorie> sort = new SortedList<>(filter);
        sort.comparatorProperty().bind(tabCategorie.comparatorProperty());
        tabCategorie.setItems(sort);

    }



    //trie selon le nom de categorie
    @FXML
    public void sortCategorie(ActionEvent actionEvent) {
        String selected = sortCategorieBox.getSelectionModel().getSelectedItem();
        if (selected.equals("Trier par Nom ↑")) {
            temp = css.sortCategorieAsc();

        } else if (selected.equals("Trier par Nom ↓")) {
            temp = css.sortCategorieDesc();

        }
        // Mettez à jour la liste observable utilisée par votre TableView (par exemple, 'list')
        ObservableList<Categorie> updatedList = FXCollections.observableArrayList(temp);

        // Mettre à jour la TableView
        tabCategorie.setItems(updatedList);
    }


    //generate pdf
    @FXML
    public void generatePdfCategorie(ActionEvent actionEvent) {

        ObservableList<Categorie> data = tabCategorie.getItems();

        try {
            // Créez un nouveau document PDF
            PDDocument document = new PDDocument();

            // Créez une page dans le document
            PDPage page = new PDPage();
            document.addPage(page);

            // Obtenez le contenu de la page
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Écrivez du texte dans le document
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);


            for (Categorie categorie : data) {
            // Ajouter l'image
                String imagePath = uploads + categorie.getImageCategorie();
                PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, document);

                String ligne = "ID : " + categorie.getIdCategorie() + "     Nom : " + categorie.getNomCategorie();
                contentStream.showText(ligne);

                contentStream.newLine();
                ;
                contentStream.newLineAtOffset(0, -15);


            }

            contentStream.endText();

            // Fermez le contenu de la page
            contentStream.close();

            String outputPath = "C:/Users/Hp/Desktop/produitCategorie/src/main/java/PDF/categories.pdf";
            File file = new File(outputPath);
            document.save(file);

            // Fermez le document
            document.close();

            System.out.println("Le PDF a été généré avec succès.");
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //generate qrcode
    @FXML
    public void generateQrCode(ActionEvent actionEvent) {

        Categorie selected = tabCategorie.getSelectionModel().getSelectedItem();
        // Vérifiez si un élément est sélectionné
        if (selected != null) {
            // Générez la chaîne de données pour le QR code
            String qrData = "Nom: " + selected.getNomCategorie();

            // Générez et affichez le QR code
            generateAndDisplayQRCode(qrData);
        } else {
            // Affichez un message d'erreur ou prenez une autre action appropriée
            System.out.println("Aucune catégorie sélectionnée.");
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
            qrcode.setFitWidth(184);
            qrcode.setFitHeight(199);

            // Convertir la matrice en image JavaFX
            Image qrCodeImage = matrixToImage(matrix);

            // Afficher l'image du QR code dans l'ImageView
            qrcode.setImage(qrCodeImage);
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


    //generate excel
    @FXML
    public void generateExcelCategorie(ActionEvent actionEvent) throws SQLException, FileNotFoundException, IOException {

        String req = "SELECT idCategorie,nomCategorie FROM categorie ";
        statement = conn.createStatement();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(req);

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Détails Categorie");
        HSSFRow header = sheet.createRow(0);


        header.createCell(0).setCellValue("idCategorie");
        header.createCell(1).setCellValue("nomCategorie");


        int index = 1;
        while (rs.next()) {
            HSSFRow row = sheet.createRow(index);

            row.createCell(0).setCellValue(rs.getInt("idCategorie"));
            row.createCell(1).setCellValue(rs.getString("nomCategorie"));
            index++;
        }

        FileOutputStream file = new FileOutputStream("C:/Users/Hp/Desktop/produitCategorie/src/main/java/EXCEL/categorie.xls");
        wb.write(file);
        file.close();

        JOptionPane.showMessageDialog(null, "Exportation 'EXCEL' effectuée avec succés");

        pst.close();
        rs.close();
    }




    //navbar to produit
    @FXML
    public void switchToProduit(ActionEvent actionEvent) throws IOException {
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

    //navbar to paniercommande
    public void switchToPanierCommande2(ActionEvent actionEvent)throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/BackPanierCommande.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        BackPanierCommandeController bcm=fxmlLoader.getController();
        bcm.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }

    @FXML
    public void retourMenu(ActionEvent event)  throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/BackMenu.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        BackMenuController bmc=fxmlLoader.getController();
        bmc.setUserData(userData);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
        stage.show();
    }
}