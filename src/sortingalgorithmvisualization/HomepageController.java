package sortingalgorithmvisualization;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.SequentialTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.beans.value.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * @author shiningflash
 */
public class HomepageController implements Initializable {
    
    @FXML private Button logoutButton;
    @FXML private Button pauseButton;
    @FXML private Button sortButton;
    @FXML private Button randomButton;
    @FXML private ChoiceBox<AbstractSort> chooseBox;
    @FXML private ChoiceBox<Integer> nodeBox;
    @FXML private ChoiceBox<Integer> speedBox;
    @FXML private Pane display;
    @FXML private Label comp;
    
    Connection connection;
    SequentialTransition st;
    boolean running = false;
    
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 350;
    public static final int BUTTON_BOUNDARY = 100;
    public static final int X_GAP = 10;
    public static int NO_OF_NODES = 20;
    public static int SPEED = 50;
    
    private Node nodes[];
    public static AbstractSort abstractSort;
    
    @FXML
    void randomButtonAction(MouseEvent event) {
        generator();
    }
    
    @FXML
    void pauseButtonAction(MouseEvent event) {
        if (running) {
            st.pause();
            pauseButton.setText("Continue");
            running = false;
        }
        else {
            st.play();
            pauseButton.setText("Pause");
            running = true;
        }
    }

    @FXML
    void sortButtonAction(MouseEvent event) {
        running = true;
        sortButton.setDisable(true);
        logoutButton.setDisable(true);
        randomButton.setDisable(true);
        pauseButton.setDisable(false);
        comp.setFont(Font.font("Verdana", 15));
        comp.setText("Sorting running....");
        comp.setTextFill(Color.web("#D8D8D8"));
        display.getChildren().add(comp);
        //logoutButton.setDisable(true);
        
        abstractSort = chooseBox.getSelectionModel().getSelectedItem();
        st = new SequentialTransition();
        st.getChildren().addAll(abstractSort.startSort(nodes));
        st.setOnFinished(e -> {
            randomButton.setDisable(false);
            pauseButton.setDisable(true);
            logoutButton.setDisable(false);
            running = false;
            comp.setText("Array is sorted");
        });
        st.play();
    }
    
    @FXML
    void logoutProcess(MouseEvent event) throws IOException {
        Parent dash = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
        Scene dashScene = new Scene(dash);
        Stage window = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        window.setTitle("Sorting Algorithm Visualization");
        window.setScene(dashScene);
        window.show();
    }
    
    public void generator() {
        sortButton.setDisable(false);
        display.getChildren().clear();
        this.nodes = GenerateRandomNodes.GenerateRandomNodes(NO_OF_NODES);
        display.getChildren().addAll(Arrays.asList(nodes)); 
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try { connection = DriverManager.getConnection("jdbc:derby://localhost:1527/myDatabase", "app", "app"); }
        catch(SQLException e) { }
        
        pauseButton.setDisable(true);
        
        this.nodes = GenerateRandomNodes.GenerateRandomNodes(NO_OF_NODES);
        display.getChildren().addAll(Arrays.asList(nodes));
        generator();
        //for (int i = 0; i < NO_OF_NODES; i++) System.out.println(nodes[i].getValue());
        
        Integer speed[] = {1, 5, 10, 50, 100, 500};
        speedBox.setValue(50);
        Tooltip tt1 = new Tooltip();
        tt1.setStyle("-fx-base: #AE3522; " + "-fx-text-fill: BLACK;" + "-fx-background-color : WHITE;");
        tt1.setText("Choose Speed");
        speedBox.setTooltip(tt1);
        speedBox.setItems(FXCollections.observableArrayList(speed));

        speedBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                Integer n = new Integer(newValue);
                SPEED = n.intValue();
            } 
        });
        
        List <AbstractSort> sortList = new ArrayList<>();
        sortList.add(new InsertionSort());
        sortList.add(new SelectionSort());
        sortList.add(new QuickSort());
        sortList.add(new BubbleSort());
        
        chooseBox.setValue(new InsertionSort());
        Tooltip tt2 = new Tooltip();
        tt2.setStyle("-fx-base: #AE3522; " + "-fx-text-fill: BLACK;" + "-fx-background-color : WHITE;");
        tt2.setText("Choose Sorting Algorithm");
        chooseBox.setTooltip(tt2);
        chooseBox.setItems(FXCollections.observableArrayList(sortList));
        chooseBox.getSelectionModel().select(4);
        
        chooseBox.setConverter(new StringConverter<AbstractSort>() {
            @Override
            public String toString(AbstractSort absSort) {
                if (absSort == null) return "";
                else return absSort.getClass().getSimpleName();
            }
            
            @Override
            public AbstractSort fromString(String s) {
                return null;
            }
        });
    }    
    
}

