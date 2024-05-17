/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.front;

import controller.back.AjouterevenementController;
import entities.evenement;
import entities.participant;
import service.evenementService;
import service.participantService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * FXML Controller class
 *
 * @author asus
 */
public class AfficherparticipantController implements Initializable {

    @FXML
    private TableView<participant> tableparticipant;
     evenementService ab=new evenementService();
    @FXML
    private TableColumn<participant, Integer> iduserTv;
    @FXML
    private TableColumn<participant, Integer> idabonnTv;
    @FXML
    private TableColumn<participant, Date> datePartTv;
    @FXML
    private Button modifierPartBtn;
    @FXML
    private Button supprimerPartBtn;
     @FXML
    private Button ajouter;
    @FXML
    private TextField idread;
    @FXML
    private TextField iduserField;
    @FXML
    private TextField idabonnField;
    @FXML
    private DatePicker datepartField;
    @FXML
    private TextField chercherabonnField;
    
    participantService Ps=new participantService();
    @FXML
    private TextField datepartField1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        getParticipant();
    }    
     @FXML
    private void ajouterevenement(ActionEvent abonn) {
      try {
            //navigation
            Parent loader = FXMLLoader.load(getClass().getResource("/ajouterevenement.fxml"));
            chercherabonnField.getScene().setRoot(loader);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    @FXML
    private void rechercherevenement(KeyEvent abonn) {
        try {
            List<evenement> evenements = ab.chercherabonn(chercherabonnField.getText());
            
            int row = 0;
            int column = 0;
            for (int i = 0; i < evenements.size(); i++) {
                //chargement dynamique d'une interface
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/evenement.fxml"));
                AnchorPane pane = loader.load();         
                //passage de parametres
                evenementController controller = loader.getController();
                controller.setevenement(evenements.get(i));
                controller.setIdabonn(evenements.get(i).getId_ev());
                
                if(evenements.get(i).getCode_participant()<=0)
                {
                 // ab.supprimerevenement(evenements.get(i));
                controller.arreterabonn();
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }   
    }
    @FXML
    private void modifierparticipant(ActionEvent abonn) throws SQLException {
        
         participant pa = new participant();
        pa.setId_participant(Integer.valueOf(idread.getText()));
        pa.setId_evenement(Integer.valueOf(idabonnField.getText()));
        pa.setId_user(Integer.valueOf(iduserField.getText()));
            Date d=Date.valueOf(datepartField.getValue());
        pa.setDate_part(d);
        //pa.setDate_part(datepartField.getText());
       
        Ps.modifierparticipant(pa);
        resetPart();
        getParticipant();
           
        
    }

    @FXML
    private void supprimerparticipant(ActionEvent abonn) {
         participant p = tableparticipant.getItems().get(tableparticipant.getSelectionModel().getSelectedIndex());
      
        try {
            Ps.Deleteparticipant(p);
        } catch (SQLException ex) {
            Logger.getLogger(AjouterevenementController.class.getName()).log(Level.SEVERE, null, ex);
        }   
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information ");
        alert.setHeaderText("participant delete");
        alert.setContentText("participant deleted successfully!");
        alert.showAndWait();
        getParticipant();
     
    }

    @FXML
    private void choisirparticipant(ActionEvent abonn) {
        participant part = tableparticipant.getItems().get(tableparticipant.getSelectionModel().getSelectedIndex());
        
        idread.setText(String.valueOf(part.getId_participant()));
        idabonnField.setText(String.valueOf(part.getId_evenement()));
        iduserField.setText(String.valueOf(part.getId_user())); 
        datepartField1.setText(String.valueOf(part.getDate_part()));
        //datepartField.setValue((part.getDate_part()));
        
    }
    
    
    public void getParticipant(){
        try {
       

           // TODO
            List<participant> part = Ps.recupererParticipant();
            ObservableList<participant> olp = FXCollections.observableArrayList(part);
            tableparticipant.setItems(olp);
            iduserTv.setCellValueFactory(new PropertyValueFactory("id_user"));
            idabonnTv.setCellValueFactory(new PropertyValueFactory("id_evenement"));
            datePartTv.setCellValueFactory(new PropertyValueFactory("date_part"));
            // this.delete();
        } catch (SQLException ex) {
            System.out.println("error" + ex.getMessage());
        }
    }
    
    public void resetPart() {
        idread.setText("");
        idabonnField.setText("");
        iduserField.setText("");
        datepartField.setValue(null);
        
    }
   
    
}


 