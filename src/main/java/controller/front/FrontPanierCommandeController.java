package controller.front;
import entities.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import service.*;

import javax.mail.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class FrontPanierCommandeController implements Initializable{
    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());
        showProduitDuPanierUser();
        facture.setText(String.format("Montant à payer (DT): %.3f", total));

    }
    @FXML
    private TableView<PanierProduit> panierTable;
    @FXML
    private TextField code;
    @FXML
    private void passerCommandeWithCode(ActionEvent actionEvent) throws SQLException {
        appliquerCodePromoEtPasserCommande();
    }
    @FXML
    private Label facture;
    private Connection conn;
    private PreparedStatement pst;
    private Statement statement;
    private final ProduitService ps = new ProduitService();
    private ProduitService pss = new ProduitService();
    private CommandeService commandeService=new CommandeService();

    @FXML
    private TableColumn<PanierProduit, String> tabProduitcart;

    @FXML
    private TableColumn<PanierProduit, HBox> tabDeletePanierr;
    PanierService pns = new PanierService();
    PanierProduitService pps=new PanierProduitService();
    ObservableList<PanierProduit> list1 = FXCollections.observableArrayList();
    @FXML
    private Button commandeButton;

    float total;
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void showProduitDuPanierUser() {
        facture.setText(String.format("Montant à payer (DT): %.3f", total));
        Panier panier = pns.selectPanierParUserId(userData.getIdUser());
        total = pps.facture(panier);
        list1 = pps.getProduitsDuPanierUtilisateur(panier);
        tabProduitcart.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PanierProduit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PanierProduit, String> param) {
                String nomProduit = param.getValue().getProduit().getNomProduit();
                return new SimpleStringProperty(nomProduit);
            }

        });
        tabDeletePanierr.setCellFactory(column -> new TableCell<PanierProduit, HBox>() {
            private final Button DeleteButton = new Button("Supprimer");

            {
                DeleteButton.getStyleClass().add("addbuttonPanier");
                DeleteButton.setOnAction(event -> {
                    PanierProduit pn = getTableView().getItems().get(getIndex());
                    Panier panier = pns.selectPanierParUserId(userData.getIdUser());
                    pps.DeleteProduitAuPanier(panier,pn.getProduit().getIdProduit());
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Avertissement");
                    alert.setHeaderText(null);
                    alert.setContentText("produit supprimé de votre panier.");
                    alert.showAndWait();
                    showProduitDuPanierUser();
                });
            }

            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    // Créez un conteneur HBox pour le bouton et l'image
                    HBox hbox = new HBox(DeleteButton);

                    setGraphic(hbox);
                }
            }

        });
        panierTable.setItems(list1);
    }

    @FXML
    public void passerCommande(ActionEvent actionEvent) throws SQLException {
        Panier panier = pns.selectPanierParUserId(userData.getIdUser());
        commandeService.ajouterCommande(panier);
        String subject = "Confirmation Commande ESPRIT MARKET";
        String body = "Cher Client  "+userData.getNomUser()+", Votre commande a été traitée avec succès et est en cours de préparation.";
        sendEmail(userData.getEmailUser(), subject, body); // Envoyer l'email
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Succes");
        a.setContentText("Commande passée avec succées ! Check your Email");

        a.showAndWait();
    }

    private void sendEmail(String to, String subject, String body) {
        String username = "medoussemaboussida@gmail.com";
        String password = "wmgq nkfy btsz ubdf";
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Change this to your SMTP server host(yahoo...)
        props.put("mail.smtp.port", "587"); // Change this to your SMTP server port
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        jakarta.mail.Session session;
        session = jakarta.mail.Session.getInstance(props,new jakarta.mail.Authenticator() {
            protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                return new jakarta.mail.PasswordAuthentication(username, password);
            }
        });


        try {
            // Create a MimeMessage object

            // Create a new message
            jakarta.mail.internet.MimeMessage message = new MimeMessage(session);
            // Set the From, To, Subject, and Text fields of the message
            message.setFrom(new jakarta.mail.internet.InternetAddress(username));
            message.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);

            // Send the message using Transport.send
            jakarta.mail.Transport.send(message);

            System.out.println("Email sent successfully");
        } catch (MessagingException ex) {
            System.err.println("Failed to send email: " + ex.getMessage());
        }

    }

    private void appliquerCodePromoEtPasserCommande() throws SQLException {
        Panier panier = pns.selectPanierParUserId(userData.getIdUser());

        String codePromoSaisi = code.getText();
        int reduction = 0;

        // Vérifier si le champ de saisie du code promo est vide
        if (codePromoSaisi.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez saisir un code promo.");
            alert.showAndWait();
            return; // Arrêter le traitement
        }

        // Recherche dans la base de données pour trouver le code promo correspondant
        CodePromoService codePromoService = new CodePromoService();
        List<CodePromo> codesPromo = codePromoService.readCodePromo();
        boolean codePromoExiste = false;
        for (CodePromo codePromo : codesPromo) {
            if (codePromo.getCode().equals(codePromoSaisi)) {
                reduction = codePromo.getReductionAssocie();
                codePromoExiste = true;
                break;
            }
        }

        // Si le code promo n'existe pas dans la base de données, afficher une alerte
        if (!codePromoExiste) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Le code promo saisi n'existe pas.");
            alert.showAndWait();
            return; // Arrêter le traitement
        }

        // Appliquer la réduction si elle existe
        float montantFinal;
        if (reduction > 0) {
            montantFinal = total * (100 - reduction) / 100;
        } else {
            montantFinal = total;
        }
// Formatter le montant final avec trois chiffres après la virgule
        String montantFinalFormatted = String.format("%.3f", montantFinal);

// Passer la commande
        commandeService.ajouterCommande(panier);

// Afficher un message de succès avec le montant final formaté
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Succes");
        a.setContentText("Code promo est activé, commande passée avec succès. Montant final = " + montantFinalFormatted + " dt au lieu de " + String.format("%.3f", total) + " dt");
        a.showAndWait();
    }



}

