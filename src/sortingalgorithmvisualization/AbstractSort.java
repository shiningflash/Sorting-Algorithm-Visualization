package sortingalgorithmvisualization;

import sortingalgorithmvisualization.HomepageController;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.util.Duration;

/**
 * @author shiningflash
 */

public abstract class AbstractSort {
    public final Color START_COLOR = Color.WHITE;
    public final Color SELECT_COLOR = Color.CYAN;
    public final Color FINAL_COLOR = Color.web("0xe53935",1.0);
    public final Color SORTED_COLOR = Color.web("0xf4511e",1.0);
    
    static int X;
    ParallelTransition p;
    FillTransition f;
    
    static {
        X = HomepageController.WINDOW_WIDTH / HomepageController.NO_OF_NODES;
    }
    
    void fillTransion(Node c, Color color) {
        f = new FillTransition();
        f.setShape(c);
        f.setToValue(color);
        f.setDuration(Duration.millis(HomepageController.SPEED));
        p.getChildren().add(f);
    }
    
    ParallelTransition colorNode(Node[] arr, Color color, int...a) {
        p = new ParallelTransition();
        for (int i = 0; i < a.length; i++) {
            fillTransion(arr[a[i]], color);
        }
        return p;
    }
    
    ParallelTransition colorNode(List<Node> list, Color color) {
        p = new ParallelTransition();
        for (Node c : list) {
            fillTransion(c, color);
        }
        return p;
    }
    
    ParallelTransition swap(Node a[], int i, int j) {
        p = new ParallelTransition();
        int dx = j-i;
        p.getChildren().addAll(a[i].moveX(X * dx), a[j].moveX(-X * dx));
        Node tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
        return p;
    }
    
    public abstract ArrayList<Transition> startSort(Node[] a);
}
