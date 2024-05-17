package service;

import entities.Produit;
import javafx.collections.ObservableList;

import java.util.List;

public interface IServiceProduit <T>{
    void addProduit (T t);
    ObservableList<T> readProduit();
    void modifyProduit(T t);
    void deleteProduit(int  idProduit);
    ObservableList<T> sortProduitPrixAsc();
    ObservableList<T> sortProduitPrixDesc();
    ObservableList<T> readProduitByCategorie(int categorieId);
    ObservableList<Produit> getAllProduits();
}
