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
public class participant extends evenement {
    private int id_participant;        
private Date date_part;    
private int id_user;
public int id_evenement; 
public evenement evenement;

    public participant() {
    }

    public participant(int id_participant, Date date_participant, int id_user, int id_evenement) {
        this.id_participant = id_participant;
        this.date_part = date_participant;
        this.id_user = id_user;
        this.id_evenement = id_evenement;
    }
    public participant(Date date_participant, int id_user, int id_evenement) {
        this.date_part = date_participant;
        this.id_user = id_user;
        this.id_evenement = id_evenement;
    }

    public participant(int id_participant, Date date_participant, int id_user, int id_evenement, evenement evenement) {
        this.id_participant = id_participant;
        this.date_part = date_participant;
        this.id_user = id_user;
        this.id_evenement = id_evenement;
        this.evenement = evenement;
    }

    public int getId_participant() {
        return id_participant;
    }

    public Date getDate_part() {
        return date_part;
    }

    public int getId_user() {
        return id_user;
    }

    public int getId_evenement() {
        return id_evenement;
    }

    public evenement getevenement() {
        return evenement;
    }

    public void setId_participant(int id_participant) {
        this.id_participant = id_participant;
    }

    public void setDate_part(Date date_participant) {
        this.date_part = date_participant;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public void setId_evenement(int id_evenement) {
        this.id_evenement = id_evenement;
    }

    public void setEvenement(evenement evenement) {
        this.evenement = evenement;
    }

    @Override
    public String toString() {
        return "participant{" + "id_participant=" + id_participant + ", date_participant=" + date_part + ", id_user=" + id_user + ", id_evenement=" + id_evenement +  '}';
    }
    
    
    




}
