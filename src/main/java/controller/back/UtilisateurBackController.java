package controller.back;

import entities.Produit;
import entities.Utilisateur;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import service.UtilisateurService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UtilisateurBackController implements Initializable {

    @FXML
    private TableColumn<Utilisateur, String> emailUser;

    @FXML
    private TableColumn<Utilisateur, String> nomUser;

    @FXML
    private TableColumn<Utilisateur, String> prenomUser;

    @FXML
    private TableView<Utilisateur> tableUserBack;

    @FXML
    private TableColumn<Utilisateur, Integer> telUser;

    @FXML
    private TextField rechercherUser;
    @FXML
    private ComboBox<String> comboUser;
    ObservableList<Utilisateur> list = FXCollections.observableArrayList();
private UtilisateurService us=new UtilisateurService();
    private Utilisateur userData;

    public void setUserData(Utilisateur user) {
        this.userData = user;
        // Now you can use this.userData to access the user's information in your controller
        System.out.println("Received User ID: " + user.getIdUser());
        System.out.println("Received User Name: " + user.getNomUser());
        System.out.println("Received User Email: " + user.getEmailUser());

    }
@Override
public void initialize(URL url, ResourceBundle rb)
{

    showUser();
    comboUser.getItems().removeAll(comboUser.getItems());
    comboUser.getItems().addAll("Trier", "Trier par nom ↑", "Trier par nom ↓" ,"Trier par prenom ↓","Trier par prenom ↑");
    comboUser.getSelectionModel().select("Trier");
}
    public void showUser()
    {
        nomUser.setCellValueFactory(new PropertyValueFactory<>("nomUser"));
        prenomUser.setCellValueFactory(new PropertyValueFactory<>("prenomUser"));
        emailUser.setCellValueFactory(new PropertyValueFactory<>("emailUser"));
        telUser.setCellValueFactory(new PropertyValueFactory<>("numeroTel"));
        list = us.userBack();
        tableUserBack.setItems(list);
    }

    @FXML
    public void retourBackMenu(ActionEvent event) throws IOException {
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

    @FXML
    public void searchUser(KeyEvent keyEvent) {
        FilteredList<Utilisateur> filter = new FilteredList<>(list, ev -> true);

        rechercherUser.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate(t -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Recherche dans le nom du produit
                boolean nomUser = t.getNomUser().toLowerCase().startsWith(lowerCaseFilter);

                // Recherche dans le nom de la catégorie
                boolean prenomUser = t.getPrenomUser().toLowerCase().startsWith(lowerCaseFilter);

                // Recherche dans le prix (ajustez selon vos besoins)
                boolean numTel = String.valueOf(t.getNumeroTel()).startsWith(lowerCaseFilter);
                // Combiner les conditions avec des opérateurs logiques (par exemple, ET (&&) ou OU (||))
                return nomUser || prenomUser || numTel;
            });
        });

        SortedList<Utilisateur> sort = new SortedList<>(filter);
        sort.comparatorProperty().bind(tableUserBack.comparatorProperty());
        tableUserBack.setItems(sort);
    }
    private List<Utilisateur> temp;

    @FXML
    public void sortUser(ActionEvent event) {
        String selected = comboUser.getSelectionModel().getSelectedItem();
        if (selected.equals("Trier par nom ↑")) {
            temp = us.sortUserNomAsc();

        } else if (selected.equals("Trier par nom ↓")) {
            temp = us.sortUserNomDesc();

        }else  if (selected.equals("Trier par prenom ↑")) {
            temp = us.sortUserPrenomAsc();

        } else if (selected.equals("Trier par prenom ↓")) {
            temp = us.sortUserPrenomDesc();

        }
        // Mettez à jour la liste observable utilisée par votre TableView (par exemple, 'list')
        ObservableList<Utilisateur> updatedList = FXCollections.observableArrayList(temp);

        // Mettre à jour la TableView
        tableUserBack.setItems(updatedList);
    }
}
