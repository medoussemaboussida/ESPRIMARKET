package entities;

public class Categorie {
    private int idCategorie;
    private String nomCategorie;
    private String imageCategorie;

    public  Categorie(int idCategorie, String nomCategorie, String imageCategorie) {
        this.idCategorie = idCategorie;
        this.nomCategorie = nomCategorie;
        this.imageCategorie = imageCategorie;
    }

    public Categorie(String nomCategorie, String imageCategorie) {
        this.nomCategorie = nomCategorie;
        this.imageCategorie = imageCategorie;
    }

    public Categorie(int id, String name) {
        this.idCategorie = idCategorie;
        this.nomCategorie = nomCategorie;
    }

    public Categorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public  Categorie() {
    }

    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int id) {
        this.idCategorie = id;
    }

    public String getNomCategorie() {
        return nomCategorie;
    }

    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    public String getImageCategorie() {
        return imageCategorie;
    }

    public void setImageCategorie(String imageCategorie) {
        this.imageCategorie = imageCategorie;
    }

    @Override
    public String toString() {

        return "Categorie{" + "idCategorie=" +idCategorie + ", NomCategorie=" + nomCategorie + ", image Categorie=" + imageCategorie + '}';
    }

}
