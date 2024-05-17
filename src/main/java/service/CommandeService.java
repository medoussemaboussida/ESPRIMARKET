package service;

import entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DataSource;

import java.sql.*;

public class CommandeService implements IServiceCommande<Commande>{
    private Connection conn;
    private Statement statement;
    private PreparedStatement pst;
    public CommandeService()
    {
        conn= DataSource.getInstance().getCnx();
    }

    @Override
    public void ajouterCommande(Panier panier) {
        String insertCommandeQuery = "INSERT INTO commande (idPanier, dateCommande) VALUES (?, NOW())";
        String updateProduitcartQuery = "UPDATE produitcart pc " +
                "JOIN produit p ON pc.idProduit = p.idProduit " +
                "SET p.quantite = p.quantite - " +
                "(SELECT COUNT(idProduit) FROM produitcart WHERE idPanier = ?) " +
                "WHERE pc.idPanier = ?";

        try (PreparedStatement insertCommandeStatement = conn.prepareStatement(insertCommandeQuery);
             PreparedStatement updateProduitcartStatement = conn.prepareStatement(updateProduitcartQuery)) {

            // Insérer la commande dans la table de commandes
            insertCommandeStatement.setInt(1, panier.getIdPanier());
            insertCommandeStatement.executeUpdate();
            System.out.println("Commande ajoutée!");

            // Mettre à jour la quantité dans la table produitcart
            updateProduitcartStatement.setInt(1, panier.getIdPanier());
            updateProduitcartStatement.setInt(2, panier.getIdPanier());
            updateProduitcartStatement.executeUpdate();
            System.out.println("Quantité mise à jour!");

        } catch (SQLException e) {
            System.out.println("Problème lors de l'ajout de la commande ou mise à jour de la quantité !");
            throw new RuntimeException(e);
        }
    }


    @Override
    public ObservableList<Commande> readAllCommande()
    {
        String requete = "SELECT * FROM commande c JOIN panier pn ON c.idPanier = pn.idPanier JOIN utilisateur u ON pn.idUser=u.idUser";
        ObservableList<Commande> list= FXCollections.observableArrayList();
        try {
            statement = conn.createStatement();
            ResultSet rs=statement.executeQuery(requete);
            while (rs.next()) {
                Utilisateur u=new Utilisateur(rs.getInt("u.idUser"),rs.getString("u.nomUser"),rs.getString("u.prenomUser"), rs.getString("u.emailUser"),rs.getString("u.mdp"),rs.getInt("u.nbPoints"),rs.getInt("u.numTel"),rs.getString("u.Role"));
                Panier pn=new Panier(rs.getInt("pn.idPanier"),u);
                Commande c =new Commande(rs.getInt(1),pn,rs.getDate(3));
                list.add(c);
            }
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return list;
    }

}
