package entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Offre {
    private int idOffre;
    private String descriptionOffre;
    private String nomOffre;
    private Date dateDebut;
    private Date dateFin;
    private String imageOffre;
    private int reduction;
    List<Produit> produits;

    public Offre(int idOffre, String descriptionOffre, String nomOffre, Date dateDebut, Date dateFin, String imageOffre, int reduction) {
        this.idOffre = idOffre;
        this.descriptionOffre = descriptionOffre;
        this.nomOffre = nomOffre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.imageOffre = imageOffre;
        this.reduction = reduction;
        this.produits = new ArrayList<>();
    }

    public Offre(int idOffre, String descriptionOffre, String nomOffre, Date dateDebut, Date dateFin) {
        this.idOffre = idOffre;
        this.descriptionOffre = descriptionOffre;
        this.nomOffre = nomOffre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }


    public Offre(String nomOffre, String descriptionOffre,Date dateDebut, Date dateFin) {
        this.descriptionOffre = descriptionOffre;
        this.nomOffre = nomOffre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }


    public Offre() {

    }

    public Offre( String descriptionOffre, String nomOffre, Date dateDebut, Date dateFin, int reduction) {
        this.descriptionOffre = descriptionOffre;
        this.nomOffre = nomOffre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.reduction = reduction;
    }


    public int getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(int idOffre) {
        this.idOffre = idOffre;
    }

    public String getDescriptionOffre() {
        return descriptionOffre;
    }

    public void setDescriptionOffre(String descriptionOffre) {
        this.descriptionOffre = descriptionOffre;
    }

    public String getNomOffre() {
        return nomOffre;
    }

    public void setNomOffre(String nomOffre) {
        this.nomOffre = nomOffre;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getImageOffre() {
        return imageOffre;
    }

    public void setImageOffre(String imageOffre) {
        this.imageOffre = imageOffre;
    }
    public int getReduction() {
        return reduction;
    }

    public void setReduction(int reduction) {
        this.reduction = reduction;
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }
    public void addProduit(Produit produit) {
        produits.add(produit);
        produit.setOffre(this); // Met à jour la référence à l'offre dans le produit
    }

    @Override
    public String toString() {
        return "Offre{" +
                "idOffre=" + idOffre +
                ", descriptionOffre='" + descriptionOffre + '\'' +
                ", nomOffre='" + nomOffre + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", imageOffre='" + imageOffre + '\'' +
                ", reduction=" + reduction +
                ", produits=" + produits +
                '}';
    }


}