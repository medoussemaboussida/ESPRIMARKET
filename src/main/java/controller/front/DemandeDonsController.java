package controller.front;

import entities.DemandeDons;
import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.DemandeDonsService;
import service.PDFExporterService;
import service.UtilisateurService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class DemandeDonsController {


    @FXML
    private TextArea contenuTextArea;

    @FXML
    private Button posterDemandeButton;

    @FXML
    private ImageView imageView;

    @FXML
    private ListView<DemandeDons> demandeListView;

    private DemandeDonsService demandeDonsService;
    private UtilisateurService userService;
    private entities.Utilisateur utilisateur;
    private PDFExporterService pdfExporterService;

    private BarcodeGenerator barcodeGenerator;
    private Utilisateur userData;
    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());
    }

    public DemandeDonsController() {
        userService=new UtilisateurService();
        demandeDonsService = new DemandeDonsService();
        pdfExporterService = new PDFExporterService(); // Ajoutez cette ligne pour initialiser pdfExporterService
        barcodeGenerator = new BarcodeGenerator();

    }
    @FXML
    public void initialize() {
        // Personnaliser l'affichage des demandes dans la ListView
        demandeListView.setCellFactory(param -> new ListCell<DemandeDons>() {
            @Override
            protected void updateItem(DemandeDons demande, boolean empty) {
                super.updateItem(demande, empty);
                if (empty || demande == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Construire le texte à afficher dans la cellule
                    StringBuilder sb = new StringBuilder();
                    sb.append("Utilisateur: ").append(demande.getNomUser()).append(" ").append(demande.getPrenomUser());
                    sb.append("\nContenu: ").append(demande.getContenu());
                    sb.append("\nDate de publication: ").append(demande.getDatePublication());
                    sb.append("\nPoints gagnés: ").append(demande.getNbPoints());

                    // Créer un bouton Supprimer avec une icône
                    Button deleteButton = new Button("Supprimer");
                    deleteButton.getStyleClass().add("addbuttonPanier");
                    ImageView deleteImageView = new ImageView(new Image(getClass().getResourceAsStream("/img/deleteimg.png")));
                    deleteImageView.setFitWidth(16);
                    deleteImageView.setFitHeight(16);
                    deleteButton.setGraphic(deleteImageView);

                    // Vérifier si l'utilisateur connecté est l'auteur de la demande
                    if (demande.getIdUtilisateur() == userData.getIdUser()) {
                        deleteButton.setOnAction(event -> deleteDemande(demande));
                    } else {
                        // Si l'utilisateur n'est pas l'auteur de la demande, ne pas créer le bouton Supprimer
                        deleteButton.setVisible(false);
                        deleteButton.setManaged(false);
                    }

                    // Vérifier si l'utilisateur connecté est l'auteur de la demande pour afficher le bouton "Exporter en PDF"
                    if (demande.getIdUtilisateur() == userData.getIdUser()) {
                        // Créer un bouton Exporter en PDF
                        Button exportButton = new Button("Exporter en PDF");
                        exportButton.getStyleClass().add("addbuttonPanier");

                        ImageView exportImageView = new ImageView(new Image(getClass().getResourceAsStream("/img/exporter.png")));
                        exportImageView.setFitWidth(16);
                        exportImageView.setFitHeight(16);
                        exportButton.setGraphic(exportImageView);
                        exportButton.setOnAction(event -> exportDemandToPDF(demande));

                        // Créer un conteneur pour afficher le texte et les boutons
                        VBox container = new VBox(new Label(sb.toString()), deleteButton, exportButton);
                        setGraphic(container);

                        // Créer le code à barres pour la demande
                        String barcodeText = demande.getNomUser() + " - Points: " + demande.getNbPoints();
                        try {
                            byte[] barcodeImageBytes = BarcodeGenerator.generateBarcode(barcodeText);
                            Image barcodeImage = new Image(new ByteArrayInputStream(barcodeImageBytes));
                            ImageView barcodeImageView = new ImageView(barcodeImage);
                            // Ajouter barcodeImageView à votre mise en page où vous voulez afficher le code à barres
                            // Par exemple, vous pouvez l'ajouter à un VBox :
                            VBox barcodeContainer = new VBox(new Label("Code à barres"), barcodeImageView);
                            container.getChildren().add(barcodeContainer);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Gérer l'exception
                        }
                    } else {
                        // Créer un conteneur pour afficher le texte et le bouton Supprimer
                        VBox container = new VBox(new Label(sb.toString()), deleteButton);
                        setGraphic(container);
                    }
                }
            }
        });
        // Charger les demandes existantes lors de l'initialisation
        loadDemandes();

        // Ajouter un écouteur d'événement au bouton "Poster Demande"
        posterDemandeButton.setOnAction(event -> posterDemande());
    }



    @FXML
    private void exportDemandToPDF(DemandeDons demande) {
        // Afficher un sélecteur de fichier pour choisir l'emplacement de sauvegarde du fichier PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("demande_" + demande.getIdDemande() + ".pdf");
        Stage stage = (Stage) demandeListView.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        // Si un fichier est sélectionné, exporter la demande en PDF
        if (file != null) {
            boolean success = pdfExporterService.exportDemandToPDF(demande, file);
            if (success) {
                afficherAlerte("Succès", "La demande a été exportée avec succès en PDF.");
            } else {
                afficherAlerte("Erreur", "Une erreur est survenue lors de l'exportation en PDF.");
            }
        }
    }





    private void deleteDemande(DemandeDons demande) {
        // Vérifier si l'utilisateur connecté est l'auteur de la demande
        if (demande.getIdUtilisateur() == userData.getIdUser()) {
            // Confirmer la suppression avec une boîte de dialogue
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirmation de suppression");
            confirmationDialog.setHeaderText("Voulez-vous vraiment supprimer cette demande ?");
            confirmationDialog.setContentText("Cette action est irréversible.");

            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Appeler le service pour supprimer la demande
                if (demandeDonsService.supprimerDemande(demande.getIdDemande())) {
                    // Recharger les demandes après la suppression
                    loadDemandes();
                } else {
                    afficherAlerte("Erreur", "Erreur lors de la suppression de la demande.");
                }
            }
        } else {
            afficherAlerte("Erreur", "Vous n'êtes pas autorisé à supprimer cette demande.");
        }
    }



    @FXML
    public void posterDemande() {
        // Vérifier si un utilisateur est connecté
        if (userData.getIdUser() != 0) {
            // Récupérer le contenu de la demande
            String contenu = contenuTextArea.getText();

            // Créer un objet DemandeDons avec les informations
            DemandeDons nouvelleDemande = new DemandeDons();
            nouvelleDemande.setIdUtilisateur(userData.getIdUser()); // Utiliser l'ID de l'utilisateur connecté
            nouvelleDemande.setContenu(contenu);

            // Poster la demande en utilisant le service
            demandeDonsService.posterDemande(nouvelleDemande);

            // Réinitialiser le contenu du TextArea après la soumission de la demande
            contenuTextArea.clear();

            // Recharger les demandes pour afficher la nouvelle demande
            loadDemandes();
        } else {
            // Afficher un message d'erreur si aucun utilisateur n'est connecté
            afficherAlerte("Erreur", "Aucun utilisateur connecté.");
        }
    }


    @FXML
    public void transferPoints() {
        if (userData != null) {
            DemandeDons selectedDemande = demandeListView.getSelectionModel().getSelectedItem();
            if (selectedDemande != null) {
                // Demander à l'utilisateur le nombre de points à transférer
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Transférer des points");
                dialog.setHeaderText("Transférer des points à l'utilisateur");
                dialog.setContentText("Entrez le nombre de points à transférer:");
                Optional<String> result = dialog.showAndWait();

                // Vérifier si l'utilisateur a saisi un nombre valide
                if (result.isPresent()) {
                    try {
                        int donPoints = Integer.parseInt(result.get());
                        if (donPoints > 0) {
                            int idDemande = selectedDemande.getIdDemande(); // Récupérer l'ID de la demande
                            // Vérifier si l'utilisateur a suffisamment de points
                            int userPoints = demandeDonsService.getUserPoints(userData.getIdUser());
                            if (userPoints >= donPoints) {
                                // Ajouter les points transférés à l'utilisateur qui a fait la demande
                                if (userData!= null) {
                                    int updatedReceiverPoints = userData.getNbPoints() + donPoints;
                                    userService.updateUserPoints(userData.getIdUser(), updatedReceiverPoints);
                                } else {
                                    afficherAlerte("Erreur", "Utilisateur non trouvé.");
                                    return;
                                }

                                // Soustraire les points transférés des points disponibles de l'utilisateur qui transfère
                                int updatedSenderPoints = userData.getNbPoints() - donPoints;
                                userService.updateUserPoints(userData.getIdUser(), updatedSenderPoints);

                                // Appeler la méthode pour ajouter les dons pour la demande sélectionnée
                                int donId = demandeDonsService.addDonsForDemande(userData.getIdUser(), donPoints, idDemande);

                                // Mettre à jour les points gagnés dans la demande sélectionnée en ajoutant les nouveaux points aux points existants
                                if (donId != -1) { // Vérifier si l'ajout des dons a réussi
                                    selectedDemande.setNbPoints(selectedDemande.getNbPoints() + donPoints);
                                    selectedDemande.setTotalPointsGagnes(selectedDemande.getTotalPointsGagnes() + donPoints);

                                    // Rafraîchir l'affichage des demandes pour refléter les modifications
                                    loadDemandes();
                                } else {
                                    afficherAlerte("Erreur", "Erreur lors de l'ajout des points pour la demande.");
                                }
                            } else {
                                afficherAlerte("Erreur", "Points insuffisants. Vous avez actuellement " + userPoints + " points.");
                            }
                        } else {
                            afficherAlerte("Erreur", "Veuillez saisir un nombre valide de points.");
                        }
                    } catch (NumberFormatException e) {
                        afficherAlerte("Erreur", "Veuillez saisir un nombre entier valide.");
                    }
                }
            } else {
                afficherAlerte("Erreur", "Veuillez sélectionner une demande pour transférer des points.");
            }
        } else {
            afficherAlerte("Erreur", "Utilisateur expéditeur non trouvé.");
        }
    }






    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    private void loadDemandes() {
        demandeListView.getItems().clear();
        demandeListView.getItems().addAll(demandeDonsService.getDemandesAvecUtilisateurs());
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
