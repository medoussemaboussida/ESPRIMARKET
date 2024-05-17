package service;

import entities.Offre;
import entities.Produit;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OffreService implements IServiceOffre<Offre> {
    private Connection conn;
    private Statement statement;
    private PreparedStatement pst;
    private ProduitService produitService; // Service pour les opérations liées aux produits

    public OffreService() {
        conn = DataSource.getInstance().getCnx();
        produitService = new ProduitService(); // Initialisation du service des produits
    }
    public void addOffre(Offre o) {
        String insertOffreQuery = "INSERT INTO offre (nomOffre, descriptionOffre, dateDebut, dateFin, imageOffre, reduction) VALUES (?,?,?,?,?,?)";

        try {
            PreparedStatement pstOffre = conn.prepareStatement(insertOffreQuery, Statement.RETURN_GENERATED_KEYS);

            pstOffre.setString(1, o.getNomOffre());
            pstOffre.setString(2, o.getDescriptionOffre());
            if (o.getDateDebut() != null) {
                pstOffre.setDate(3, new Date(o.getDateDebut().getTime()));
            } else {
                pstOffre.setNull(3, Types.DATE);
            }
            if (o.getDateFin() != null) {
                pstOffre.setDate(4, new Date(o.getDateFin().getTime()));
            } else {
                pstOffre.setNull(4, Types.DATE);
            }
            pstOffre.setString(5, o.getImageOffre());
            pstOffre.setInt(6, o.getReduction());

            int affectedRows = pstOffre.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstOffre.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idOffre = generatedKeys.getInt(1);
                    insertOffreProduits(idOffre, o.getProduits());
                } else {
                    throw new SQLException("Failed to retrieve the ID of the offer.");
                }
            } else {
                System.out.println("No rows affected. Offer not inserted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertOffreProduits(int idOffre, List<Produit> produits) {
        String insertOffreProduitQuery = "UPDATE produit SET offre_id = ? WHERE idProduit = ?";
        try {
            if (produits == null) {
                System.out.println("La liste des produits est null. Aucun produit à ajouter.");
                return;
            }
            PreparedStatement pstOffreProduit = conn.prepareStatement(insertOffreProduitQuery);
            for (Produit produit : produits) {
                pstOffreProduit.setInt(1, idOffre);
                pstOffreProduit.setInt(2, produit.getIdProduit());
                pstOffreProduit.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting offer products: " + e.getMessage());
        }
    }

    public List<Offre> readOffre() {
        String query = "SELECT * FROM Offre";
        List<Offre> offres = new ArrayList<>();

        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                Offre offre = new Offre();
                offre.setReduction(rs.getInt("reduction"));
                offre.setNomOffre(rs.getString("nomOffre"));
                offre.setDescriptionOffre(rs.getString("descriptionOffre"));
                offre.setDateDebut(rs.getDate("dateDebut"));
                offre.setDateFin(rs.getDate("dateFin"));
                offre.setImageOffre(rs.getString("imageOffre"));
                offres.add(offre);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des offres : " + e.getMessage());
        }

        return offres;
    }

    public List<Offre> getAllOffres() {
        List<Offre> offres = new ArrayList<>();
        String query = "SELECT * FROM Offre";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Offre offre = new Offre();
                offre.setIdOffre(resultSet.getInt("idOffre"));
                offre.setDescriptionOffre(resultSet.getString("descriptionOffre"));
                offre.setNomOffre(resultSet.getString("nomOffre"));
                offre.setDateDebut(resultSet.getDate("dateDebut"));
                offre.setDateFin(resultSet.getDate("dateFin"));
                offre.setImageOffre(resultSet.getString("imageOffre"));
                offre.setReduction(resultSet.getInt("reduction"));
                offres.add(offre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return offres;
    }
    private void deleteOffreProduits(int idOffre) {
        String deleteOffreProduitsQuery = "UPDATE Produit SET idOffre = NULL WHERE idOffre = ?";
        try {
            pst = conn.prepareStatement(deleteOffreProduitsQuery);
            pst.setInt(1, idOffre);
            pst.executeUpdate();
            System.out.println("Produits associés à l'offre supprimés avec succès !");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression des produits associés à l'offre : " + e.getMessage());
        }
    }
    public void updateOffre(Offre o) {
        try {
            deleteOffreProduits(o.getIdOffre());

            String updateOffreQuery = "UPDATE Offre SET nomOffre = ?, descriptionOffre = ?, dateDebut = ?, dateFin = ?, imageOffre = ?, reduction = ? WHERE idOffre = ?";
            pst = conn.prepareStatement(updateOffreQuery);
            pst.setString(1, o.getNomOffre());
            pst.setString(2, o.getDescriptionOffre());
            if (o.getDateDebut() != null) {
                pst.setDate(3, new Date(o.getDateDebut().getTime()));
            } else {
                pst.setNull(3, Types.DATE);
            }
            if (o.getDateFin() != null) {
                pst.setDate(4, new Date(o.getDateFin().getTime()));
            } else {
                pst.setNull(4, Types.DATE);
            }
            pst.setString(5, o.getImageOffre());
            pst.setInt(6, o.getReduction());
            pst.setInt(7, o.getIdOffre());
            int rowsUpdated = pst.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Offre mise à jour avec succès !");
                insertOffreProduits(o.getIdOffre(), o.getProduits());
            } else {
                System.out.println("Aucune offre trouvée avec cet identifiant. Mise à jour annulée. ID: " + o.getIdOffre());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'offre : " + e.getMessage());
        }
    }
    public void deleteOffre(int id) {
        String dissociateOffreProduitsQuery = "UPDATE produit SET offre_id = NULL WHERE offre_id = ?";
        String deleteOffreQuery = "DELETE FROM offre WHERE idOffre = ?";

        try {
            conn.setAutoCommit(false);

            PreparedStatement pstDissociate = conn.prepareStatement(dissociateOffreProduitsQuery);
            pstDissociate.setInt(1, id);
            int rowsUpdated = pstDissociate.executeUpdate();
            System.out.println("Nombre de produits dissociés : " + rowsUpdated);

            PreparedStatement pstDelete = conn.prepareStatement(deleteOffreQuery);
            pstDelete.setInt(1, id);
            int rowsDeleted = pstDelete.executeUpdate();

            if (rowsDeleted > 0) {
                conn.commit();
                System.out.println("Offre supprimée avec succès. ID: " + id);
            } else {
                conn.rollback();
                System.out.println("Aucune offre trouvée avec cet identifiant. Suppression annulée. ID: " + id);
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException autoCommitException) {
                autoCommitException.printStackTrace();
            }
        }
    }


}
