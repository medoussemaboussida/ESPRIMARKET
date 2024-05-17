/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.back;

import controller.front.FrontMenuController;
import entities.Utilisateur;
import entities.evenement;
import entities.participant;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.Pdf2;
import service.UtilisateurService;
import service.evenementService;
import service.participantService;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * FXML Controller class
 *
 * @author asus
 */
public class AjouterevenementController implements Initializable {
    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());

    }
    @FXML
    private TextField descriptionabonnField;
    @FXML
    private DatePicker dateabonnField;
    @FXML
    private TextField typeabonnField;
    @FXML
    private TextField imageabonnField;
    @FXML
    private TextField nom_evField;
    @FXML
    private Button supprimerBoutton;
    @FXML
    private Button ajouterButton;
    @FXML
    private Button afficherBoutton;
    @FXML
    private Button goToPong;

    @FXML
    private TableView<evenement> evenementTv;
    @FXML
    private TableColumn<evenement, String> nomabonnTv;
    @FXML
    private TableColumn<evenement, String> typeabonnTv;
    @FXML
    private TableColumn<evenement, String> imageabonnTv;
    @FXML
    private TableColumn<evenement, String> dateabonnTv;
    @FXML
    private TableColumn<evenement, String> descriptionabonnTv;
    @FXML
    private TableColumn<evenement, Integer> code_participantTv;
    @FXML
    private TextField code_participantField;

    private Date date1;
    @FXML
    private Label partError;
    @FXML
    private Label idLabel;

    ObservableList<evenement> abonns;
    evenementService Ev=new evenementService();
    participantService Pservice =new participantService();
    Pdf2 oo=new Pdf2();

    @FXML
    private TextField idmodifierField;
    @FXML
    private Button participerbutton;
    @FXML
    private ImageView imageview;
    @FXML
    private TextField rechercher;
    @FXML
    private ImageView QrCode;
    @FXML
    private ImageView GoBackBtn;
    @FXML
    private Canvas myCanvas;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {


        partError.setVisible(false);
        //idLabel.setText("");
        getabonns();
    }






    private boolean NoDate() {
        LocalDate currentDate = LocalDate.now();
        LocalDate myDate = dateabonnField.getValue();
        int comparisonResult = myDate.compareTo(currentDate);
        boolean test = true;
        if (comparisonResult < 0) {
            // myDate est antérieure à currentDate
            test = true;
        } else if (comparisonResult > 0) {
            // myDate est postérieure à currentDate
            test = false;
        }
        return test;
    }
    @FXML
    private void ajouterevenement(ActionEvent abonn) {

        int part=0;
        if ((nom_evField.getText().length() == 0) || (typeabonnField.getText().length() == 0) || (imageabonnField.getText().length() == 0) || (code_participantField.getText().length() == 0)|| (descriptionabonnField.getText().length() == 0)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error ");
            alert.setHeaderText("Error!");
            alert.setContentText("Fields cannot be empty");
            alert.showAndWait();
        }
        else if (NoDate() == true) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error ");
            alert.setHeaderText("Error!");
            alert.setContentText("la date date doit être aprés la date d'aujourd'hui");
            alert.showAndWait();
        }
        else{
            try {
                part = Integer.parseInt(code_participantField.getText());
                partError.setVisible(false);
            } catch (Exception exc) {
                System.out.println("Number of code_participant int");
                partError.setVisible(true);
                return;
            }
            if(part<10)
            {Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error ");
                alert.setHeaderText("Error!");
                alert.setContentText("le code participant doit être suupp ou egale  à 10");
                alert.showAndWait();
                partError.setVisible(true);}
            else
            {
                evenement e = new evenement();
                if (typeabonnField.getText().equals("Gold")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText("Prix de l'evenement");
                    alert.setContentText("Le prix de cette evenement est 250 DT.");
                    alert.showAndWait();
                } else if (typeabonnField.getText().equals("SILVER")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText("Prix de l'evenement");
                    alert.setContentText("Le prix de cette evenement est 150 DT.");
                    alert.showAndWait();
                } else if (typeabonnField.getText().equals("BRONZE")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText("Prix de l'evenement");
                    alert.setContentText("Le prix de cette evenement est 100 DT.");
                    alert.showAndWait();
                }

                e.setNom_ev(nom_evField.getText());
                e.setType_ev(typeabonnField.getText());
                e.setDescription_ev(descriptionabonnField.getText());
                java.util.Date date_debut=java.util.Date.from(dateabonnField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date sqlDate = new Date(date_debut.getTime());
                e.setDate(sqlDate);
                e.setCode_participant(Integer.valueOf(code_participantField.getText()));

                //lel image
                e.setImage_ev(imageabonnField.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information ");
                alert.setHeaderText("evenement add");
                alert.setContentText("evenement added successfully!");
                alert.showAndWait();
                try {
                    Ev.ajouterevenement(e);
                    reset();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                getabonns();


            }}}

    //fin d ajout d'un evenement
    private void reset() {
        nom_evField.setText("");
        typeabonnField.setText("");
        descriptionabonnField.setText("");
        imageabonnField.setText("");
        code_participantField.setText("");
        dateabonnField.setValue(null);
    }

    public void getabonns() {
        try {
            // TODO
            List<evenement> evenements = Ev.recupererevenement();
            ObservableList<evenement> olp = FXCollections.observableArrayList(evenements);
            evenementTv.setItems(olp);
            nomabonnTv.setCellValueFactory(new PropertyValueFactory<>("nom_ev"));
            typeabonnTv.setCellValueFactory(new PropertyValueFactory<>("type_ev"));
            imageabonnTv.setCellValueFactory(new PropertyValueFactory<>("image_ev"));
            dateabonnTv.setCellValueFactory(new PropertyValueFactory<>("date"));
            descriptionabonnTv.setCellValueFactory(new PropertyValueFactory<>("description_ev"));
            code_participantTv.setCellValueFactory(new PropertyValueFactory<>("code_participant"));


            // this.delete();
        } catch (SQLException ex) {
            System.out.println("error" + ex.getMessage());
        }
    }//get abonns


    @FXML
    private void modifierevenement(ActionEvent abonn) throws SQLException {
        evenement e = new evenement();
        e.setId_ev(Integer.parseInt(idmodifierField.getText()));
        e.setNom_ev(nom_evField.getText());
        e.setType_ev(typeabonnField.getText());
        e.setDescription_ev(descriptionabonnField.getText());
        Date d=Date.valueOf(dateabonnField.getValue());
        e.setDate(d);
        e.setImage_ev(imageabonnField.getText());
        e.setCode_participant(Integer.parseInt(code_participantField.getText()));
        Ev.modifierevenement(e);
        reset();
        getabonns();
    }

    @FXML
    private void supprimerevenement(ActionEvent abonn) {
        evenement e = evenementTv.getItems().get(evenementTv.getSelectionModel().getSelectedIndex());
        try {
            Ev.supprimerevenement(e);
        } catch (SQLException ex) {
            Logger.getLogger(AjouterevenementController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information ");
        alert.setHeaderText("evenement delete");
        alert.setContentText("evenement deleted successfully!");
        alert.showAndWait();
        getabonns();
    }

    @FXML
    private void afficherevenement(ActionEvent abonn) {
        try {
            //navigation
            Parent loader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/afficherevenement.fxml")));
            typeabonnField.getScene().setRoot(loader);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    @FXML
    //ta3 tablee bch nenzel 3ala wehed ya5tarou w yet3abew textfield
    private void choisirabonn(MouseEvent abonn) throws IOException {
        evenement e = evenementTv.getItems().get(evenementTv.getSelectionModel().getSelectedIndex());
        //idLabel.setText(String.valueOf(e.getid_ev()));
        idmodifierField.setText(String.valueOf(e.getId_ev()));
        nom_evField.setText(e.getNom_ev());
        typeabonnField.setText(e.getType_ev());
        imageabonnField.setText(e.getImage_ev());
        descriptionabonnField.setText(e.getDescription_ev());
        //dateabonnField.setValue((e.getDate()));
        code_participantField.setText(String.valueOf(e.getCode_participant()));
        //lel image
        String path = e.getImage_ev();
        File file=new File(path);
        Image img = new Image(file.toURI().toString());
        imageview.setImage(img);

        //////////////      
        String filename = Ev.GenerateQrabonn(e);
        System.out.println("filename lenaaa " + filename);
        String path1="C:\\\\Users\\\\Hp\\\\Desktop\\\\pidev\\\\public\\\\images"+filename;
        File file1=new File(path1);
        Image img1 = new Image(file1.toURI().toString());
        //Image image = new Image(getClass().getResourceAsStream("src/utils/img/" + filename));
        QrCode.setImage(img1);

    }

    private void participer(ActionEvent abonn) {

        Utilisateur u=new Utilisateur();
        LocalDate dateActuelle = LocalDate.now();
        Date dateSQL = Date.valueOf(dateActuelle);
        participant p=new participant();
        p.setDate_part(dateSQL);
        //p.setevenement();
        p.setId_evenement(Integer.parseInt(idmodifierField.getText()));
        p.setId_user(u.getIdUser());
        Pservice.ajouterparticipant(p);
    }

    @FXML
    private void afficherparticipants(ActionEvent abonn) {
        try {
            //navigation
            Parent loader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/afficherparticipant.fxml")));
            typeabonnField.getScene().setRoot(loader);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void uploadImage(ActionEvent abonn)throws FileNotFoundException, IOException  {

        Random rand = new Random();
        int x = rand.nextInt(1000);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload File Path");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File file = fileChooser.showOpenDialog(null);
        String DBPath = x + ".jpg";
        if (file != null) {
            FileInputStream Fsource = new FileInputStream(file.getAbsolutePath());
            FileOutputStream Fdestination = new FileOutputStream("C:/Users/Hp/Desktop/pidev/public/images/"+DBPath);
            BufferedInputStream bin = new BufferedInputStream(Fsource);
            BufferedOutputStream bou = new BufferedOutputStream(Fdestination);
            System.out.println(file.getAbsoluteFile());
            String path=file.getAbsolutePath();
            Image img = new Image(file.toURI().toString());
            imageview.setImage(img);
            imageabonnField.setText(DBPath);
            int b = 0;
            while (b != -1) {
                b = bin.read();
                bou.write(b);
            }
            bin.close();
            bou.close();
        } else {
            System.out.println("error");
        }
    }

    @FXML
    private void excelabonn(ActionEvent abonn) {

        try{
            String filename="C:\\xampp\\htdocs\\dataabonn.xls" ;
            HSSFWorkbook hwb=new HSSFWorkbook();
            HSSFSheet sheet =  hwb.createSheet("new sheet");
            HSSFRow rowhead=   sheet.createRow((short)0);
            rowhead.createCell((short) 0).setCellValue("nom Evenement");
            rowhead.createCell((short) 1).setCellValue("type d'evenement");
            rowhead.createCell((short) 2).setCellValue("description ");
            List<evenement> evenements = Ev.recupererevenement();
            for (int i = 0; i < evenements.size(); i++) {
                HSSFRow row=   sheet.createRow((short)i);
                row.createCell((short) 0).setCellValue(evenements.get(i).getNom_ev());
                row.createCell((short) 1).setCellValue(evenements.get(i).getType_ev());
                row.createCell((short) 2).setCellValue(evenements.get(i).getDescription_ev());
//row.createCell((short) 3).setCellValue((evenements.get(i).getDate()));
                i++;
            }
            int i=1;
            FileOutputStream fileOut =  new FileOutputStream(filename);
            hwb.write(fileOut);
            fileOut.close();
            System.out.println("Your excel file has been generated!");
            File file = new File(filename);
            if (file.exists()){
                if(Desktop.isDesktopSupported()){
                    Desktop.getDesktop().open(file);
                }}
        } catch ( Exception ex ) {
            System.out.println(ex);
        }
    }

    @FXML
    private void pdfabonn(ActionEvent abonn) throws FileNotFoundException, SQLException, IOException {
        // evenement tab_Recselected = evenementTv.getSelectionModel().getSelectedItem();
        long millis = System.currentTimeMillis();
        Date DateRapport = new Date(millis);

        String DateLyoum = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH).format(DateRapport);//yyyyMMddHHmmss
        System.out.println("Date d'aujourdhui : " + DateLyoum);

        com.itextpdf.text.Document document = new com.itextpdf.text.Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(String.valueOf(DateLyoum + ".pdf")));//yyyy-MM-dd
            document.open();
            Paragraph ph1 = new Paragraph("Voici un rapport détaillé de notre application qui contient tous les Evenements . Pour chaque Evenement, nous fournissons des informations telles que la date d'Aujourd'hui :" + DateRapport );
            Paragraph ph2 = new Paragraph(".");
            PdfPTable table = new PdfPTable(4);
            //On créer l'objet cellule.
            PdfPCell cell;
            //contenu du tableau.
            table.addCell("nom_ev");
            table.addCell("type_ev");
            table.addCell("description_ev");
            table.addCell("image_ev");

            evenement r = new evenement();
            Ev.recupererevenement().forEach(new Consumer<evenement>() {
                @Override
                public void accept(evenement e) {
                    table.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(String.valueOf(e.getNom_ev()));
                    table.addCell(String.valueOf(e.getType_ev()));
                    table.addCell(String.valueOf(e.getDescription_ev()));
                    try {
                        // Créer un objet Image à partir de l'image
                        String path = e.getImage_ev();
                        com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(path);

                        // Définir la taille de l'image dans le tableau
                        img.scaleToFit(100, 100); // Définir la largeur et la hauteur de l'image

                        // Ajouter l'image à la cellule du tableau
                        PdfPCell cell = new PdfPCell(img);
                        table.addCell(cell);
                    } catch (Exception ex) {
                        table.addCell("Erreur lors du chargement de l'image");
                    }
                }
            });
            document.add(ph1);
            document.add(ph2);
            document.add(table);
        } catch (Exception e) {
            System.out.println(e);
        }
        document.close();

        ///Open FilePdf
        File file = new File(DateLyoum + ".pdf");
        Desktop desktop = Desktop.getDesktop();
        if (file.exists()) //checks file exists or not  
        {
            desktop.open(file); //opens the specified file   
        }
    }

    @FXML
    private void rechercherabonn(KeyEvent abonn) {

        evenementService bs=new evenementService();
        evenement b= new evenement();
        ObservableList<evenement>filter= bs.chercherabonn(rechercher.getText());
        populateTable(filter);
    }
    private void populateTable(ObservableList<evenement> branlist){
        evenementTv.setItems(branlist);

    }
    @FXML
    private void GoBk(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Statistics.fxml"));
        Parent root = loader.load();

        // Set the root of the current scene to the new FXML file
        GoBackBtn.getScene().setRoot(root);
    }

    @FXML
    private void goToPong(ActionEvent abonn) {
        try {
            //navigation
            Parent loader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("pppppp.fxml")));
            goToPong.getScene().setRoot(loader);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } // canvas est le nom de votre composant Canvas
    }


    public void retour(ActionEvent event)throws IOException {
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


    





    

