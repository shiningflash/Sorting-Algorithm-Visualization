package sortingalgorithmvisualization;

/**
 * @author shiningflash
 */

import java.util.Random;
import javafx.scene.paint.Color;

public class GenerateRandomNodes {
    public GenerateRandomNodes(){};
    
    public static Node[] GenerateRandomNodes(int len) {
        Node nodes[] = new Node[len];
        Random r = new Random();
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(1 + r.nextInt(nodes.length));
            nodes[i].setX(i * (HomepageController.WINDOW_WIDTH / nodes.length) + 5);
            nodes[i].setFill(Color.WHITE);
            setNodeDim(nodes[i], nodes.length);
            //System.out.println(nodes[i]);
        }
        return nodes;
    }
    
    public static void setNodeDim(Node nodes, int len) {
        nodes.setWidth(HomepageController.WINDOW_WIDTH / len - HomepageController.X_GAP);
        nodes.setHeight(((HomepageController.WINDOW_HEIGHT - HomepageController.BUTTON_BOUNDARY) / len) * nodes.getValue());
    }
}
