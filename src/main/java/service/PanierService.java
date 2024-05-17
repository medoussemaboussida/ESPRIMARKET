package service;

import entities.Panier;
import utils.DataSource;

import java.sql.*;

public class PanierService implements IServicePanier<Panier>{

    private Connection conn;
    private Statement statement;
    private PreparedStatement pst;
    public PanierService()
    {
        conn= DataSource.getInstance().getCnx();
    }

    @Override
    public void ajouterPanier(int idUser)
    {
        String requete = "insert into panier (idUser) values (?)";
        try {
            pst = conn.prepareStatement(requete);
            pst.setInt(1, idUser);
            pst.executeUpdate();
            System.out.println("Panier ajoutée!");
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Panier selectPanierParUserId(int idUser) {
        Panier panier = null;
        String requete = "SELECT * FROM panier WHERE idUser = ?";
        try {
            pst = conn.prepareStatement(requete);
            pst.setInt(1, idUser);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                panier = new Panier();
                panier.setIdPanier(rs.getInt("idPanier"));
                // Ajoutez d'autres attributs du panier en fonction de votre modèle de données
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return panier;
    }

}
