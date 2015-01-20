package alvis301;

import java.util.ArrayList;
import java.util.Comparator;
import java.lang.Math;

/**
 *
 * @author tapan
 * used to sort nodes based on their h values from a goal
 */
public class HComparator implements Comparator<ArrayList<Node>>{

    Node goal;
    public HComparator(Node goal)
    {
        this.goal=goal;
    }
    
    public double dist(double ax,double ay, double bx, double by)
    {
        return Math.sqrt(Math.pow(ax-bx, 2)+Math.pow(ay-by,2));
    }
    
    public int compare(ArrayList<Node> o1, ArrayList<Node> o2) {
        Node a = o1.get(0);
        Node b = o2.get(0);
        double val = dist(a.getX(),a.getY(),goal.getX(),goal.getY());
        double val2 = dist(b.getX(),b.getY(),goal.getX(),goal.getY());
        return (int)(val-val2);
 
    }
    
}
