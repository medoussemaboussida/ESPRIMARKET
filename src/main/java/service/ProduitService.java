package service;

import entities.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DataSource;
import entities.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements IServiceProduit<Produit> {
    private Connection conn;
    private Statement statement;
    private PreparedStatement pst;
    public ProduitService()
    {
        conn= DataSource.getInstance().getCnx();
    }

    @Override
    public void addProduit(Produit p)
    {
        String requete = "insert into Produit (categorie_id,nomProduit,quantite,prix,imageProduit,offre_id) values (?,?,?,?,?,?)";
        try {
            pst = conn.prepareStatement(requete);
            pst.setInt(1,p.getCategorie().getIdCategorie());
            pst.setString(2,p.getNomProduit());
            pst.setInt(3,p.getQuantite());
            pst.setFloat(4,p.getPrix());
            pst.setString(5,p.getImageProduit());
            pst.executeUpdate();
            System.out.println("Produit ajoutée!");
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
@Override
public ObservableList<Produit> readProduit()
{
    String requete = "SELECT * FROM produit p JOIN categorie c ON p.categorie_id = c.idCategorie";
    ObservableList<Produit> list= FXCollections.observableArrayList();
    try {
        statement = conn.createStatement();
        ResultSet rs=statement.executeQuery(requete);
        while (rs.next()) {
            Categorie c = new Categorie(rs.getInt("c.idCategorie"), rs.getString("c.nomCategorie"),rs.getString("c.imageCategorie"));
            Produit prod = new Produit(rs.getInt(1),rs.getString(3),rs.getInt(4),rs.getFloat(5),c,rs.getString(6));
            list.add(prod);
        }
    } catch (SQLException e)
    {
        throw new RuntimeException(e);
    }
    return list;
}

   @Override
    public void modifyProduit(Produit p)
    {
        String requete = "UPDATE produit set categorie_id = ?,nomProduit = ?,quantite = ?,prix = ? ,imageProduit = ?  where  idProduit= ?";
        try {
            pst = conn.prepareStatement(requete);
            pst.setInt(1,p.getCategorie().getIdCategorie());
            pst.setString(2,p.getNomProduit());
            pst.setInt(3,p.getQuantite());
            pst.setFloat(4,p.getPrix());
            pst.setString(5,p.getImageProduit());
            pst.setInt(6,p.getIdProduit());

            pst.executeUpdate();
            System.out.println("Produit Modifiée!");
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
   public void deleteProduit(int idProduit)
    {

        String requete = "delete from produit where idProduit = ?";
        try {
            pst=conn.prepareStatement(requete);
            pst.setInt(1, idProduit);
            pst.executeUpdate();
            System.out.println("produit supprimé!");
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

    }


    @Override
    public ObservableList<Produit> sortProduitPrixAsc()
    {
        ObservableList<Produit> list = FXCollections.observableArrayList();
        try {
            String req = "SELECT * FROM produit p JOIN categorie c ON p.categorie_id = c.idCategorie  ORDER BY p.prix ASC";
            statement = conn.createStatement();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Categorie c = new Categorie(rs.getInt("c.idCategorie"), rs.getString("c.nomCategorie"),rs.getString("c.imageCategorie"));
                Produit prod = new Produit(rs.getInt(1),rs.getString(3),rs.getInt(4),rs.getFloat(5),c,rs.getString(6));
                list.add(prod);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return list;
    }

    @Override
    public ObservableList<Produit> sortProduitPrixDesc()
    {
        ObservableList<Produit> list = FXCollections.observableArrayList();
        try {
            String req = "SELECT * FROM produit p JOIN categorie c ON p.categorie_id = c.idCategorie  ORDER BY p.prix desc";
            statement = conn.createStatement();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Categorie c = new Categorie(rs.getInt("c.idCategorie"), rs.getString("c.nomCategorie"),rs.getString("c.imageCategorie"));
                Produit prod = new Produit(rs.getInt(1),rs.getString(3),rs.getInt(4),rs.getFloat(5),c,rs.getString(6));
                list.add(prod);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return list;
    }


    public ObservableList<Produit> readProduitByCategorie(int categorieId) {
        String requete = "SELECT * FROM produit p JOIN categorie c ON p.categorie_id = c.idCategorie WHERE c.idCategorie = ?";
        ObservableList<Produit> list = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(requete);
            preparedStatement.setInt(1, categorieId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Categorie c = new Categorie(rs.getInt("c.idCategorie"), rs.getString("c.nomCategorie"), rs.getString("c.imageCategorie"));
                Produit prod = new Produit(rs.getInt(1), rs.getString(3), rs.getInt(4), rs.getFloat(5), c, rs.getString(6));
                list.add(prod);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }


    public ObservableList<Produit> readProduitByCategorieAndReduction(int categorieId, float reductionMin) {
        String requete = "SELECT * FROM produit p JOIN categorie c ON p.categorie_id = c.idCategorie WHERE c.idCategorie = ? AND ps.getReduction(p.idProduit()) >= ?";
        ObservableList<Produit> list = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(requete);
            preparedStatement.setInt(1, categorieId);
            preparedStatement.setFloat(2, reductionMin);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Categorie c = new Categorie(rs.getInt("c.idCategorie"), rs.getString("c.nomCategorie"), rs.getString("c.imageCategorie"));
                Produit prod = new Produit(rs.getInt(1), rs.getString(3), rs.getInt(4), rs.getFloat(5), c, rs.getString(6));
                list.add(prod);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    public float getReduction(int idProduit) {
        String requete = "SELECT reduction FROM Offre WHERE idOffre = (SELECT offre_id FROM Produit WHERE idProduit = ?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(requete);
            preparedStatement.setInt(1, idProduit);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getFloat("reduction");
            } else {
                // Retourner 0 si aucune réduction n'est trouvée pour le produit donné
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void associerProduitAOffre(int idProduit, int idOffre) {
        String query = "UPDATE produit SET idOffre = ? WHERE idProduit = ?";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, idOffre);
            pst.setInt(2, idProduit);
            pst.executeUpdate();
            System.out.println("Product associated with offer successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Error associating product with offer: " + e.getMessage());
        }
    }
    @Override
    public ObservableList<Produit> getAllProduits() {
        ObservableList<Produit> produits = FXCollections.observableArrayList();
        String query = "SELECT * FROM produit p JOIN categorie c ON p.categorie_id = c.idCategorie ";

        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                Categorie categorie = getCategorieById(rs.getInt("c.idCategorie")); // Récupérer la catégorie associée au produit
                Produit produit = new Produit(
                        rs.getInt("idProduit"),
                        rs.getString("nomProduit"),
                        rs.getInt("quantite"),
                        rs.getFloat("prix"),
                        categorie,
                        rs.getString("imageProduit")
                );
                produits.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produits;
    }
    public Categorie getCategorieById(int idCategorie) {
        String query = "SELECT * FROM categorie WHERE idCategorie = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, idCategorie);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Categorie(
                            rs.getInt("idCategorie"),
                            rs.getString("nomCategorie"),
                            rs.getString("imageCategorie")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la catégorie par ID : " + e.getMessage());
        }
        return null; // Retourne null si aucune catégorie trouvée pour l'ID donné
    }
}


