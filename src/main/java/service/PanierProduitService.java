package service;

import entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanierProduitService  implements IServicePanierProduit<PanierProduit>{
    private Connection conn;
    private Statement statement;
    private PreparedStatement pst;
    public PanierProduitService()
    {
        conn= DataSource.getInstance().getCnx();
    }
    @Override
    public void ajouterProduitAuPanier(Panier panier, int idProduit) {
        String requete = "INSERT INTO produitcart (idPanier,idProduit) VALUES (?, ?)";
        try {
            PreparedStatement pst = conn.prepareStatement(requete);
            pst.setInt(1, panier.getIdPanier());
            pst.setInt(2, idProduit);

            pst.executeUpdate();
            System.out.println("Produit ajouté au panier!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObservableList<PanierProduit> getProduitsDuPanierUtilisateur(Panier panier) {
        String requete = "SELECT pc.*, pr.*, pn.*, c.* " +
                "FROM produitcart pc " +
                "JOIN produit pr ON pc.idProduit = pr.idProduit " +
                "JOIN panier pn ON pc.idPanier = pn.idPanier " +
                "JOIN categorie c ON pr.categorie_id = c.idCategorie " +
                "WHERE pc.idPanier = ?";


        ObservableList<PanierProduit> list = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(requete);
            preparedStatement.setInt(1, panier.getIdPanier());
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Categorie c = new Categorie(rs.getInt("c.idCategorie"), rs.getString("c.nomCategorie"),rs.getString("c.imageCategorie"));
                Produit prod = new Produit(rs.getInt("pr.idProduit"), rs.getString("pr.nomProduit"), rs.getInt("pr.quantite"), rs.getFloat("pr.prix"), c, rs.getString("pr.imageProduit"));
                PanierProduit pnss = new PanierProduit(rs.getInt("pc.idPanierProduit"), panier, prod);
                list.add(pnss);
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage()); // Ajout d'une impression de message d'erreur
            System.out.println("Erreur lors de la récupération des produits du panier");

        }

        return list;
    }

    @Override
    public void DeleteProduitAuPanier(Panier panier, int idProduit) {
        String requete = "DELETE FROM produitcart where idPanier= ? and idProduit = ?";
        try {
            pst=conn.prepareStatement(requete);
            pst.setInt(1, panier.getIdPanier());
            pst.setInt(2, idProduit);
            pst.executeUpdate();
            System.out.println("produit supprimé de votre panier!");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des produits du panier");

            throw new RuntimeException(e);
        }
    }




    @Override
    public ObservableList<PanierProduit> getAllProduitsPanier() {
        String requete = "SELECT pc.*,pr.*, pn.*, c.*,u.* " +
                "FROM produitcart pc " +
                "JOIN produit pr ON pc.idProduit = pr.idProduit " +
                "JOIN panier pn ON pc.idPanier = pn.idPanier " +
                "JOIN utilisateur u ON pn.idUser = u.idUser " +
                "JOIN categorie c ON pr.categorie_id = c.idCategorie";


        ObservableList<PanierProduit> list = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(requete);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Utilisateur u=new Utilisateur(rs.getInt("u.idUser"),rs.getString("u.nomUser"),rs.getString("u.prenomUser"), rs.getString("u.emailUser"),rs.getString("u.mdp"),rs.getInt("u.nbPoints"),rs.getInt("u.numTel"),rs.getString("u.Role"));
                Panier pn=new Panier(rs.getInt("pn.idPanier"),u);
                Categorie c = new Categorie(rs.getInt("c.idCategorie"), rs.getString("c.nomCategorie"),rs.getString("c.imageCategorie"));
                Produit prod = new Produit(rs.getInt("pr.idProduit"), rs.getString("pr.nomProduit"), rs.getInt("pr.quantite"), rs.getFloat("pr.prix"), c, rs.getString("pr.imageProduit"));
                PanierProduit pnss = new PanierProduit(rs.getInt("pc.idPanierProduit"),pn, prod);
                list.add(pnss);
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage()); // Ajout d'une impression de message d'erreur
            System.out.println("Erreur lors de la récupération des produits du panier");

        }

        return list;
    }

    @Override
    public float facture(Panier panier) {
        String requete = "SELECT SUM(pr.prix) AS total " +
                "FROM produitcart pc " +
                "JOIN produit pr ON pc.idProduit = pr.idProduit " +
                "JOIN panier pn ON pc.idPanier = pn.idPanier " +
                "WHERE pc.idPanier = ?";
        float total = 0; // Initialisation de la variable total

        try {
            PreparedStatement pst = conn.prepareStatement(requete);
            pst.setInt(1, panier.getIdPanier());
            ResultSet rs = pst.executeQuery(); // Exécution de la requête

            // Vérification si le résultat existe
            if (rs.next()) {
                total = rs.getFloat(1); // Récupération de la somme des prix
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }

        return total; // Retourne le total calculé
    }


}
