/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alvis301;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author kevin
 */

public class BeamSearchforBSS extends Algorithm 
{
    int beamWidth;

    PriorityQueue<NodePair> openList = new PriorityQueue<NodePair>(1000, new Comparator<NodePair>() { 
    public int compare(NodePair np1, NodePair np2)
    {
        Double a = np1.val;
        Double b = np2.val;
        return a.compareTo(b);
    }});

    ArrayList<NodePair> closedList = new ArrayList<NodePair>();
    
    PriorityQueue<NodePair> tempList = new PriorityQueue<NodePair>(1000, new Comparator<NodePair>() { 
    public int compare(NodePair np1, NodePair np2)
    {
        Double a = np1.val;
        Double b = np2.val;
        return a.compareTo(b);
    }});
    
    public BeamSearchforBSS(int t)
    {
        super(t);
    }
    
    @Override
    public boolean goalTest(Node goalNode)
    {              
        int nodeID = goalNode.getNodeID();
        return (nodeID == g.getGoalID());
    }

    @Override
    public ArrayList<Node> moveGen(Node parentNode)
    {        
        ArrayList<Node> children = parentNode.getAdjList();      
        return children;
    }

    @Override
    public void run()
    {
        beamWidth  = Integer.valueOf(getInput("Enter the Beam Width: "));
                                
        NodePair sp = new NodePair();
        sp.n = g.getNode(g.getStartID());
        sp.val = 0;
        sp.parent = null;
        openList.add(sp);
            
        displayMessage("Start: " + g.getStartID());
        displayMessage("Goal: " + g.getGoalID());

        while(!openList.isEmpty())
        {
            //generate children and add them to priority queue tempList
            for(int i=0;i<beamWidth;i++)
            {
                if(openList.isEmpty())
                    continue;

                NodePair currentNodePair = openList.poll();
                
                if(currentNodePair.parent != null)
                    currentNodePair.val = actualCost(currentNodePair.parent, currentNodePair.n);
                currentNodePair.n.setState(alvis301.State.closed);
                updateNode(currentNodePair.n);                
                closedList.add(currentNodePair);

                if(goalTest(currentNodePair.n))
                {
                    goalFound(currentNodePair);
                    reconstructPath(currentNodePair);
                    display();
 
                    return; //Exit Point 1
                }                   
                
                ArrayList<Node> children = moveGen(currentNodePair.n);                
                
                for (Node child: children)
                {                  
                    //If node is part of open or closed lists, then not added to open
                    if(child.getState()==alvis301.State.open || child.getState()==alvis301.State.closed)
                        continue;
                     
                   display();
                   
                   NodePair np = new NodePair();
                   np.n = child;
                   if(currentNodePair.parent != null)
                    np.val = actualCost(sp, child) + heuristicCost(child, g.getNode(g.getStartID()));
                   else                    
                    np.val = actualCost(currentNodePair, child) + heuristicCost(child, g.getNode(g.getStartID()));
                   np.parent = currentNodePair;

                   child.setState(alvis301.State.open);
                   updateNode(child);
                   
                   tempList.add(np);
                }
            }
            //only beam width number of nodes are moved to openList  
            openList = removeNodesFromOpen();  
        }
        displayMessage("Goal node not found"); //Exit Point 2
    }
    
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
    
    public PriorityQueue removeNodesFromOpen()
    {                
        PriorityQueue<NodePair> pq = new PriorityQueue<NodePair>(1000, new Comparator<NodePair>() { 
        public int compare(NodePair np1, NodePair np2)
        {
            Double a = np1.val;
            Double b = np2.val;
            return a.compareTo(b);
        }});

        for(int i=0;i<beamWidth;i++)
        {
            if(tempList.isEmpty())
                    break;            
            
            NodePair np = tempList.poll();
            pq.add(np);
        }
        tempList.clear();
        return pq;
    }
    
    public void goalFound(NodePair currentNode)
    {
        displayMessage("YOYO , Cost: " + currentNode.val);

        currentNode.n.setState(alvis301.State.goal);
        updateNode(currentNode.n);                                   

        g.getNode(g.getStartID()).setState(alvis301.State.start);
        updateNode(g.getNode(g.getStartID()));
    }
                
    public double actualCost(NodePair parentpair, Node ctNode) //TODO
    {
        double cost;
        ArrayList<Edge> adjEdgeList = ctNode.getAdjEdgeList();
        for (Edge e : adjEdgeList)
        {
            cost = parentpair.val + e.getCost();
            if(e.getNodeID1() == parentpair.n.getNodeID() || e.getNodeID2() == parentpair.n.getNodeID())
                return cost;
        }
        return 1;
    }
        
    public double heuristicCost(Node ctNode, Node goal)
    {
        double x1 = ctNode.getX();        double x2 = goal.getX();
        double y1 = ctNode.getY();        double y2 = goal.getY();
        
	double  xDiff = x1-x2;
        double  xSqr  = Math.pow(xDiff, 2);

	double yDiff = y1-y2;
	double ySqr = Math.pow(yDiff, 2);

	double output   = Math.sqrt(xSqr + ySqr);
	return output;
    }
   
};
