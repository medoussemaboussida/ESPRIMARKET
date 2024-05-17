/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.sql.Date;

/**
 *
 * @author asus
 */
public class evenement {

   
        private int id_ev;
        private int code_participant;
    private String nom_ev,type_ev,image_ev,description_ev;
    private Date date;

    public evenement() {
    }

    public evenement(int id_ev, int code_participant, String nom_ev, String type_ev, String image_ev, String description_ev, Date date) {
        this.id_ev = id_ev;
        this.code_participant=code_participant;
        this.nom_ev = nom_ev;
        this.type_ev = type_ev;
        this.image_ev = image_ev;
        this.description_ev = description_ev;
        this.date = date;
    }
    public evenement(int code_participant, String nom_ev, String type_ev, String image_ev, String description_ev, Date date) {
        this.code_participant=code_participant;
        this.nom_ev = nom_ev;
        this.type_ev = type_ev;
        this.image_ev = image_ev;
        this.description_ev = description_ev;
        this.date = date;
    }
    
    
     public evenement(int id_ev, int code_participant, String nom_ev, String type_ev, String image_ev, String description_ev) {
        this.id_ev = id_ev;
        this.code_participant=code_participant;
        this.nom_ev = nom_ev;
        this.type_ev = type_ev;
        this.image_ev = image_ev;
        this.description_ev = description_ev;
        
    }
    
    
     //****************** getters ****************

    public int getId_ev() {
        return id_ev;
    }

    public String getNom_ev() {
        return nom_ev;
    }

    public String getType_ev() {
        return type_ev;
    }

    public String getImage_ev() {
        return image_ev;
    }

    public String getDescription_ev() {
        return description_ev;
    }

    public Date getDate() {
        return date;
    }
    
    
    //****************** setters ****************

    public void setId_ev(int id_ev) {
        this.id_ev = id_ev;
    }

    public void setNom_ev(String nom_ev) {
        this.nom_ev = nom_ev;
    }

    public void setType_ev(String type_ev) {
        this.type_ev = type_ev;
    }

    public void setImage_ev(String image_ev) {
        this.image_ev = image_ev;
    }

    public void setDescription_ev(String description_ev) {
        this.description_ev = description_ev;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCode_participant() {
        return code_participant;
    }

    public void setCode_participant(int code_participant) {
        this.code_participant = code_participant;
    }
    
    

    @Override
    public String toString() {
        return "evenement{" + "id_ev=" + id_ev+ ", code_participant=" + code_participant  + ", nom_ev=" + nom_ev + ", type_ev=" + type_ev + ", image_ev=" + image_ev + ", description_ev=" + description_ev + ", date=" + date + '}';
    }
    
    
    
    
    
    
}
