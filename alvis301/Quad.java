package alvis301;

/**
 * 
 * @author Stanley
 *
 */

public class Quad
    {
    /** Contains information about the node in the tree */
    public Node j;
    /** Is used to know whether the node is live or solved */
    public Label s;
    /** The value associated with the node */
    public int h;
    /** The leaf node corresponding to the h value */
    public Node leaf;
    
    Quad(Node node, Label label, int value, Node leaf)
        {
        this.j = node;
        this.s = label;
        this.h = value;
        this.leaf = leaf;
        }
    }