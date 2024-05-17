package service;

import entities.Panier;

public interface IServicePanier <T>{
    void ajouterPanier(int idProduit);
    T selectPanierParUserId(int idUser);

    }
