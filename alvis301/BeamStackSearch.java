/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alvis301;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 *
 * @author kevin
 */
class StackEntry
{
    public double fmin;
    public double fmax;
    
    StackEntry(double fmin, double fmax)
    {
        this.fmin = fmin;
        this.fmax = fmax;
    }
};

class NodePair
{       
    public Node n;
    public double val;            
    public NodePair parent;    
    
    NodePair(Node n, double val, NodePair parent)
    {
        this.n=n;
        this.val = val;
        this.parent = parent;
    }
    
    NodePair()
    {        
    }
};

public class BeamStackSearch extends Algorithm 
{
    int beamWidth;
    double upperBound; 
    PriorityQueue<NodePair> open;
    ArrayList<NodePair> closed;        
    ArrayList<NodePair> bestPath;     
    Stack<StackEntry> beamStack;    
    NodePair sp;//start pair
    NodePair bp;//best pair
    Stack<ArrayList<NodePair>> backtrackDaemon;
    
    public BeamStackSearch(int t)
    {
        super(t);
    }
    
    @Override
    public boolean goalTest(Node goalNode)
    {              
        int nodeID = goalNode.getNodeID();
        return (nodeID == g.getGoalID());
    }

    public ArrayList<Node> moveGen(Node parentNode)
    {       
        ArrayList<Node> children = parentNode.getAdjList();      
        ArrayList<Node> distinct_Children = new ArrayList();      
        boolean distinct, contains;
                
        //Removing duplicates
        for(Node n1:children)
        {
            distinct = true;
            contains = false;
            for(Node n2: distinct_Children)
                if(n1.getNodeID() == n2.getNodeID())
                {
                    distinct = false;
                    break;                                           
                }
            
            for(NodePair np: closed)  //TODO: Is this necessary here?
                if(np.n.getNodeID() == n1.getNodeID())
                    contains = true;

            if(distinct && !contains)
            distinct_Children.add(n1);
          }
 
        for (Node n: distinct_Children)
            System.out.println("Child of " + parentNode.getNodeID() + ": " + n.getNodeID());
        
        return distinct_Children;
    }

    public void init()
    {
        open = new PriorityQueue(1000, new Comparator<NodePair>() { 
                public int compare(NodePair np1, NodePair np2)
                {
                    Double a = np1.val;
                    Double b = np2.val;
                    return a.compareTo(b);
                }});    
        closed = new ArrayList();
        beamStack = new Stack();
        
        beamWidth  = 4;//Integer.valueOf(getInput("Enter the Beam Width: "));
        upperBound = 10000;
        
        sp = new NodePair(g.getNode(g.getStartID()), 0, null);
        backtrackDaemon = new Stack();
        bestPath = new ArrayList();
        
        System.out.println("Beam width: " + beamWidth);  
        System.out.println("Upper Bound: " + upperBound);
        System.out.println("Start: " + g.getStartID());
        System.out.println("Goal: " + g.getGoalID());  
    }
    
    @Override
    public void run()
    {     
        ArrayList<NodePair> temp = new ArrayList();
        //Initialize all data structure
        init();
        
        //Initialize beam stack
        beamStack.push(new StackEntry(0, upperBound));                

        open.add(sp);   
        temp.add(sp);
        backtrackDaemon.push(temp);
        
        while(!beamStack.empty())
        {
            NodePair gp = BSS();

            if(gp!=null)
            {              
                bp = gp;
                reconstructPath(gp);
                display();
                System.out.println("Goal found, with cost:" + gp.val);
                displayMessage("Goal found, with cost:" + gp.val);             
                upperBound = gp.val;                
            }
 
            //When beamStack search backtracks, it removes from the top consecutive items with an fmax greater than or equal to U
            while(beamStack.peek().fmax >= upperBound)
            {
                beamStack.pop();
                backtrackDaemon.pop();
            }
            System.out.println("Backtracking: " + beamStack.peek().fmin + " " + beamStack.peek().fmax+ " " + upperBound);
           
            if(beamStack.empty())
                break;     
                       
            //The algorithm uses the current fmax as the new fmin, and the upper bound U
            //as the new fmax when it backtracks to a layer
            StackEntry se = beamStack.pop();
            se.fmin = se.fmax;
            se.fmax = upperBound;
            beamStack.push(se)  ;     
            
            for(NodePair np: closed)
            {
                np.n.setState(alvis301.State.old);
                updateNode(np.n); 
                bestPath.add(np);
            }

            open.clear();
            closed.clear();
            
            //Launch backtrack daemon           
            temp = backtrackDaemon.peek();
            for(NodePair np: temp)
                open.add(np);
        }
        
        displayMessage("BSS exiting nicely.");
        closed.clear();
        for(NodePair np: bestPath)
            closed.add(np);
        reconstructPath(bp);
    }
            
    public NodePair BSS()
    {                   
        NodePair gp = null;
        NodePair currentNodePair = null;
        ArrayList<NodePair> temp;
        PriorityQueue<NodePair> open_new = new PriorityQueue(1000, new Comparator<NodePair>() { 
                public int compare(NodePair np1, NodePair np2)
                {
                    Double a = np1.val;
                    Double b = np2.val;
                    return a.compareTo(b);
                }});
               
        while(!open.isEmpty())
        {                         
            while(!open.isEmpty())
            {                
                //Pick node from OPEN
                currentNodePair = open.poll();
              
                //Move the node to CLOSED
                if(currentNodePair.parent != null) //for start node, val = 0, all others only actual cost needed
                    currentNodePair.val = actualCost(currentNodePair.parent, currentNodePair.n);
                else 
                    currentNodePair.val = 0;
                currentNodePair.n.setState(alvis301.State.closed);
                updateNode(currentNodePair.n);
                closed.add(currentNodePair);

                //Check whether node picked from OPEN is a goal node
                if(goalTest(currentNodePair.n))
                {
                    upperBound = currentNodePair.val;
                    gp = currentNodePair;    
                }

                //Generate all its children
                ArrayList<Node> children = moveGen(currentNodePair.n);

                //Add the children to OPEN list of next layer
                for(Node child: children)
                {
                    //Opening the edge
                    ArrayList<Edge> adjEdgeList = child.getAdjEdgeList();
                    for (Edge e : adjEdgeList)
                    {
                        if(e.getNodeID1() == currentNodePair.n.getNodeID() || e.getNodeID2() == currentNodePair.n.getNodeID())
                        {
                            e.setState(alvis301.State.open);   
                            updateEdge(e);
                            break;
                        }
                    }            

                    //Create node pair for the child node
                    NodePair np = new NodePair();
                    np.n = child;
                    np.val = actualCost(currentNodePair, child) + heuristicCost(child, g.getNode(g.getGoalID()));
                    np.parent = currentNodePair;

                    //When expanding nodes in a layer associated with an item with range [fmin, fmax), the algorithm
                    //prunes any successor node (generated for the next layer) with an f-cost less than fmin , or greater
                    //than or equal to fmax
                    System.out.println(beamStack.peek().fmin + " " +  beamStack.peek().fmax + " " + np.val);
                    if( np.val < beamStack.peek().fmin || np.val >= beamStack.peek().fmax)
                        continue;       
                    open_new.add(np);                            
              
                    //Closing the edge
                    for (Edge e : adjEdgeList)
                    {
                        if(e.getNodeID1() == currentNodePair.n.getNodeID() || e.getNodeID2() == currentNodePair.n.getNodeID())
                        {
                            e.setState(alvis301.State.closed);   
                            updateEdge(e);
                            break;
                        }               
                    }                             
                }                                    
            }

            //Remove duplicates and move the nodes to OPEN list
            temp = new ArrayList();
            for(NodePair np:open_new)
                temp.add(np);
            temp = removeDuplicates(temp);    
            for(NodePair np: temp)
            {
                open.add(np);
            }
            
            //Prune the OPEN list
            if(open.size() > beamWidth)
                open = pruneLayer(open);
            
            //Move nodes in OPEN to backtrack dameon
            if(!open.isEmpty())
                   temp.clear();
            for(NodePair np: open)
                temp.add(np);
            backtrackDaemon.push(temp);
            
            //Updating the state of nodes in OPEN
            for (NodePair np:open)
            {
                    np.n.setState(alvis301.State.open);
                    updateNode(np.n);
                    System.out.println("New node added to OPEN: " + np.n.getNodeID() + " with cost " + np.val);
                    display();    
            }     
                             
            open_new.clear();
            beamStack.push(new StackEntry(0, upperBound));
        }    
        return gp;           
    }

    //Prune layer 
    public PriorityQueue<NodePair> pruneLayer(PriorityQueue<NodePair> tempList)
    {                
        PriorityQueue<NodePair> pq = new PriorityQueue<NodePair>(1000, new Comparator<NodePair>() { 
        public int compare(NodePair np1, NodePair np2)
        {
            Double a = np1.val;
            Double b = np2.val;
            return a.compareTo(b);
        }});
        
        for(int i=0;i<beamWidth;i++)
            pq.add(tempList.poll());
        
        //Update fmax
        StackEntry se = beamStack.pop();
        se.fmax = tempList.peek().val;
        beamStack.push(se);     
        
        return pq;
    }
       
    //Actual cost from Start node
    public double actualCost(NodePair parentPair, Node ctNode)
    {
        double cost = 0.0;
        ArrayList<Edge> adjEdgeList = ctNode.getAdjEdgeList();
        for (Edge e : adjEdgeList)
        {
            cost = parentPair.val + e.getCost();
            if(e.getNodeID1() == parentPair.n.getNodeID() || e.getNodeID2() == parentPair.n.getNodeID())
               break;
        }
        return cost;
    }

    //Calculating the heursitic(Euclidean Distance) to Goal node
    public double heuristicCost(Node ctNode, Node dest)
    {
        double x1 = ctNode.getX();        double x2 = dest.getX();
        double y1 = ctNode.getY();        double y2 = dest.getY();
        
	double  xDiff = x1-x2;
        double  xSqr  = Math.pow(xDiff, 2);

	double yDiff = y1-y2;
	double ySqr = Math.pow(yDiff, 2);

        return Math.sqrt(xSqr + ySqr);
    }
    
    //Reconstructing the path once goal node is found
    public void reconstructPath(NodePair gp)
    {
        while(gp.parent != null)
        {
            gp.n.setState(alvis301.State.path);   
            updateNode(gp.n);

            gp.parent.n.setState(alvis301.State.path);   
            updateNode(gp.parent.n);
            
            ArrayList<Edge> adjEdgeList = gp.n.getAdjEdgeList();
            for (Edge e : adjEdgeList)
            {
                if(e.getNodeID1() == gp.parent.n.getNodeID() || e.getNodeID2() == gp.parent.n.getNodeID())
                {
                    e.setState(alvis301.State.path);   
                    updateEdge(e);
                    break;
                }               
            }
            gp = gp.parent;
        }        
    }
    
    public ArrayList<NodePair> removeDuplicates(ArrayList<NodePair> temp)
    {        
        ArrayList<NodePair> distinctTemp = new ArrayList();   
        boolean distinct = true;
        
        for(NodePair np1:temp)
        {
            distinct = true;
            for(NodePair np2: distinctTemp)
                if ( np1.n.getNodeID() == np2.n.getNodeID())
                {
                    distinct = false;
                    break;                                           
                }
            if(distinct)
            distinctTemp.add(np1);
         }        
        return distinctTemp;             
    }

};
