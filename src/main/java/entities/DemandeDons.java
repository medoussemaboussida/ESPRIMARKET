package entities;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.sql.Timestamp;

public class DemandeDons {
    private final IntegerProperty idDemandeProperty = new SimpleIntegerProperty();

    private int idDemande;
    private int idDons;
    private int idUtilisateur;
    private String contenu;
    private String image;
    private Timestamp datePublication;
    private String nomUser;
    private String prenomUser;
    private int nbPoints;

    private int idUser;

    public int getTotalPointsGagnes() {
        return totalPointsGagnes;
    }

    public void setTotalPointsGagnes(int totalPointsGagnes) {
        this.totalPointsGagnes = totalPointsGagnes;
    }

    private int totalPointsGagnes;




    public DemandeDons(int idDemande, int idUtilisateur, String contenu, String image, Timestamp datePublication) {
        this.idDemande = idDemande;
        this.idUtilisateur = idUtilisateur;
        this.contenu = contenu;
        this.image = image;
        this.datePublication = datePublication;
    }

    public DemandeDons() {

    }
    public IntegerProperty idDemandeProperty() {
        return idDemandeProperty;
    }
    // Getters and Setters
    public int getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(int idDemande) {
        this.idDemande = idDemande;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Timestamp getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Timestamp datePublication) {
        this.datePublication = datePublication;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNomUser() {
        return nomUser;
    }

    public void setNomUser(String nomUser) {
        this.nomUser = nomUser;
    }

    public String getPrenomUser() {
        return prenomUser;
    }

    public void setPrenomUser(String prenomUser) {
        this.prenomUser = prenomUser;
    }

    public int getNbPoints() {
        return nbPoints;
    }

    public void setNbPoints(int nbPoints) {
        this.nbPoints = nbPoints;
    }



    public int getIdDons() {
        return idDons;
    }
}
