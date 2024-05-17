package service;

import entities.Publication;
import utils.DataSource;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class PublicationService implements IServicePublication<Publication> {

    private Connection conn;
    private PreparedStatement pste;

    private Statement ste;

    public PublicationService() {
        conn = DataSource.getInstance().getCnx();
    }

    @Override
    public void addPublication(Publication p) {
        String requete = "insert into publication (titrePublication, datePublication, imagePublication, description) values (?, ?, ?, ?)";
        try {
            pste = conn.prepareStatement(requete);
            pste.setString(1, p.getDescription());

            // Formater la date au format 'YYYY-MM-DD HH:MM:SS'
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = dateFormat.format(p.getDatePublication());

            pste.setString(2, formattedDate);
            pste.setString(3, p.getTitrePublication());  // Assurez-vous que le titre est associé au bon paramètre
            pste.setString(4, p.getImagePublication());  // Assurez-vous que l'image est associée au bon paramètre

            pste.executeUpdate();
            System.out.println("Publication ajoutée!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // Fermer la déclaration et la connexion
            try {
                if (pste != null) pste.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public void deletePublication(int idPublication) {
        String requete = "DELETE FROM publication WHERE idPublication=?";
        try {
            PreparedStatement ps = conn.prepareStatement(requete);
            // Paramètre pour la requête préparée
            ps.setInt(1, idPublication);

            // Exécution de la requête
            ps.executeUpdate();

            System.out.println("Publication supprimée !");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void updatePublication(Publication publication) {
        String requete = "UPDATE publication SET description=?, datePublication=?, imagePublication=?, titrePublication=? WHERE idPublication=?";
        try {
            PreparedStatement ps = conn.prepareStatement(requete);
            // Paramètres pour la requête préparée
            ps.setString(1, publication.getDescription());
            ps.setDate(2, new Date(publication.getDatePublication().getTime())); // Convertir Date en java.sql.Date
            ps.setString(3, publication.getImagePublication());
            ps.setString(4, publication.getTitrePublication());
            ps.setInt(5, publication.getIdPublication());

            // Exécution de la requête
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Publication> readAll() {
        String requete = "select * from publication";
        List<Publication> list = new ArrayList<>();
        try {
            ste = conn.createStatement();
            ResultSet rs = ste.executeQuery(requete);
            while (rs.next()) {
                // Créer un objet Date à partir de la date récupérée de la base de données
                Date datePublication = rs.getDate("datePublication");
                // Créer un objet Publication avec toutes les informations récupérées de la base de données
                Publication publication = new Publication(
                        rs.getInt("idPublication"),
                        rs.getString("description"),
                        datePublication, // Utiliser l'objet Date créé ci-dessus
                        rs.getString("imagePublication"),
                        rs.getString("titrePublication")
                );
                list.add(publication);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }


    @Override
    public Publication readByTitre(String titrePublication) {
        String requete = "SELECT * FROM publication WHERE titrePublication=?";
        try {
            PreparedStatement ps = conn.prepareStatement(requete);
            // Paramètre pour la requête préparée
            ps.setString(1, titrePublication);

            // Exécution de la requête
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Création d'un objet Date à partir de la date récupérée de la base de données
                Date datePublication = rs.getDate("datePublication");

                // Création d'un objet Publication avec toutes les informations récupérées de la base de données
                Publication publication = new Publication(
                        rs.getInt("idPublication"),
                        rs.getString("description"),
                        datePublication, // Utiliser l'objet Date créé ci-dessus
                        rs.getString("imagePublication"),
                        titrePublication // Utiliser le titre passé en paramètre
                );
                return publication;
            } else {
                // Aucune publication trouvée avec le titre spécifié
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Publication readById(int idPublication) {
        String requete = "SELECT * FROM publication WHERE idPublication=?";
        try {
            PreparedStatement ps = conn.prepareStatement(requete);
            // Paramètre pour la requête préparée
            ps.setInt(1, idPublication);

            // Exécution de la requête
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Création d'un objet Date à partir de la date récupérée de la base de données
                Date datePublication = rs.getDate("datePublication");

                // Création d'un objet Publication avec toutes les informations récupérées de la base de données
                Publication publication = new Publication(
                        idPublication, // Utiliser l'identifiant passé en paramètre
                        rs.getString("description"),
                        datePublication, // Utiliser l'objet Date créé ci-dessus
                        rs.getString("imagePublication"),
                        rs.getString("titrePublication")
                );
                return publication;
            } else {
                // Aucune publication trouvée avec l'identifiant spécifié
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Publication> sortProduitPrixAsc() {
        String requete = "SELECT * FROM publication ORDER BY datePublication ASC"; // Modifier la requête SQL pour trier par prix
        List<Publication> list = new ArrayList<>();
        try {
            ste = conn.createStatement();
            ResultSet rs = ste.executeQuery(requete);
            while (rs.next()) {
                // Créer un objet Date à partir de la date récupérée de la base de données
                Date datePublication = rs.getDate("datePublication");
                // Créer un objet Publication avec toutes les informations récupérées de la base de données
                Publication publication = new Publication(
                        rs.getInt("idPublication"),
                        rs.getString("description"),
                        datePublication, // Utiliser l'objet Date créé ci-dessus
                        rs.getString("imagePublication"),
                        rs.getString("titrePublication")
                );
                list.add(publication);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<Publication> sortProduitPrixDesc() {
        String requete = "SELECT * FROM publication ORDER BY datePublication DESC"; // Modifier la requête SQL pour trier par prix
        List<Publication> list = new ArrayList<>();
        try {
            ste = conn.createStatement();
            ResultSet rs = ste.executeQuery(requete);
            while (rs.next()) {
                // Créer un objet Date à partir de la date récupérée de la base de données
                Date datePublication = rs.getDate("datePublication");
                // Créer un objet Publication avec toutes les informations récupérées de la base de données
                Publication publication = new Publication(
                        rs.getInt("idPublication"),
                        rs.getString("description"),
                        datePublication, // Utiliser l'objet Date créé ci-dessus
                        rs.getString("imagePublication"),
                        rs.getString("titrePublication")
                );
                list.add(publication);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }







}



