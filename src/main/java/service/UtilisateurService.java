package service;

import entities.Categorie;
import entities.PasswordResetToken;
import entities.Produit;
import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mindrot.jbcrypt.BCrypt;
import utils.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService implements IServiceUtilisateur<Utilisateur>{
    private Connection conn;
    private Statement ste;
    private PreparedStatement pst;
    public UtilisateurService()
    {
        conn= DataSource.getInstance().getCnx();
    }

    public Utilisateur getUserById(int idUser) {
        Utilisateur utilisateur = null;
        ResultSet resultSet = null;

        try {
            // Préparez la requête SQL
            String query = "SELECT * FROM utilisateur WHERE idUser = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, idUser);

            // Exécutez la requête
            resultSet = pst.executeQuery();

            // Vérifiez s'il y a des résultats
            if (resultSet.next()) {
                // Construisez l'objet Utilisateur à partir des résultats
                utilisateur = new Utilisateur(
                        resultSet.getInt("idUser"),
                        resultSet.getString("nomUser"),
                        resultSet.getString("prenomUser"),
                        resultSet.getString("emailUser"),
                        resultSet.getString("mdp"),
                        resultSet.getInt("nbPoints"),
                        resultSet.getInt("numTel"),
                        resultSet.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer les exceptions SQL
        } finally {
            // Fermez les ressources
            closeResources(pst, resultSet);
        }

        return utilisateur;
    }

    public void add(Utilisateur x){
        String requete = "INSERT INTO utilisateur (nomUser, prenomUser, emailUser, mdp, nbPoints, numTel, role) VALUES ('"
                + x.getNomUser() + "','" + x.getPrenomUser() + "','" + x.getEmailUser() + "','" + x.getMotDePasse() + "',"
                + x.getNbPoints() + ",'" + x.getNumeroTel() + "','" + x.getRole() + "')";

        try {
            ste= conn.createStatement();
            ste.executeUpdate(requete);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void ajouterPersonne(Utilisateur x) throws SQLException {
        Connection conn = DataSource.getInstance().getCnx();
        if (conn != null) {

            String query = "INSERT INTO Utilisateur (nomUser, prenomUser, emailUser, mdp, nbPoints, numTel, role) VALUES (?, ?,?,?,?,?,?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, x.getNomUser());
                preparedStatement.setString(2, x.getPrenomUser());
                preparedStatement.setString(3, x.getEmailUser());

                // Hash the password using bcrypt
                String hashedPassword = BCrypt.hashpw(x.getMotDePasse(), BCrypt.gensalt());
                preparedStatement.setString(4, hashedPassword);

                preparedStatement.setInt(5, x.getNbPoints());
                preparedStatement.setInt(6, x.getNumeroTel());
                preparedStatement.setString(7, x.getRole());

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Person added successfully.");
                } else {
                    System.out.println("Failed to add person.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            System.out.println("Failed to establish a database connection.");
        }
    }



    @Override
    public void delete(Utilisateur utilisateur) {
        String requete = "DELETE FROM utilisateur WHERE idUser = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(requete)) {
            preparedStatement.setInt(1, utilisateur.getIdUser());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Utilisateur utilisateur) {
        String requete = "UPDATE utilisateur SET nomUser=?, prenomUser=?, emailUser=?, mdp=?, nbPoints=?, numTel=?, role=? WHERE idUser=?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(requete)) {
            preparedStatement.setString(1, utilisateur.getNomUser());
            preparedStatement.setString(2, utilisateur.getPrenomUser());
            preparedStatement.setString(3, utilisateur.getEmailUser());

            // Hash the password with Bcrypt before updating
            String hashedPassword = BCrypt.hashpw(utilisateur.getMotDePasse(), BCrypt.gensalt());
            preparedStatement.setString(4, hashedPassword);

            preparedStatement.setInt(5, utilisateur.getNbPoints());
            preparedStatement.setInt(6, utilisateur.getNumeroTel());
            preparedStatement.setString(7, utilisateur.getRole());
            preparedStatement.setInt(8, utilisateur.getIdUser());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public List<Utilisateur> readAll() {
        String requete = "SELECT * FROM Utilisateur ";
        List<Utilisateur> list=new ArrayList<>();


        try {
            ste = conn.createStatement();
            ResultSet rs = ste.executeQuery(requete);
            while (rs.next()) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setIdUser(rs.getInt("idUser"));
                utilisateur.setNomUser(rs.getString("nomUser"));
                utilisateur.setPrenomUser(rs.getString("prenomUser"));
                utilisateur.setEmailUser(rs.getString("emailUser"));
                utilisateur.setMotDePasse(rs.getString("mdp"));
                utilisateur.setNbPoints(rs.getInt("nbPoints"));
                utilisateur.setNumeroTel(rs.getInt("numTel"));
                utilisateur.setRole(rs.getString("role"));

                list.add(utilisateur);

                list.add(new Utilisateur());
                //pst.close();
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }


    @Override
    public Utilisateur readbyId(int idUser) {

        String requete = "SELECT * FROM utilisateur WHERE idUser = ?";
        Utilisateur utilisateur = null;

        try (PreparedStatement preparedStatement = conn.prepareStatement(requete)) {
            preparedStatement.setInt(1, idUser);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // L'utilisateur a été trouvé, créez un objet Utilisateur avec les détails
                    utilisateur  = new Utilisateur();
                    utilisateur.setIdUser(resultSet.getInt("idUser"));
                    utilisateur.setNomUser(resultSet.getString("nomUser"));
                    utilisateur.setPrenomUser(resultSet.getString("prenomUser"));
                    utilisateur.setEmailUser(resultSet.getString("emailUser"));
                    utilisateur.setMotDePasse(resultSet.getString("mdp"));
                    utilisateur.setNbPoints(resultSet.getInt("nbPoints"));
                    utilisateur.setNumeroTel(resultSet.getInt("numTel"));
                    utilisateur.setRole(resultSet.getString("role"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return utilisateur;
    }
//    public Utilisateur getUserByEmail(String email) {
//
//
//
//        String requete = "SELECT user.* FROM user WHERE email='" + email + "'";
//
//        Utilisateur t = null;
//        try {
//            Statement st = conn.createStatement();
//            ResultSet rs = st.executeQuery(requete);
//            while (rs.next()) {
//
//
//                t = new Utilisateur(rs.getInt("idUser"), rs.getString("nomUser"), rs.getString("prenomUser"), rs.getString("emailUser"), rs.getString("mdp"), rs.getInt("nbPoints"), rs.getInt("numeroTel"), rs.getString("role"));
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(UtilisateurService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return t;
//
//    }


// Other imports...

    public Utilisateur login(String email, String password) {
        String query = "SELECT * FROM utilisateur WHERE emailUser=? AND mdp=?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Utilisateur(
                            resultSet.getInt("idUser"),
                            resultSet.getString("nomUser"),
                            resultSet.getString("prenomUser"),
                            resultSet.getString("emailUser"),
                            resultSet.getString("mdp"),
                            resultSet.getInt("nbPoints"),
                            resultSet.getInt("numTel"),
                            resultSet.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null; // Return null if login is unsuccessful
    }


    public void updateUserPassword(int userId, String newPassword) {
        PreparedStatement stmt = null;

        try {
            stmt = this.conn.prepareStatement("UPDATE utilisateur SET mdp = ? WHERE idUser = ?");
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException var13) {
            var13.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                }

                if (this.conn != null) {
                }
            } catch (RuntimeException var12) {
                var12.printStackTrace();
            }

        }

    }

    public void insertPasswordResetToken(int userId, String token, long timestamp, String email) {
        try {
            PreparedStatement stmt = this.conn.prepareStatement("INSERT INTO password_reset_token(token,idUser, timestamp,emailUser) VALUES (?, ?, ?,?)");
            stmt.setInt(2, userId);
            stmt.setString(1, token);
            stmt.setTimestamp(3, new Timestamp(timestamp));
            stmt.setString(4, email);
            stmt.executeUpdate();
        } catch (SQLException var7) {
            var7.printStackTrace();
        }

    }

    public PasswordResetToken getPasswordResetToken(String token) {
        try {
            PreparedStatement statement = this.conn.prepareStatement("SELECT * FROM password_reset_token WHERE token = ?");
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            PasswordResetToken passwordResetToken = null;
            if (resultSet.next()) {
                passwordResetToken = new PasswordResetToken();
                passwordResetToken.setToken(resultSet.getString("token"));
                passwordResetToken.setUserId(resultSet.getInt("idUser"));
                System.out.println(resultSet.getInt("idUser"));
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                LocalDateTime localDateTime = timestamp.toLocalDateTime();
                passwordResetToken.setTimestamp(localDateTime);
                System.out.println(resultSet.getString("token"));
                return passwordResetToken;
            } else {
                return passwordResetToken;
            }
        } catch (SQLException var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public Utilisateur getUserByEmail(String email) {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM utilisateur WHERE emailUser = ?");
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();


            if (resultSet.next()) {
                return  new Utilisateur( resultSet.getInt("idUser"),
                        resultSet.getString("nomUser"),
                        resultSet.getString("prenomUser"),
                        resultSet.getString("emailUser"),
                        resultSet.getString("mdp"),
                        resultSet.getInt("numTel"),

                        resultSet.getInt("nbPoints"),
                        resultSet.getString("role"));

            }
//            resultSet.close();
//            statement.close();
            return new Utilisateur();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUserEmailByToken(String token) {
        try {
            PreparedStatement statement = this.conn.prepareStatement("SELECT emailUser FROM password_reset_token WHERE token = ?");
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getString("emailUser") : "not found";
        } catch (SQLException var4) {
            var4.printStackTrace();
            return "system error";
        }
    }

    public void deleteToken(int id) {
        String requete = "DELETE FROM password_reset_token WHERE idUser = " + id;

        try {
            this.ste = this.conn.createStatement();
            this.ste.executeUpdate(requete);
        } catch (SQLException var4) {
            throw new RuntimeException(var4);
        }
    }





    private void closeResources(PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer les exceptions SQL lors de la fermeture des ressources
        }
    }

    public Utilisateur loginAdmin(String email, String password) {
        String query = "SELECT * FROM utilisateur WHERE emailUser=? AND mdp=? AND role='admin'";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Utilisateur(
                            resultSet.getInt("idUser"),
                            resultSet.getString("nomUser"),
                            resultSet.getString("prenomUser"),
                            resultSet.getString("emailUser"),
                            resultSet.getString("mdp"),
                            resultSet.getInt("nbPoints"),
                            resultSet.getInt("numTel"),
                            resultSet.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null; // Return null if login is unsuccessful
    }

    //// part asma

    public List<Utilisateur> getAllUsers() {
        List<Utilisateur> userList = new ArrayList<>();
        try {
            String query = "SELECT * FROM utilisateur";
            pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("idUser");
                int nbpoints = rs.getInt("nbpoints");
                String nomUser = rs.getString("nomUser");
                String prenomUser = rs.getString("prenomUser");
                String emailUser = rs.getString("emailUser");
                String numTel = rs.getString("numTel");
                Utilisateur user = new Utilisateur(id, nbpoints, nomUser, prenomUser, emailUser, Integer.parseInt(numTel));
                userList.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        } finally {
            // Fermeture des ressources (PreparedStatement, ResultSet, etc.)
        }
        return userList;
    }



    public void updateUserPoints(int userId, int newNbPoints) {
        try {
            String query = "UPDATE utilisateur SET nbpoints = ? WHERE idUser = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, newNbPoints);
            pst.setInt(2, userId);
            pst.executeUpdate();
            System.out.println("Points de l'utilisateur mis à jour avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour des points de l'utilisateur: " + e.getMessage());
        } finally {
            // Fermeture des ressources (PreparedStatement, ResultSet, etc.)
        }
    }


    @Override
    //back user
    public ObservableList<Utilisateur> userBack() {
        ObservableList<Utilisateur> userList = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM utilisateur where role ='Client' ";
            pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String nomUser = rs.getString("nomUser");
                String prenomUser = rs.getString("prenomUser");
                String emailUser = rs.getString("emailUser");
                int numTel = rs.getInt("numTel");
                Utilisateur user = new Utilisateur(nomUser, prenomUser, emailUser,numTel);
                userList.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        } finally {
            // Fermeture des ressources (PreparedStatement, ResultSet, etc.)
        }
        return userList;
    }
    @Override
    public List<Utilisateur> getAllUsersPub() {
        List<Utilisateur> users = new ArrayList<>();
        String query = "SELECT * FROM utilisateur";

        try {
            conn = DataSource.getInstance().getCnx();
            ste = conn.createStatement();
            ResultSet rs = ste.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("idUser");
                String nom = rs.getString("nomUser");
                String prenom = rs.getString("prenomUser");
                String email = rs.getString("emailUser");

                Utilisateur utilisateur = new Utilisateur(id, nom, prenom, email);
                users.add(utilisateur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ste != null) {
                    ste.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    //sort

    @Override
    public ObservableList<Utilisateur> sortUserNomDesc()
    {
        ObservableList<Utilisateur> userList = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM utilisateur where role ='Client' ORDER BY nomUser DESC ";
            pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String nomUser = rs.getString("nomUser");
                String prenomUser = rs.getString("prenomUser");
                String emailUser = rs.getString("emailUser");
                int numTel = rs.getInt("numTel");
                Utilisateur user = new Utilisateur(nomUser, prenomUser, emailUser,numTel);
                userList.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        } finally {
            // Fermeture des ressources (PreparedStatement, ResultSet, etc.)
        }
        return userList;
    }
    @Override
    public ObservableList<Utilisateur> sortUserNomAsc()
    {
        ObservableList<Utilisateur> userList = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM utilisateur where role ='Client' ORDER BY nomUser ASC ";
            pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String nomUser = rs.getString("nomUser");
                String prenomUser = rs.getString("prenomUser");
                String emailUser = rs.getString("emailUser");
                int numTel = rs.getInt("numTel");
                Utilisateur user = new Utilisateur(nomUser, prenomUser, emailUser,numTel);
                userList.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        } finally {
            // Fermeture des ressources (PreparedStatement, ResultSet, etc.)
        }
        return userList;
    }
    @Override
    public ObservableList<Utilisateur> sortUserPrenomDesc()
    {
        ObservableList<Utilisateur> userList = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM utilisateur where role ='Client' ORDER BY prenomUser DESC ";
            pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String nomUser = rs.getString("nomUser");
                String prenomUser = rs.getString("prenomUser");
                String emailUser = rs.getString("emailUser");
                int numTel = rs.getInt("numTel");
                Utilisateur user = new Utilisateur(nomUser, prenomUser, emailUser,numTel);
                userList.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        } finally {
            // Fermeture des ressources (PreparedStatement, ResultSet, etc.)
        }
        return userList;
    }
    @Override
    public ObservableList<Utilisateur> sortUserPrenomAsc()
    {
        ObservableList<Utilisateur> userList = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM utilisateur where role ='Client' ORDER BY prenomUser ASC ";
            pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String nomUser = rs.getString("nomUser");
                String prenomUser = rs.getString("prenomUser");
                String emailUser = rs.getString("emailUser");
                int numTel = rs.getInt("numTel");
                Utilisateur user = new Utilisateur(nomUser, prenomUser, emailUser,numTel);
                userList.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        } finally {
            // Fermeture des ressources (PreparedStatement, ResultSet, etc.)
        }
        return userList;
    }
}
