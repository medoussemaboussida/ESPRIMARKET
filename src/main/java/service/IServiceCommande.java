package service;

import entities.Commande;
import entities.Panier;
import entities.PanierProduit;
import javafx.collections.ObservableList;

public interface IServiceCommande <T>{
    void ajouterCommande(Panier panier);
    ObservableList<Commande> readAllCommande();
}
