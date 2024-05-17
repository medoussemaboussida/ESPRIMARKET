package service;

import entities.Utilisateur;
import javafx.collections.ObservableList;

import java.util.List;

public interface IServiceUtilisateur <T>{
    T getUserById(int idUser);
    void add(T t);
    void delete(T t);
    void update(T t);
    List<T> readAll ();
    T readbyId(int idUser);
    public ObservableList<T> userBack();
    public List<T> getAllUsersPub();

    ObservableList<T> sortUserNomDesc();
    ObservableList<T> sortUserNomAsc();
    ObservableList<T> sortUserPrenomDesc();
    ObservableList<T> sortUserPrenomAsc();


}
