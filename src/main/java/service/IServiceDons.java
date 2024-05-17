package service;

import entities.Dons;
import entities.Utilisateur;

import java.util.List;

public interface IServiceDons {
    int addDons(Utilisateur user, int donPoints);
    void updateDonsPoints(int donsId, int newPoints);


    boolean supprimerDon(int donsId, int nbPoints);

    boolean donExists(int idDon);

    List<Dons> getAllDons();


    void addDons(Utilisateur user, int donPoints, String etatStatutDons);

    int addDonsWithStatus(Utilisateur user, int donPoints);


    void updateDons(Dons don);
}
