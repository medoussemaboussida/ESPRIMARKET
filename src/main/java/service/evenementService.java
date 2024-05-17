/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

//import com.sun.javafx.iio.ImageStorage.ImageType;

import entities.evenement;
import utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asus
 */
public class evenementService implements IevenementService<evenement> {

    Connection cnx;
    public Statement ste;
    public PreparedStatement pst;

    public evenementService() {
        cnx = DataSource.getInstance().getCnx();

    }

    @Override
    public void ajouterevenement(evenement e) throws SQLException {

        String requete = "INSERT INTO `evenement` (`nom_ev`,`type_ev`,`image_ev`,`description_ev`,`date`,`code_participant`) "
                + "VALUES (?,?,?,?,?,?);";
        try {
            pst = (PreparedStatement) cnx.prepareStatement(requete);
            pst.setString(1, e.getNom_ev());
            pst.setString(2, e.getType_ev());
            pst.setString(3, e.getImage_ev());
            pst.setString(4, e.getDescription_ev());
            pst.setDate(5, e.getDate());
            pst.setInt(6, e.getCode_participant());
            pst.executeUpdate();
            System.out.println("abonn " + e.getNom_ev() + " added successfully");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public void modifierevenement(evenement e) throws SQLException {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String req = "UPDATE evenement SET nom_ev = ?,type_ev = ?,image_ev=?,description_ev = ?,date=?,code_participant=? where id_ev = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, e.getNom_ev());
        ps.setString(2, e.getType_ev());
        ps.setString(3, e.getImage_ev());
        ps.setString(4, e.getDescription_ev());
        ps.setDate(5, e.getDate());
        ps.setInt(6, e.getCode_participant());
        ps.setInt(7, e.getId_ev());
        ps.executeUpdate();
    }

    @Override
    public void supprimerevenement(evenement e) throws SQLException {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String req = "delete from evenement where id_ev = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, e.getId_ev());
        ps.executeUpdate();
        System.out.println("abonn with id= " + e.getId_ev() + "  is deleted successfully");
    }

    @Override
    public List<evenement> recupererevenement() throws SQLException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        List<evenement> evenements = new ArrayList<>();
        String s = "select * from evenement";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(s);
        while (rs.next()) {
            evenement e = new evenement();
            e.setNom_ev(rs.getString("nom_ev"));
            e.setType_ev(rs.getString("type_ev"));
            e.setImage_ev(rs.getString("Image_ev"));
            e.setDescription_ev(rs.getString("description_ev"));
            e.setDate(rs.getDate("date"));
            e.setCode_participant(rs.getInt("code_participant"));
            e.setId_ev(rs.getInt("id_ev"));

            evenements.add(e);

        }
        return evenements;
    }

    public evenement FetchOneabonn(int id) {
        evenement abonn = new evenement();
        String requete = "SELECT * FROM `evenement` where id_ev = " + id;

        try {
            ste = (Statement) cnx.createStatement();
            ResultSet rs = ste.executeQuery(requete);

            while (rs.next()) {

                abonn = new evenement(rs.getInt("id_ev"), rs.getInt("code_participant"), rs.getString("nom_ev"), rs.getString("type_ev"), rs.getString("image_ev"), rs.getString("description_ev"), rs.getDate("date"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(evenementService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return abonn;
    }

    public ObservableList<evenement> Fetchabonns() {
        ObservableList<evenement> abonns = FXCollections.observableArrayList();
        String requete = "SELECT * FROM `evenement`";
        try {
            ste = (Statement) cnx.createStatement();
            ResultSet rs = ste.executeQuery(requete);

            while (rs.next()) {
                abonns.add(new evenement(rs.getInt("id_ev"), rs.getInt("code_participant"), rs.getString("nom_ev"), rs.getString("type_ev"), rs.getString("image_ev"), rs.getString("description_ev"), rs.getDate("date")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(evenementService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return abonns;
    }

    public String GenerateQrabonn(evenement abonn) throws FileNotFoundException, IOException {
        String abonnName = "abonn name: " + abonn.getNom_ev() + "\n" + "abonn date: " + abonn.getDate() + "\n" + "abonn description: " + abonn.getDescription_ev() + "\n";
        ByteArrayOutputStream out = QRCode.from(abonnName).to(ImageType.JPG).stream();
        String filename = abonn.getNom_ev() + "_QrCode.jpg";
        //File f = new File("src\\utils\\img\\" + filename);
                File f = new File("C:\\xampp\\htdocs\\xchangex\\imgQr\\qrcode" + filename);
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(out.toByteArray());
        fos.flush();
       
        System.out.println("qr yemshi");
        return filename;
    }
    

    public ObservableList<evenement> chercherabonn(String chaine) {
        String sql = "SELECT * FROM evenement WHERE (nom_ev LIKE ? or type_ev LIKE ?  ) order by nom_ev ";
        //Connection cnx= Maconnexion.getInstance().getCnx();
        String ch = "%" + chaine + "%";
        ObservableList<evenement> myList = FXCollections.observableArrayList();
        try {

            Statement ste = cnx.createStatement();
            // PreparedStatement pst = myCNX.getCnx().prepareStatement(requete6);
            PreparedStatement stee = cnx.prepareStatement(sql);
            stee.setString(1, ch);
            stee.setString(2, ch);

            ResultSet rs = stee.executeQuery();
            while (rs.next()) {
                evenement e = new evenement();

                e.setNom_ev(rs.getString("nom_ev"));
                e.setType_ev(rs.getString("type_ev"));
                e.setImage_ev(rs.getString("Image_ev"));
                e.setDescription_ev(rs.getString("description_ev"));
                e.setDate(rs.getDate("date"));
                e.setCode_participant(rs.getInt("code_participant"));
                e.setId_ev(rs.getInt("id_ev"));

                myList.add(e);
                System.out.println("abonn trouvé! ");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return myList;
    }

    public List<evenement> trierabonn()throws SQLException {
        List<evenement> evenements = new ArrayList<>();
        String s = "select * from evenement order by nom_ev ";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(s);
        while (rs.next()) {
            evenement e = new evenement();
            e.setNom_ev(rs.getString("nom_ev"));
            e.setType_ev(rs.getString("type_ev"));
            e.setImage_ev(rs.getString("Image_ev"));
            e.setDescription_ev(rs.getString("description_ev"));
            e.setDate(rs.getDate("date"));
            e.setCode_participant(rs.getInt("code_participant"));
            e.setId_ev(rs.getInt("id_ev"));
            evenements.add(e);
        }
        return evenements;
    }
   

}
