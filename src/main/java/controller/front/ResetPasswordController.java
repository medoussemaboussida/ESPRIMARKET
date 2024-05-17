package controller.front;

import entities.PasswordResetToken;
import entities.Utilisateur;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;
import service.UtilisateurService;

public class ResetPasswordController implements Initializable {

    @FXML
    private TextField code;

    @FXML
    private TextField confirmPasswordField;

    @FXML
    private TextField newPasswordField;

    @FXML
    private Button resetPasswordButton;

    public ResetPasswordController() {
    }

    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void resetPassword(ActionEvent event) throws IOException {
        UtilisateurService su = new UtilisateurService();
        String mail = su.getUserEmailByToken(this.code.getText());
        Utilisateur user = su.getUserByEmail(mail);
        if (!this.isValidCode(this.code.getText().trim())) {
            PasswordResetToken resetToken = su.getPasswordResetToken(this.code.getText());
            System.out.println("Before");
            if (resetToken != null) {
                long now = System.currentTimeMillis();
                long elapsedTime = now - resetToken.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long expiryTime = TimeUnit.MINUTES.toMillis(30L);
                System.out.println("now: " + now + ", token timestamp: " + resetToken.getTimestamp() + ", elapsed time: " + elapsedTime);
                if (elapsedTime <= expiryTime) {
                    String hashedPassword = BCrypt.hashpw(this.newPasswordField.getText(), BCrypt.gensalt());
                    su.updateUserPassword(user.getIdUser(), hashedPassword);
                } else {
                    Alert expiredAlert = new Alert(AlertType.ERROR);
                    expiredAlert.setTitle("Password Reset");
                    expiredAlert.setHeaderText("Token Expired");
                    expiredAlert.setContentText("Sorry, the password reset token has expired.");
                    expiredAlert.showAndWait();
                }
            } else {
                Alert invalidCodeAlert = new Alert(AlertType.ERROR);
                invalidCodeAlert.setTitle("Password Reset");
                invalidCodeAlert.setHeaderText("Invalid Code");
                invalidCodeAlert.setContentText("Sorry, the code you entered is invalid.");
                invalidCodeAlert.showAndWait();
            }
        }

        this.reset();
    }

    public void reset() {
        String token = this.code.getText();
        UtilisateurService su = new UtilisateurService();
        PasswordResetToken resetToken = su.getPasswordResetToken(token);
        System.out.println(resetToken == null || resetToken.isExpired());
        if (resetToken != null && !resetToken.isExpired()) {
            String newPassword = this.newPasswordField.getText();
            String confirmPassword = this.confirmPasswordField.getText();
            Alert alert;
            if (!this.isValidPassword(newPassword)) {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Password Reset");
                alert.setHeaderText("Invalid Password");
                alert.setContentText("Sorry, the new password you entered is not valid. Please try again.");
                alert.showAndWait();
            } else if (!newPassword.equals(confirmPassword)) {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Password Reset");
                alert.setHeaderText("Passwords do not match");
                alert.setContentText("Sorry, the new passwords you entered do not match. Please try again.");
                alert.showAndWait();
            } else {
                su.updateUserPassword(resetToken.getuserId(), newPassword);
                alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Password Reset");
                alert.setHeaderText("Password reset successful");
                alert.setContentText("Your password has been reset. Please log in again using your new password.");
                alert.showAndWait();
                su.deleteToken(resetToken.getuserId());

                try {
                    FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/Seconnecter.fxml"));
                    Parent root = (Parent)loader.load();
                    SeconnecterController auc = (SeconnecterController) loader.getController();
                    this.code.getScene().setRoot(root);
                } catch (IOException var10) {
                }

            }
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Password Reset");
            alert.setHeaderText("Invalid or Expired Token");
            alert.setContentText("Sorry, the password reset link you clicked is no longer valid. Please try again.");
            alert.showAndWait();
        }
    }

    private String getResetTokenFromURL() {
        String url = this.getClass().getResource("../resources/ResetPassword.fxml").toString();
        int tokenIndex = url.indexOf("token=") + 6;
        return url.substring(tokenIndex);
    }

    private boolean isValidPassword(String password) {
        return true;
    }

    private boolean isValidCode(String code) {
        String trimmedCode = code.trim();
        String pattern = "[a-zA-Z0-9]{6}";
        return trimmedCode.matches(pattern);
    }
}
