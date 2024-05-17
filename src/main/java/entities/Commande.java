package entities;

import java.util.Date;

public class Commande {
    int idCommande;
    Panier panier;
    Date dateCommande;

    public Commande()
    {

    }
    public Commande(int idCommande,Panier panier,Date dateCommande)
    {
        this.idCommande=idCommande;
        this.panier=panier;
        this.dateCommande=dateCommande;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public Panier getPanier() {
        return panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }
}
