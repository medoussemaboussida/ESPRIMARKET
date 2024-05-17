package entities;

public class PanierProduit {

    int idPanierProduit;
    Produit produit;
    Panier panier;

    public PanierProduit()
    {

    }
    public PanierProduit(int idPanierProduit,Panier panier,Produit produit)
    {
        this.idPanierProduit=idPanierProduit;
        this.produit=produit;
        this.panier=panier;
    }

    public Produit getProduit() {
        return produit;
    }

    public Panier getPanier() {
        return panier;
    }

    public int getIdPanierProduit() {
        return idPanierProduit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    public void setIdPanierProduit(int idPanierProduit) {
        this.idPanierProduit = idPanierProduit;
    }
}
