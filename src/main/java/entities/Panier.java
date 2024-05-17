package entities;

import service.ProduitService;

public class Panier {
    int idPanier;
    Utilisateur utilisateur;

    public Panier()
    {

    }
    public Panier(int idPanier,Utilisateur utilisateur)
    {
        this.idPanier=idPanier;
        this.utilisateur=utilisateur;
    }

    public int getIdPanier() {
        return idPanier;
    }


    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setIdPanier(int idPanier) {
        this.idPanier = idPanier;
    }
}
