package service;

import javafx.collections.ObservableList;

import java.util.List;

public interface IServiceCategorie <T> {
    void addCategorie (T t);
    ObservableList<T> readCategorie();
    void deleteCategorie(int idCategorie);
    void modifyCategorie (T t);
    ObservableList<T> sortCategorieAsc();
    ObservableList<T> sortCategorieDesc();


}
