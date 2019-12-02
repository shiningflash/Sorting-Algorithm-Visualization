package sortingalgorithmvisualization;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sun.misc.BASE64Encoder;

/**
 * @author shiningflash
 */
public class ChangePassController implements Initializable {
    
    @FXML private TextField email;
    @FXML private PasswordField pass;
    @FXML private PasswordField repass;
    @FXML private Button loginButton;
    
    @FXML void addEmail1(ActionEvent event) {}
    @FXML void addPass1(ActionEvent event) {}
    @FXML void addRepass1(ActionEvent event) {}
    
    Connection connection;
    Statement statement;
    int res;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try { connection = DriverManager.getConnection("jdbc:derby://localhost:1527/myDatabase", "app", "app"); }
        catch(SQLException e) { }
    }    

    @FXML
    void gotoLogInButton(MouseEvent event) throws IOException {
        Parent dash = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
        Scene dashScene = new Scene(dash);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Sorting Algorithm Visualization");
        window.setScene(dashScene);
        window.show();
    }
    
    private boolean showError(String err) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(err);
        alert.showAndWait();
        return true;
    }
    
    private boolean checkMail(String Mail) {
        if (Mail.isEmpty()) {
            showError("Please, enter your email");
            return true;
        }
        return false;
    }
    
    private boolean checkPass(String Pass) {
        // empty checking
        if (Pass.isEmpty()) return showError("Please enter your password");
        
        // length checking
        if (Pass.length() < 6) return showError("The length of password must be at least 6 characters");
        
        // strength checking
        int a = 0, b = 0, c = 0;
        for (int i = 0; i < Pass.length(); i++) {
            if (Pass.charAt(i) >= '0' && Pass.charAt(i) <= '9') a = 1;
            else if (Pass.charAt(i) >= 'A' && Pass.charAt(i) <= 'Z') b = 1;
            else if (Pass.charAt(i) >= 'a' && Pass.charAt(i) <= 'z') c = 1;
            if (a + b + c == 3) break;
        }
        if (a + b + c < 3) return showError("Password must contain at least 1 uppercase letter, 1 lowercase letter and 1 numeric number");
        
        return false;
    }
    
    private boolean checkRePass(String Pass, String RePass) {
        // empty checking
        if (RePass.isEmpty()) return showError("Please, enter your repeat password");
        
        // equality checking
        if (!Pass.equals(RePass)) return showError("Password doesn't match");
        
        return false;
    }
    
    public synchronized String encrypt(String pass) throws Exception {
        String hashValue = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(pass.getBytes("UTF-16"));
            byte bt[] = md.digest();
            hashValue = (new BASE64Encoder()).encode(bt);
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Something went wrong. Give another password.");
            alert.showAndWait();
        }
        return hashValue;
    } 

    @FXML
    void changePassword(MouseEvent event) throws IOException, SQLException, Exception {
        String Email = email.getText();
        if (checkMail(Email)) return;
        String Pass = pass.getText();
        if (checkPass(Pass)) return;
        String RePass = repass.getText();
        if (checkRePass(Pass, RePass)) return;
        
        String encryptedPass = encrypt(Pass);
        
        String q = "SELECT * FROM USER_SIGNUP WHERE EMAIL = '" + Email + "'";
        connection = DriverManager.getConnection("jdbc:derby://localhost:1527/myDatabase", "app", "app");
        PreparedStatement pst = connection.prepareStatement(q);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            String qp1 = "SELECT * FROM USER_PASSWORD WHERE EMAIL = '" + Email + "'AND PASSWORD = '" + encryptedPass + "'";
            connection = DriverManager.getConnection("jdbc:derby://localhost:1527/myDatabase", "app", "app");
            PreparedStatement pst1 = connection.prepareStatement(qp1);
            ResultSet rs1 = pst1.executeQuery();
            System.out.println(rs1);
            if (rs1.next()) {
                showError("You can't set any previous password as new password. Try another one.");
                return;
            }
            else {
                String qp2 = "INSERT INTO USER_PASSWORD VALUES ("
                    + "'" + Email + "'," 
                    + "'" + encryptedPass + "'" + ")";
                connection = DriverManager.getConnection("jdbc:derby://localhost:1527/myDatabase", "app", "app");
                statement = connection.createStatement();
                res = statement.executeUpdate(qp2);
                
                String qp3 = "UPDATE USER_SIGNUP SET PASS = '" + encryptedPass + "'WHERE EMAIL = '" + Email +"'";
                connection = DriverManager.getConnection("jdbc:derby://localhost:1527/myDatabase", "app", "app");
                statement = connection.createStatement();
                res = statement.executeUpdate(qp3);
                
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setContentText("Password changed");
                alert.showAndWait();
                
                Parent signin = FXMLLoader.load(getClass().getResource("Homepage.fxml"));
                Scene signinScene = new Scene(signin);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setTitle("Sorting Algorithm Visualization");
                window.setScene(signinScene);
                window.show();
            }
        }
        else {
            showError("Please, enter correct email");
            return;
        }
    }    
}

