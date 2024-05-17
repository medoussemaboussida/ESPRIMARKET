/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.Utilisateur;
import entities.evenement;
import entities.participant;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asus
 */
public class participantService {

    Connection cnx;
    public Statement ste;
    public PreparedStatement pst;

    public participantService() {

        cnx = DataSource.getInstance().getCnx();
    }

    public void ajouterparticipant(participant p) {
        Utilisateur U = new Utilisateur();
        evenementService es = new evenementService();
        String requete = "INSERT INTO `participant` (`date_part`,`id_evenement` ,`id_user`) VALUES(? ,?,?) ;";

        try {
            evenement tempabonn = es.FetchOneabonn(p.getId_evenement());
            System.out.println("before" + tempabonn);
            tempabonn.setCode_participant(tempabonn.getCode_participant() - 1);
            tempabonn.setCode_participant(Math.max(tempabonn.getCode_participant() - 1, 0));
            es.modifierevenement(tempabonn);
            int new_id = tempabonn.getId_ev();
            p.setEvenement(tempabonn);
            System.out.println("after" + tempabonn);

            pst = (PreparedStatement) cnx.prepareStatement(requete);
            pst.setDate(1, p.getDate_part());
            pst.setInt(2, p.getId_evenement());
            pst.setInt(3, p.getId_user());
            pst.executeUpdate();
          

            System.out.println("participant with id abonn = " + p.getId_evenement() + " is added successfully");

        } catch (SQLException ex) {
            System.out.println("error in adding reservation");
            Logger.getLogger(participantService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<participant> recupererParticipant() throws SQLException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        List<participant> particip = new ArrayList<>();
        String s = "select * from participant";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(s);
        while (rs.next()) {
            participant pa = new participant();
            pa.setId_participant(rs.getInt("id_participant"));
            pa.setId_user(rs.getInt("id_user"));
            pa.setId_evenement(rs.getInt("id_evenement"));
            pa.setDate_part(rs.getDate("date_part"));

            particip.add(pa);

        }
        return particip;
    }

    public void supprimerparticipant(participant p) throws SQLException {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String req = "delete from participant where id_participant  = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, p.getId_participant());
        ps.executeUpdate();
        System.out.println("participant with id= " + p.getId_participant() + "  is deleted successfully");
    }

    public participant FetchOneRes(int id) throws SQLException {
        participant r = new participant();
        String requete = "SELECT * FROM `participant` where id_participant=" + id;

        try {
            ste = (Statement) cnx.createStatement();
            ResultSet rs = ste.executeQuery(requete);

            while (rs.next()) {

                r = new participant(rs.getInt("id_participant"), rs.getDate("date_part"), rs.getInt("id_user"), rs.getInt("id_evenement"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(evenementService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r;
    }

    public void Deleteparticipant(participant p) throws SQLException {
        evenementService es = new evenementService();
        participantService rs = new participantService();

        participant r = rs.FetchOneRes(p.getId_participant());

        String requete = "delete from participant where id_participant=" + p.getId_participant();
        try {
            evenement tempabonn = es.FetchOneabonn(r.getId_evenement());
            System.out.println("before" + tempabonn);
            tempabonn.setCode_participant(tempabonn.getCode_participant() + 1);
            es.modifierevenement(tempabonn);
            System.out.println("after" + tempabonn);
            pst = (PreparedStatement) cnx.prepareStatement(requete);
            //pst.setInt(1, id);

            pst.executeUpdate();
            System.out.println("participant with id=" + p.getId_participant() + " is deleted successfully");
        } catch (SQLException ex) {
            System.out.println("error in delete participant " + ex.getMessage());
        }
    }
    
    public void modifierparticipant(participant p) throws SQLException {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String req = "UPDATE participant SET id_user = ?,id_evenement = ?,date_part=? where id_participant = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, p.getId_user());
        ps.setInt(2, p.getId_evenement());
        ps.setDate(3, p.getDate_part());
        ps.setInt(4, p.getId_participant());

        ps.executeUpdate();
    }

}
