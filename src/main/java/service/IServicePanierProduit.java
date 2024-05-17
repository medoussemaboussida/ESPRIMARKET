package service;

import entities.Panier;
import entities.PanierProduit;
import entities.Produit;
import javafx.collections.ObservableList;

import java.util.List;

public interface IServicePanierProduit <T>{
    void ajouterProduitAuPanier(Panier panier, int idProduit);
    ObservableList<T> getProduitsDuPanierUtilisateur(Panier panier);
    void DeleteProduitAuPanier(Panier panier, int idProduit);

    public ObservableList<PanierProduit> getAllProduitsPanier();
    public float facture(Panier panier);

    }
