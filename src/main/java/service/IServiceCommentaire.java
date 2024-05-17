package service;

import entities.commentaire;

import java.util.List;

public interface IServiceCommentaire <T>{
    void addCommentaire(T t);

    void deleteCommentaire(int idCommentaire);

    void deleteAllCommentairesForPublication(int idPublication);

    List<T> readAll();


    List<commentaire> readAll(int idPublication);

    List<commentaire> sortProduitPrixAsc(int idPublication);


    List<commentaire> sortProduitPrixDESC(int idPublication);

    void updateCommentaire(commentaire commentaire);

    int countCommentairesParPublication(int idPublication);
}
