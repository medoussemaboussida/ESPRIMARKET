package service;

import entities.CodePromo;
import utils.DataSource;

import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CodePromoService implements IServiceCodePromo <CodePromo>  {

    private Connection conn;
    private Statement statement;
    private PreparedStatement pst;
    public CodePromoService()
    {
        conn= DataSource.getInstance().getCnx();
    }

    @Override
    public void addCodePromo(CodePromo c) {
        String insertCodePromoQuery = "insert into codePromo ( code,reductionAssocie,dateDebut,dateFin) values (?,?,?,?)";
        try {
            PreparedStatement pst = conn.prepareStatement(insertCodePromoQuery, Statement.RETURN_GENERATED_KEYS);;
            pst.setInt(2,c.getReductionAssocie());
            pst.setString(1,c.getCode());
            if (c.getDateDebut() != null) {
                pst.setDate(3, new java.sql.Date(c.getDateDebut().getTime()));
            } else {
                pst.setNull(3, Types.DATE);
            }
            // Gestion de la date de fin
            if (c.getDateFin() != null) {
                pst.setDate(4, new java.sql.Date(c.getDateFin().getTime()));
            } else {
                pst.setNull(4, Types.DATE);
            }

            pst.executeUpdate();
            System.out.println("Code promo ajouté!");
            //String to = getUserEmail(reponse.getId_reclamation()); // Get user email from database
            // Envoyer l'email à tous les utilisateurs
            List<String> userEmails = getAllUserEmails(); // Récupérer les emails des utilisateurs
            for (String email : userEmails) {
                String subject = "Nouveaux Code Promo Ajouter";
                String body = "Bonjour,\n\nNouveaux Code Promo est ajouté dans notre épicerie de nom : " + c.getCode() + " et de réduction "+c.getReductionAssocie()+"% .\n\nCordialement,\n de Esprit Market";
                sendEmail(email, subject, body); // Envoyer l'email
            }

        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

    }



    @Override
    public List<CodePromo> readCodePromo() throws SQLException {
        String requete = "select * from codePromo";
        List<CodePromo> list=new ArrayList<>();
        try {
            statement = conn.createStatement();
            ResultSet rs=statement.executeQuery(requete);
            while (rs.next()) {
                CodePromo codePromo = new CodePromo();
                codePromo.setIdCode(rs.getInt("idCode"));
                codePromo.setReductionAssocie(rs.getInt("reductionAssocie"));
                codePromo.setCode(rs.getString("code"));
                codePromo.setDateDebut(rs.getDate("dateDebut"));
                codePromo.setDateFin(rs.getDate("dateFin"));
                list.add(codePromo);

            }
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public void deleteCodePromo(int id) {
        String requete = "delete from CodePromo where idCode = ?";
        try {
            pst=conn.prepareStatement(requete);
            pst.setInt(1,id);
            pst.executeUpdate();
            System.out.println("Code Promo supprimé!");
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void modifyCodePromo(CodePromo c)
    {
        String requete = "UPDATE CodePromo set Code = ?, reductionAssocie = ?, dateDebut = ?, dateFin = ? where idCode= ?";
        try {
            pst = conn.prepareStatement(requete);
            pst.setString(1,c.getCode() );
            pst.setInt(2,c.getReductionAssocie());
            pst.setDate(3, new Date(c.getDateDebut().getTime()));
            pst.setDate(4, new Date(c.getDateFin().getTime()));
            pst.setInt(5,c.getIdCode());

            pst.executeUpdate();
            System.out.println("Code promo Modifiée!");
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllUserEmails() throws SQLException {
        List<String> emails = new ArrayList<>();
        String query = "SELECT emailUser FROM Utilisateur"; // Assurez-vous que le nom de la colonne et de la table sont corrects
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                emails.add(rs.getString("emailUser"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des e-mails des utilisateurs: " + e.getMessage());
            throw e;
        }
        return emails;
    }

    private void sendEmail(String to, String subject, String body) {
        String username = "ghassenbenmahmoud3@gmail.com";
        String password = "gogm cdyo rxrg kuxv";
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Change this to your SMTP server host(yahoo...)
        props.put("mail.smtp.port", "587"); // Change this to your SMTP server port
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session;
        session = Session.getInstance(props,new Authenticator() {
            protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                return new jakarta.mail.PasswordAuthentication(username, password);
            }
        });


        try {
            // Create a MimeMessage object

            // Create a new message
            MimeMessage message = new MimeMessage(session);
            // Set the From, To, Subject, and Text fields of the message
            message.setFrom(new InternetAddress(username));
            message.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);

            // Send the message using Transport.send
            Transport.send(message);

            System.out.println("Email sent successfully");
        } catch (MessagingException ex) {
            System.err.println("Failed to send email: " + ex.getMessage());
        }

    }

    }



