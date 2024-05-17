package service;

import entities.Publication;

import java.util.List;

public interface IServicePublication <T>{

    void addPublication(T t);



    void deletePublication(int idPublication);

    void updatePublication(T t);

    List<T> readAll();

    T readByTitre(String titrePublication);

    Publication readById(int idPublication);

    List<T> sortProduitPrixAsc();

    List<T> sortProduitPrixDesc();





}
