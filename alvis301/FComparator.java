package alvis301;

import java.util.Comparator;

/**
 *
 * @author tapan
 * Used to sort classes based on f values
 * this is used for the dcbss algorithm
 */
public class FComparator implements Comparator<Node>{

    public int compare(Node o1, Node o2) {
        return (int)(((DCBSSNodeData)o1.getData()).fVal - ((DCBSSNodeData)o2.getData()).fVal);
    }
    
}
