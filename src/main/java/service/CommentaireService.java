package service;

import entities.commentaire;
import entities.Utilisateur;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService implements IServiceCommentaire<commentaire> {

    private Connection conn;

    private PreparedStatement pste;

    private Statement ste;

    private ResultSet rs;

    public CommentaireService() {
        conn = DataSource.getInstance().getCnx();
    }

    @Override
    public void addCommentaire(commentaire commentaire) {
        String requete = "insert into commentaire (descriptionCommentaire, idUser, idPublication) values (?, ?, ?)";
        try {
            pste = conn.prepareStatement(requete);
            pste.setString(1, commentaire.getDescriptionCommentaire());

            pste.setInt(2, commentaire.getIdUser().getIdUser());
            pste.setInt(3, commentaire.getIdPublication().getIdPublication());


            pste.executeUpdate();
            System.out.println("Commentaire ajoutée!");
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
    public void deleteCommentaire(int idCommentaire) {

        String requete = "DELETE FROM commentaire WHERE idCommentaire=?";
        try {
            PreparedStatement ps = conn.prepareStatement(requete);
            // Paramètre pour la requête préparée
            ps.setInt(1, idCommentaire);

            // Exécution de la requête
            ps.executeUpdate();

            System.out.println("Commentaire supprimée !");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void deleteAllCommentairesForPublication(int idPublication) {
        String requete = "DELETE FROM commentaire WHERE idPublication=?";
        try {
            pste = conn.prepareStatement(requete);
            pste.setInt(1, idPublication);
            pste.executeUpdate();
            System.out.println("Tous les commentaires de la publication avec l'ID " + idPublication + " ont été supprimés !");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (pste != null) pste.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public List<commentaire> readAll() {
        return null;
    }

    @Override
    public List<commentaire> readAll(int idPublication) {
        List<commentaire> commentaires = new ArrayList<>();
        String requete = "SELECT * FROM commentaire WHERE idPublication = ?";
        try {
            pste = conn.prepareStatement(requete);
            pste.setInt(1, idPublication);
            rs = pste.executeQuery();
            while (rs.next()) {
                // Créer un objet Commentaire pour chaque ligne de résultat
                commentaire commentaire = new commentaire();
                commentaire.setIdCommentaire(rs.getInt("idCommentaire"));
                commentaire.setDescriptionCommentaire(rs.getString("descriptionCommentaire"));

                // Récupérer l'ID de l'utilisateur pour ce commentaire
                int userId = rs.getInt("idUser");

                // Charger les informations de l'utilisateur
                String userQuery = "SELECT * FROM utilisateur WHERE idUser = ?";
                PreparedStatement userStatement = conn.prepareStatement(userQuery);
                userStatement.setInt(1, userId);
                ResultSet userResult = userStatement.executeQuery();

                if (userResult.next()) {
                    // Créer un nouvel objet User
                   Utilisateur user = new Utilisateur();
                    user.setIdUser(userResult.getInt("idUser"));
                    user.setNomUser(userResult.getString("nomUser"));
                    user.setPrenomUser(userResult.getString("prenomUser"));

                    // Associer l'utilisateur au commentaire
                    commentaire.setIdUser(user);
                }

                // Ajouter le commentaire à la liste
                commentaires.add(commentaire);

                // Fermer le ResultSet et le PreparedStatement de l'utilisateur
                userResult.close();
                userStatement.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // Fermer la déclaration et le résultat
            try {
                if (rs != null) rs.close();
                if (pste != null) pste.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return commentaires;
    }



    @Override
    public List<commentaire> sortProduitPrixAsc(int idPublication) {
        List<commentaire> commentaires = new ArrayList<>();
        String requete = "SELECT * FROM commentaire WHERE idPublication = ? ORDER BY idCommentaire ASC";

        try {
            pste = conn.prepareStatement(requete);
            pste.setInt(1, idPublication);
            rs = pste.executeQuery();
            while (rs.next()) {
                // Créer un objet Commentaire pour chaque ligne de résultat
                commentaire commentaire = new commentaire();
                commentaire.setIdCommentaire(rs.getInt("idCommentaire"));
                commentaire.setDescriptionCommentaire(rs.getString("descriptionCommentaire"));
                // Vous devrez peut-être récupérer d'autres données selon votre schéma de base de données

                // Ajouter le commentaire à la liste
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // Fermer la déclaration et le résultat
            try {
                if (rs != null) rs.close();
                if (pste != null) pste.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return commentaires;
    }



    @Override
    public List<commentaire> sortProduitPrixDESC(int idPublication) {
        List<commentaire> commentaires = new ArrayList<>();
        String requete = "SELECT * FROM commentaire WHERE idPublication = ? ORDER BY idCommentaire DESC";

        try {
            pste = conn.prepareStatement(requete);
            pste.setInt(1, idPublication);
            rs = pste.executeQuery();
            while (rs.next()) {
                // Créer un objet Commentaire pour chaque ligne de résultat
                commentaire commentaire = new commentaire();
                commentaire.setIdCommentaire(rs.getInt("idCommentaire"));
                commentaire.setDescriptionCommentaire(rs.getString("descriptionCommentaire"));
                // Vous devrez peut-être récupérer d'autres données selon votre schéma de base de données

                // Ajouter le commentaire à la liste
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            System.out.print("erreur");
            throw new RuntimeException(e);
        } finally {
            // Fermer la déclaration et le résultat
            try {
                if (rs != null) rs.close();
                if (pste != null) pste.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return commentaires;
    }


    @Override
    public void updateCommentaire(commentaire commentaire) {
        String requete = "UPDATE commentaire SET descriptionCommentaire=? WHERE idCommentaire=?";
        try {
            pste = conn.prepareStatement(requete);
            pste.setString(1, commentaire.getDescriptionCommentaire());
            pste.setInt(2, commentaire.getIdCommentaire());
            pste.executeUpdate();
            System.out.println("Commentaire mis à jour !");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (pste != null) pste.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int countCommentairesParPublication(int idPublication) {
        String requete = "SELECT COUNT(*) AS total FROM commentaire WHERE idPublication = ?";
        int nombreCommentaires = 0;

        try {
            pste = conn.prepareStatement(requete);
            pste.setInt(1, idPublication);
            rs = pste.executeQuery();

            if (rs.next()) {
                nombreCommentaires = rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des commentaires par publication", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pste != null) pste.close();
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la fermeture des ressources", e);
            }
        }

        return nombreCommentaires;
    }







}







