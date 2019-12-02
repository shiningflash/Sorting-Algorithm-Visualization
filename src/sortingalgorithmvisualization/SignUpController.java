package sortingalgorithmvisualization;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.sql.Statement;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import sun.misc.BASE64Encoder;

/**
 * @author shiningflash
 */

public class SignUpController implements Initializable {
    
    @FXML private Button loginButton;
    @FXML private TextField name;
    @FXML private TextField email;
    @FXML private PasswordField pass;
    @FXML private PasswordField repass;
    @FXML private Button signupButton;
    
    Connection connection;
    Statement statement;
    int res;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try { connection = DriverManager.getConnection("jdbc:derby://localhost:1527/myDatabase", "app", "app"); }
        catch(SQLException e) { }
    }   
    
    @FXML
    private void logIn(MouseEvent event) throws IOException {
        Parent dash = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
        Scene dashScene = new Scene(dash);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Sorting Algorithm Visualization");
        window.setScene(dashScene);
        window.show();
    }
    
    @FXML void addName(ActionEvent event) throws Exception {}
    @FXML void addEmail(ActionEvent event) {}
    @FXML void addPass(ActionEvent event) {}
    @FXML void addRePass(ActionEvent event) {}
    
    private boolean showError(String err) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(err);
        alert.showAndWait();
        return true;
    }

    private boolean checkName(String Name) {
        if (Name.isEmpty()) return showError("Please, enter your name");
        return false;
    }
    
    private boolean local_valid(char ch) {
        return ((ch >= (int) 35 && ch <= (int) 39) || (ch >= (int) 48 && ch <= (int) 57) || (ch >= (int) 65 && ch <= (int) 90) || (ch >= (int) 94 && ch <= (int) 126) || ch == (int) 33 || ch == (int) 61 || ch == (int) 63);
    }
    
    private boolean domain_valid(char ch, char pre) {
        if (ch == '.' && pre != '.') return true;
        return ((ch >= (int) 48 && ch <= (int) 57) || (ch >= (int) 65 && ch <= (int) 90) || (ch >= (int) 97 && ch <= (int) 122));
    }
    
    private boolean checkMail(String Mail) throws SQLException {
        // empty checking
        if (Mail.isEmpty()) return showError("Please, enter your email");
        
        // duplicate checking
        String duplicateCheck = "SELECT * FROM USER_SIGNUP WHERE EMAIL = '" + Mail + "'";
        PreparedStatement pst = connection.prepareStatement(duplicateCheck);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) return showError("An account already exists with this email");

        // email validity checking
        int i = 0, state = 0, f = 0;
        while (i < Mail.length()) {
            if (state == 0 && (local_valid(Mail.charAt(i)))) state = 1;
            else if (state == 1 && (local_valid(Mail.charAt(i)) || (Mail.charAt(i) == '.' && Mail.charAt(i-1) != '.'))) state = 1;
            else if (state == 1 && Mail.charAt(i) == '@') state = 2;
            else if ((state == 2 || state == 3) && domain_valid(Mail.charAt(i), Mail.charAt(i-1))) state = 3;
            else { f = 1; break; }
            i++;
        }
        if (f == 1 || state != 3) return showError("Enter a valid email address");
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
    private void signUp(MouseEvent event) throws SQLException, IOException, Exception {
        String Name = name.getText();
        if (checkName(Name)) return;
        String Email = email.getText();
        if (checkMail(Email)) return;
        String Pass = pass.getText();
        if (checkPass(Pass)) return;
        String RePass = repass.getText();
        if (checkRePass(Pass, RePass)) return;
        
        String encryptedPass = encrypt(Pass);
        
        String qp = "INSERT INTO USER_PASSWORD VALUES ("
                + "'" + Email + "'," 
                + "'" + encryptedPass + "'" + ")";
        connection = DriverManager.getConnection("jdbc:derby://localhost:1527/myDatabase", "app", "app");
        statement = connection.createStatement();
        res = statement.executeUpdate(qp);
        
        String q = "INSERT INTO USER_SIGNUP VALUES ("
                + "'" + Name + "'," 
                + "'" + Email + "',"
                + "'" + encryptedPass + "'" + ")";
        connection = DriverManager.getConnection("jdbc:derby://localhost:1527/myDatabase", "app", "app");
        statement = connection.createStatement();
        res = statement.executeUpdate(q);

        if (res != 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(null);
            alert.setContentText("SignUp Successful. Please, log in");
            alert.showAndWait();
            Parent dash = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
            Scene dashScene = new Scene(dash);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle("Sorting Algorithm Visualization");
            window.setScene(dashScene);
            window.show();
        }
    }
}

