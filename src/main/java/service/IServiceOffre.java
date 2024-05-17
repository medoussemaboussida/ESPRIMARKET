package service;

import entities.Offre;

import java.util.List;

public interface IServiceOffre<O> {
    void addOffre(Offre o);

    List<Offre> readOffre();

    void deleteOffre(int id);

    void updateOffre(Offre o);
}
