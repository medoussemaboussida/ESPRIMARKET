package entities;

import java.util.Date;

public class Publication {
    private int idPublication;
    private String description;
    private Date datePublication;
    private String imagePublication;
    private String titrePublication;

    public Publication() {
    }

    public Publication(int idPublication, String description, Date datePublication, String imagePublication, String titrePublication) {
        this.idPublication = idPublication;
        this.description = description;
        this.datePublication = datePublication;
        this.imagePublication = imagePublication;
        this.titrePublication = titrePublication;
    }

    public Publication(String description, Date datePublication, String imagePublication, String titrePublication) {
        this.description = description;
        this.datePublication = datePublication;
        this.imagePublication = imagePublication;
        this.titrePublication = titrePublication;
    }

    public int getIdPublication() {
        return idPublication;
    }

    public void setIdPublication(int idPublication) {
        this.idPublication = idPublication;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Date datePublication) {
        this.datePublication = datePublication;
    }

    public String getImagePublication() {
        return imagePublication;
    }

    public void setImagePublication(String imagePublication) {
        this.imagePublication = imagePublication;
    }

    public String getTitrePublication() {
        return titrePublication;
    }

    public void setTitrePublication(String titrePublication) {
        this.titrePublication = titrePublication;
    }

    @Override
    public String toString() {
        return "Publication{" +
                "idPublication=" + idPublication +
                ", description='" + description + '\'' +
                ", datePublication=" + datePublication +
                ", imagePublication='" + imagePublication + '\'' +
                ", titrePublication='" + titrePublication + '\'' +
                '}';
    }
}
