package sortingalgorithmvisualization;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.animation.Transition;

/**
 * @author shiningflash
 */

public class BubbleSort extends AbstractSort {
    private ArrayList<Transition> transitions;
    private boolean f;
    
    public BubbleSort() {
        transitions = new ArrayList<>();
    }
    
    private ArrayList<Transition> compare(Node nodes[], int i, int j) {
        ArrayList<Transition> transitions = new ArrayList<>();
        transitions.add(colorNode(nodes, SELECT_COLOR, i, j));
        if (nodes[i].getValue() > nodes[j].getValue()) {
            transitions.add(swap(nodes, i, j));
            f = true;
        }
        transitions.add(colorNode(nodes, START_COLOR, i, j));
        return transitions;
    } 
    
    private void bubbleSort(Node[] nodes) {
        int len = nodes.length-1;
        for (int i = 0; i < nodes.length; i++) {
            f = false;
            for (int j = 0; j < nodes.length-1-i; j++) {
                transitions.addAll(compare(nodes, j, j+1));
            }
            transitions.add(colorNode(nodes, SORTED_COLOR, len--));
            if (!f) break;
        }
    }
    
    @Override
    public ArrayList<Transition> startSort(Node[] nodes) {
        bubbleSort(nodes);
        transitions.add(colorNode(Arrays.asList(nodes), FINAL_COLOR));
        return transitions;
    }
}
