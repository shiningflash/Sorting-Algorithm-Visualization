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

public class LogInController implements Initializable {

    @FXML private TextField email;
    @FXML private PasswordField pass;
    @FXML private Button signupButton;
    @FXML private Button loginButton;
    @FXML private Button fpass;
    
    Connection connection;
    Statement statement;
    int res;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try { connection = DriverManager.getConnection("jdbc:derby://localhost:1527/myDatabase", "app", "app"); }
        catch(SQLException e) { }
    }   
    
    @FXML
    private void SignUp(MouseEvent event) throws IOException {
        Parent dash = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
        Scene dashScene = new Scene(dash);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Sorting Algorithm Visualization");
        window.setScene(dashScene);
        window.show();
    }
    
    private void showError(String err) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(err);
        alert.showAndWait();
    }

    private boolean checkMail(String Mail) {
        if (Mail.isEmpty()) {
            showError("Please, enter your email");
            return true;
        }
        return false;
    }
    
    private boolean checkPass(String Pass) {
        if (Pass.isEmpty()) {
            showError("Please enter your password");
            return true;
        }
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
    void logIn(MouseEvent event) throws SQLException, Exception {
        String Email = email.getText();
        if (checkMail(Email)) return;
        String Pass = pass.getText();
        if (checkPass(Pass)) return;
        
        String q = "SELECT * FROM USER_SIGNUP WHERE EMAIL = '" + Email + "'";
        connection = DriverManager.getConnection("jdbc:derby://localhost:1527/myDatabase", "app", "app");
        System.out.println("Database Connected");
        PreparedStatement pst = connection.prepareStatement(q);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            String password = rs.getString(3);
            String encryptedPass = encrypt(Pass);
            if (encryptedPass.equals(password)) {
                Parent signin = FXMLLoader.load(getClass().getResource("Homepage.fxml"));
                Scene signinScene = new Scene(signin);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setTitle("Sorting Algorithm Visualization");
                window.setScene(signinScene);
                window.show();
            }
            else {
                showError("Please, enter correct password");
                return;
            }
        }
        else {
            showError("Please, enter correct email");
            return;
        }
    }
    
    @FXML
    void forgotPass(MouseEvent event) throws IOException {
        Parent dash = FXMLLoader.load(getClass().getResource("ChangePass.fxml"));
        Scene dashScene = new Scene(dash);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Sorting Algorithm Visualization");
        window.setScene(dashScene);
        window.show();
    }
}
