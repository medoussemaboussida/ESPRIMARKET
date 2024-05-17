package entities;

import javafx.beans.property.*;

import java.util.Date;

public class Dons {
    private IntegerProperty idDons;
    private IntegerProperty idUser;
    private IntegerProperty nbPoints;
    private ObjectProperty<Date> date_ajout;
    private StringProperty etatStatutDons;

    // Nouvelles propriétés pour les informations de l'utilisateur
    private StringProperty nomUser;
    private StringProperty prenomUser;
    private StringProperty emailUser;
    private StringProperty numTel;

    public Dons() {
        this.idDons = new SimpleIntegerProperty();
        this.idUser = new SimpleIntegerProperty();
        this.nbPoints = new SimpleIntegerProperty();
        this.date_ajout = new SimpleObjectProperty<>();
        this.etatStatutDons = new SimpleStringProperty();
        this.nomUser = new SimpleStringProperty();
        this.prenomUser = new SimpleStringProperty();
        this.emailUser = new SimpleStringProperty();
        this.numTel = new SimpleStringProperty();
    }

    public Dons(int idDons, int idUser, int nbPoints, Date date_ajout, String etatStatutDons, String nomUser, String prenomUser, String emailUser, String numTel) {
        this.idDons = new SimpleIntegerProperty(idDons);
        this.idUser = new SimpleIntegerProperty(idUser);
        this.nbPoints = new SimpleIntegerProperty(nbPoints);
        this.date_ajout = new SimpleObjectProperty<>(date_ajout);
        this.etatStatutDons = new SimpleStringProperty(etatStatutDons);
        this.nomUser = new SimpleStringProperty(nomUser);
        this.prenomUser = new SimpleStringProperty(prenomUser);
        this.emailUser = new SimpleStringProperty(emailUser);
        this.numTel = new SimpleStringProperty(numTel);
    }

    @Override
    public String toString() {
        return "Dons [nomUser=" + nomUser + ", prenomUser=" + prenomUser + ", nbPoints=" + nbPoints + ", date_ajout=" + date_ajout + ", etatStatutDons=" + etatStatutDons + "]";
    }

    
   

    public Dons(String nomUser, String prenomUser, String emailUser, String numTel, int nbpoints, Date dateAjout, String etatStatutDons) {
    }

    public Dons(int idDons, String nomUser, String prenomUser, String emailUser, int nbPoints, Date dateAjout, String etatStatutDons) {
    }


    public Dons(int idDons, int userId, int nbPoints, Date dateAjout, String etatStatutDons) {
    }

    public Dons(int idDons, int idUser, String nomUser, String prenomUser, String emailUser, int nbPoints, Date dateAjout, String etatStatutDons) {
    }


    public IntegerProperty idDonsProperty() {
        return idDons;
    }

    public int getIdDons() {

        return idDons.get();
    }

    public void setIdDons(int idDons) {
        this.idDons.set(idDons);
    }

    public IntegerProperty idUserProperty() {
        return idUser;
    }

    public int getIdUser() {
        return idUser.get();
    }

    public void setIdUser(int idUser) {
        this.idUser.set(idUser);
    }

    public IntegerProperty nbPointsProperty() {
        return nbPoints;
    }

    public int getNbPoints() {
        return nbPoints.get();
    }

    public void setNbPoints(int nbPoints) {
        this.nbPoints.set(nbPoints);
    }

    public ObjectProperty<Date> date_ajoutProperty() {
        return date_ajout;
    }

    public Date getDate_ajout() {
        return date_ajout.get();
    }

    public void setDate_ajout(Date date_ajout) {
        this.date_ajout.set(date_ajout);
    }

    public StringProperty etatStatutDonsProperty() {
        return etatStatutDons;
    }

    public String getEtatStatutDons() {
        return etatStatutDons.get();
    }

    public void setEtatStatutDons(String etatStatutDons) {
        this.etatStatutDons.set(etatStatutDons);
    }

    // Getters et setters pour les informations de l'utilisateur
    public String getNomUser() {
        return nomUser.get();
    }

    public StringProperty nomUserProperty() {
        return nomUser;
    }

    public void setNomUser(String nomUser) {
        this.nomUser.set(nomUser);
    }

    public String getPrenomUser() {
        return prenomUser.get();
    }

    public StringProperty prenomUserProperty() {
        return prenomUser;
    }

    public void setPrenomUser(String prenomUser) {
        this.prenomUser.set(prenomUser);
    }

    public String getEmailUser() {
        return emailUser.get();
    }

    public StringProperty emailUserProperty() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser.set(emailUser);
    }

    public String getNumTel() {
        return numTel.get();
    }

    public StringProperty numTelProperty() {
        return numTel;
    }

    public void setNumTel(String numTel) {
        this.numTel.set(numTel);
    }
}
