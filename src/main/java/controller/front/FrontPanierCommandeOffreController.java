package controller.front;

import javafx.fxml.Initializable;
import entities.*;
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

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;
public class FrontPanierCommandeOffreController implements Initializable {

    @FXML
    private TableView<PanierProduit> panierTable;

    private Connection conn;
    private PreparedStatement pst;
    private Statement statement;
    private final ProduitService ps = new ProduitService();
    private ProduitService pss = new ProduitService();
    private CommandeService commandeService=new CommandeService();

    @FXML
    private TextField code;

    @FXML
    private void passerCommande(ActionEvent actionEvent) throws SQLException {
        appliquerCodePromoEtPasserCommande();
    }
    @FXML
    private TableColumn<PanierProduit, String> tabProduitcart;

    @FXML
    private TableColumn<PanierProduit, HBox> tabDeletePanierr;
    PanierService pns = new PanierService();
    PanierProduitService pps=new PanierProduitService();
    ObservableList<PanierProduit> list1 = FXCollections.observableArrayList();
    @FXML
    private Button commandeButton;
    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());
        showProduitDuPanierUser();

    }

    //selectionner la panier de user qui est connecté

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void showProduitDuPanierUser() {

        Panier panier = pns.selectPanierParUserId(userData.getIdUser());

        list1 = pps.getProduitsDuPanierUtilisateur(panier);

        tabProduitcart.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PanierProduit, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PanierProduit, String> param) {
                String nomProduit = param.getValue().getProduit().getNomProduit();
                return new SimpleStringProperty(nomProduit);
            }

        });


        tabDeletePanierr.setCellFactory(column -> new TableCell<PanierProduit, HBox>() {
            private final Button DeleteButton = new Button("Delete");

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
    private float calculerMontantTotal() {
        float montantTotal = 0;
        for (PanierProduit panierProduit : list1) {
            montantTotal += panierProduit.getProduit().getPrix();
        }
        return montantTotal;
    }
    private void appliquerCodePromoEtPasserCommande() throws SQLException {
        Panier panier = pns.selectPanierParUserId(userData.getIdUser());

        String codePromoSaisi = code.getText();
        int reduction = 0;

        // Vérifier si le champ du code promo est vide
        if (!codePromoSaisi.isEmpty()) {
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
                alert.setContentText("Le code promo saisi est incorrect.");
                alert.showAndWait();
                return; // Arrêter le traitement
            }
        }

        // Calcul du montant total des produits dans le panier
        float montantTotal = calculerMontantTotal();

        // Appliquer la réduction si elle existe
        float montantFinal;
        if (reduction > 0) {
            montantFinal = montantTotal * (100 - reduction) / 100;
        } else {
            montantFinal = montantTotal;
        }

        // Passer la commande
        commandeService.ajouterCommande(panier);

        // Afficher un message de succès
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Succes");
        a.setContentText("Commande passée avec succès. Montant final : " + montantFinal);
        a.showAndWait();
    }

}
