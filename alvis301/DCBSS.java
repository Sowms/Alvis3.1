
package alvis301;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
/*
 * @Author : Tapan Chugh
 * @Author : Pankaj Yadav
 * This is the main class to simulate the Divide and Conquer Beam Stack Search
 */
public class DCBSS extends Algorithm {

    int b = 5; //Beam Width can be changed here
    int x =0;
    int m = 0;
    public DCBSS(int type) {
        super(type);
    }
    /*
     * Simple goal test functions, the second one is generally used though
     */
    @Override
    public boolean goalTest(Node goalNode) {
        
        int nodeID = goalNode.getNodeID();
        return (nodeID == g.getGoalID());
    }
    
    public boolean goalTest(Node node,Node goal) {
        
        return (node.getNodeID()==goal.getNodeID());
    }

    /*
     * A simple move gen function, used by beam search
     * */
    @Override
    public ArrayList<ArrayList<Node>> moveGen(Node parentNode) {
        
        ArrayList<Node> adjList = parentNode.getAdjList();
        ArrayList<ArrayList<Node>> children = new ArrayList<ArrayList<Node>>();
        for (Node n : adjList) {
            ArrayList<Node> nodePair  = new ArrayList<Node>();
            nodePair.add(n);
            nodePair.add(parentNode);
            children.add(nodePair);
        }
        return children;
    }
    /*
     * Move gen function used while marking the boundry
     */
    public ArrayList<Node> moveGenA_star(Node n,Node goal,float U){
    	ArrayList<Node> returnList=new ArrayList<Node>();
    	ArrayList<Node> adjList =n.getAdjList();
    	for(Node i: adjList){
    		Edge e=null;
    		for(Edge f : i.getAdjEdgeList())
    		{
    			if(f.getNodeID1()==n.getNodeID() || f.getNodeID2()==n.getNodeID())
    				e=f;
    		}
            double g1 =((DCBSSNodeData)n.getData()).gVal+e.getCost();
            if(i.getData()==null)
            {
                i.setData(new DCBSSNodeData());
                ((DCBSSNodeData)i.getData()).gVal=g1;
            }
            else if(((DCBSSNodeData)i.getData()).gVal<0)
            {
            	((DCBSSNodeData)i.getData()).gVal=g1;
            }
            else if(g1<((DCBSSNodeData)i.getData()).gVal)
            {
            	((DCBSSNodeData)i.getData()).gVal=g1;
            }
            ((DCBSSNodeData)i.getData()).hVal=distance(i,goal);
            ((DCBSSNodeData)i.getData()).fVal = ((DCBSSNodeData)i.getData()).gVal+((DCBSSNodeData)i.getData()).hVal;

    		if(((DCBSSNodeData)i.getData()).fVal<=U)
    		{
    			returnList.add(i);
    			changeState(i,alvis301.State.old);
    			updateNode(i);
    			display();
    		}
    	} 
    	return returnList;
    }
    /*
     * Marking nodes in the boundary until the found upper bound
     */
    public void boundryFunction(Node start,Node goal,float U){
    	ArrayList<Node> openList = new ArrayList<Node>();
    	ArrayList<Node> closeList = new ArrayList<Node>();
    	openList.add(start);
        start.setData(new DCBSSNodeData());
        ((DCBSSNodeData)start.getData()).gVal=0;
        ((DCBSSNodeData)start.getData()).hVal=distance(start,goal);
        displayMessage("Starting marking");
    	while(!openList.isEmpty()){
    		//sort open with its f value
    		Node x=openList.get(0);
    		ArrayList<Node> y = moveGenA_star(x,goal,U);

    		for(Node i : y){
    			if(!openList.contains(i) && !closeList.contains(i)){
    				openList.add(i);
    			}
    		}
    		closeList.add(x);
            HashSet<Node> set = new HashSet<Node>();
            set.addAll(openList);
            openList.clear();
            openList.addAll(set);
            Collections.sort(openList,new FComparator());
			openList.remove(x);
	//		displayMessage("Going on "+openList);
			

    	}
    	displayMessage("Done marking");
    }
    //*******************************************************************

    /*
     * A wrapper function to change state. Makes sure not to change the state of start or goal
     */
    public void changeState(Node n,alvis301.State s)
    {
    	if(n.getState()==alvis301.State.start || n.getState()==alvis301.State.goal)
    	{
    		return;
    	}
    	n.setState(s);
    }

    /*
     * Function to update the changes in the path as and when found.
     */
    public void updatePath(ArrayList<Node> f)
    {
        for(Node n : f)
        {
        	//changeState(n,alvis301.State.);
        	updateNode(n);
        //	display();
        	for(Edge e : n.getAdjEdgeList())
        	{
        		Node m;
        		if(e.getNodeID1()==n.getNodeID())
        			m=g.getNode(e.getNodeID2());
        		else
        			m=g.getNode(e.getNodeID1());
        		
        		if(f.contains(m)) //Adjacent nodes in the path
        		{
        			e.setState(alvis301.State.relay);
        			updateEdge(e);
        	//		display();
        		}
        		
        	}
        }
    }
    
    @Override
    public void run() {
        ArrayList<Node> f = dcbss(g.getNode(g.getStartID()),g.getNode(g.getGoalID()),true);
        /*
         * The code below shows the final path as found by the algorithm
         */
        for(Node n : f)
        {
        	changeState(n,alvis301.State.path);
        	updateNode(n);
        //	display();
        	for(Edge e : n.getAdjEdgeList())
        	{
        		Node m;
        		if(e.getNodeID1()==n.getNodeID())
        			m=g.getNode(e.getNodeID2());
        		else
        			m=g.getNode(e.getNodeID1());
        		
        		if(f.contains(m))
        		{
        			e.setState(alvis301.State.path);
        			updateEdge(e);
        	//		display();
        		}
        		
        	}
        }
        Node n = g.getNode(g.getStartID());
        Node m = g.getNode(g.getGoalID());
        changeState(n,alvis301.State.start);
        changeState(m,alvis301.State.goal);
        updateNode(n);
        updateNode(m);
        display();
    }

    /*
     * Function to remove seen nodes from a list of nodes. Used by beam search
     */
    private ArrayList<ArrayList<Node>> removeSeen(ArrayList<ArrayList<Node>> nodes,ArrayList<ArrayList<Node>> open,ArrayList<ArrayList<Node>> closed) {
        
        if(nodes.isEmpty())
            return nodes;
        Node n = nodes.get(0).get(0);
        if (OccursIn(n,open) || OccursIn(n,closed)) {
            ArrayList<ArrayList<Node>> newList = new ArrayList<ArrayList<Node>>();
            newList.addAll(nodes);
            newList.remove(nodes.get(0));
            return removeSeen(newList,open,closed);
        }
        else {
            ArrayList<ArrayList<Node>> newList = new ArrayList<ArrayList<Node>>();
            ArrayList<ArrayList<Node>> tailList = new ArrayList<ArrayList<Node>>();
            tailList.addAll(nodes);
            tailList.remove(nodes.get(0));
            newList.add(nodes.get(0));
            newList.addAll(removeSeen(tailList,open,closed));
            return newList;
        }
    }

    /*
     * Function to check if a node is present in another list. Used by beamSearch
     */
    private boolean OccursIn(Node n, ArrayList<ArrayList<Node>> nodeList) {
    
        if(nodeList.isEmpty())
            return false;
        if (n.equals(nodeList.get(0).get(0)))
            return true;
        ArrayList<ArrayList<Node>> newList;
        newList = new ArrayList<ArrayList<Node>>();
        newList.addAll(nodeList);
        newList.remove(nodeList.get(0));
        return OccursIn(n,newList);
    }

    /*
     * Function to calculate eucledian distance b/w two nodes 
     */
    public double distance(Node n1, Node n2) {
        return Math.sqrt(Math.pow((n1.getX()-n2.getX()),2)+Math.pow((n1.getY()-n2.getY()),2));
    }

    /*
     * Function to reconstruct path. Used by beamSearch. BoundData is the bounds that we get from beamSearch
     * BoundData -
     * 	1) Upper bound on cost
     * 	2) No of nodes in the path. This is used as approximately half of this is set as relay node layer
     */
    private BoundData ReconstructPath(ArrayList<Node> nodePair,ArrayList<ArrayList<Node>> closed,boolean show) {
       
        double U = 0;
        int n =0;
        m=0;
        Node node = nodePair.get(0);
        /*if(show)
        	node.setState(alvis301.State.path);*/
        updateNode(node);
        Node parent = nodePair.get(1);
        Node child = node;
        while(parent!=null) {
            /*if(show)
                parent.setState(alvis301.State.path);*/
            ArrayList<Edge> adjEdgeList = parent.getAdjEdgeList();
            for (Edge e : adjEdgeList) {
                int node1 = e.getNodeID1();
                int node2 = e.getNodeID2();
                if (node1==child.getNodeID() || node2==child.getNodeID()) {
                    if(show)
                        //e.setState(alvis301.State.path);
                    updateEdge(e);
                    n++;
                    U+=e.getCost();
                    break;
                }
            }
            updateNode(parent);
            ArrayList<Node> nodePair1=findLink(parent,closed);
            child = parent; 
            parent=nodePair1.get(1); 
        }
        BoundData d = new BoundData();
        d.N=n;
        d.U=U;
        return d;
    }
    /*
     * Used to find the link node from the closed list. Used for reconstructing the path in beam search
     */
    private ArrayList<Node> findLink(Node parent, ArrayList<ArrayList<Node>> nodes)
    {
         if(parent.equals(nodes.get(0).get(0)))
            return nodes.get(0);
         else  
         { 
             
            ArrayList<ArrayList<Node>> temp=new ArrayList<ArrayList<Node>>();
            temp.addAll(nodes);
            temp.remove(nodes.get(0));
            return findLink(parent,temp);
         }  
    }
    
    /*
     * Do a beam search from start node to goal node, to determine an upper bound on the cost and the number fo nodes
     * show - to show the progress of beamSearch or not 
     * avoid keeping show disabled. Feels like program has hanged. No output for long time
     * init - tells whether this is the initial run of the program of this function or not
     * A major part of this code, and all other functions for beamSearch are copied from the code for the BFS.
     */
    private BoundData beamSearch(Node start,Node goal,boolean show,boolean init)
    {
        ArrayList<ArrayList<Node>> open;
        ArrayList<ArrayList<Node>> closed;
        BoundData U = new BoundData();
        ArrayList<Node> first;
        first = new ArrayList<Node>();
        first.add(start);
        first.add(null);
        open = new ArrayList<ArrayList<Node>>();
        closed = new ArrayList<ArrayList<Node>>();
        open.add(first);
        ArrayList<ArrayList<Node>> newOpen = new ArrayList<ArrayList<Node>>();
        boolean done=false;
        while (!(open.isEmpty() && newOpen.isEmpty())) 
        {
        	//Generate the new open list
            newOpen = new ArrayList<ArrayList<Node>>();
            for(ArrayList<Node> np: open)
            {
            	
                Node node = np.get(0);
                boolean b = goalTest(node,goal);
                if (b) {
                    U = ReconstructPath(np,closed,show);    
                    display();
                    if(init)
                    {
                    	displayMessage("Upper bound cost = "+U.U);
                    }
                    done=true;
                    break; 
                }
                if(done)
                    break;
                if(show)
                	if(!(node.getState()==alvis301.State.relay) || (node.getState()==alvis301.State.path))
                		changeState(node,alvis301.State.closed);
                Node aNode = np.get(1);
                if (aNode!=null) {
                    ArrayList<Edge> adjEdgeList = node.getAdjEdgeList();
                    for (Edge e : adjEdgeList) {
                        Integer nodeID1 = e.getNodeID1();
                        Integer nodeID2 = e.getNodeID2();
                        if((nodeID1.equals(aNode.getNodeID())) || (nodeID2.equals(aNode.getNodeID()))) {
                            if(show)
                            //    e.setState(alvis301.State.closed);
                            updateEdge(e);
                            break;
                        }
                    }
                    updateNode(node);
            
                }   
               
                ArrayList<ArrayList<Node>> children = moveGen(node);
                newOpen.addAll(children);
            }
            closed.addAll(open);
            open = new ArrayList<ArrayList<Node>>();
            display();

            ArrayList<ArrayList<Node>> noLoops = removeSeen(newOpen,open,closed);
            
            Collections.sort(noLoops,new HComparator(goal));
            for(int i =0,j=0;j<b && i<noLoops.size();i++)
            {
                ArrayList<Node> x= noLoops.get(i);
                if(!open.contains(x))
                {
                    open.add(noLoops.get(i));
                    j++;
                }
            
            }
                
            for (ArrayList<Node> open1 : open) {
                Node nParent = open1.get(1);
                if (nParent!=null) {
                    ArrayList<Edge> adjEdgeList = nParent.getAdjEdgeList();
                    for (Edge e : adjEdgeList) {
                        int nodeID1 = e.getNodeID1();
                        int nodeID2 = e.getNodeID2();
                        if ((nodeID1 == open1.get(0).getNodeID()) || nodeID2 == open1.get(0).getNodeID()) {
                            if(show)
                            	//e.setState(alvis301.State.open);
                            updateEdge(e);
                            Node n = open1.get(0);
                            if(show)
                            	if(!(n.getState()==alvis301.State.relay) || (n.getState()==alvis301.State.path))
                            		changeState(n,alvis301.State.open);
                            updateNode(n);
                            display();

                            break;
                        }
                    }
                }
            }
        }
        if(U.U == 0)
        {
            displayMessage("Unable to find upper bound");   
            return U;
        }
        return U;
    }
    /*
     * This function is called when the size of an open list increases beyond the beam width. 
     * It prunes the list to keep only the specified no of nodes in the open list and sets
     * the value at the top of the beam stack accordingly.
     */
    private void pruneLayer(int l, Stack<ArrayList<Double>> beamStack,ArrayList<ArrayList<Node>> open)
    {
    	x++;
    	System.out.println("pruning "+l);
        ArrayList<Node> keep = new ArrayList<Node>();
        ArrayList<Node> orig = open.get(l);
        ArrayList<Node> prune = new ArrayList<Node>();
        //sort orig by f
        Collections.sort(orig,new FComparator());
        for(int i=0,j=0;j<b && i<orig.size();i++)
        {
            Node x = orig.get(i);
            if(!keep.contains(x))
            {
                keep.add(x);
                j++;
            }
        }
        
        prune.addAll(orig);
        prune.removeAll(keep);
        Collections.sort(prune,new FComparator());
        ArrayList<Double> t = beamStack.peek();
        //Update upper bound on the top of the stack
        double v = ((DCBSSNodeData)prune.get(0).getData()).fVal;
        t.set(1,v);
        System.out.println("At v:"+v);
        for(Node n : prune)
        {
            open.get(l).remove(n);
        }
    }
    /*
     * Move Gen used by the main DCBSS algorithm. Looks at the g and h value and sets up the f value
     * Only picks up the nodes in the correct range as decided by the top of the beamstack
     */
    
    private ArrayList<Node> moveGen1(Node n,ArrayList<Double> c,Node goal)
    {
        double l= c.get(0);
        double u= c.get(1);
        ArrayList<Node> nds = n.getAdjList();
        ArrayList<Node> p = new ArrayList<Node>();
        for(Edge e :n.getAdjEdgeList())
        {
            Node n1 = g.getNode(e.getNodeID1());
            Node n2 = g.getNode(e.getNodeID2());
            Node m;
            if(n1.getNodeID()==n.getNodeID())
                m=n2;
            else if(n2.getNodeID()==n.getNodeID())
                m=n1;
            else
                continue;
            double g2 =((DCBSSNodeData)n.getData()).gVal+distance(n,m);
            double g1 =((DCBSSNodeData)n.getData()).gVal+e.getCost();
            System.out.println("g1: "+g1+" g2: "+g2);
            if(m.getData()==null)
            {
                m.setData(new DCBSSNodeData());
                ((DCBSSNodeData)m.getData()).gVal=g1;
                p.add(m);
            }
            else if(((DCBSSNodeData)m.getData()).gVal<0)
            {
            	((DCBSSNodeData)m.getData()).gVal=g1;
            	p.add(m);
            }
            else if(g1<((DCBSSNodeData)m.getData()).gVal)
            {
            	((DCBSSNodeData)m.getData()).gVal=g1;
            	p.add(m);
            }
            ((DCBSSNodeData)m.getData()).hVal=distance(m,goal);
            ((DCBSSNodeData)m.getData()).fVal = ((DCBSSNodeData)m.getData()).gVal+((DCBSSNodeData)m.getData()).hVal;
            
        }
        ArrayList<Node> finl = new ArrayList<Node>();
        for(Node nd : p)
        {
            
            double fv = ((DCBSSNodeData)nd.getData()).fVal;
          //  System.out.println(nd.getNodeID()+ " "+fv);
            if(fv>=l && fv<u)
                finl.add(nd);
        }
        HashSet<Node> set = new HashSet<Node>();
        set.addAll(finl);
        finl.clear();
        finl.addAll(set);
        Collections.sort(finl,new FComparator());
        return finl;
    }

    /*
     * This is the main search routine of the algorithm. Tries to find a path from start
     * to goal with U as the upperbound.
     * relay - the number for the relay layer. Uses the bounds computed by  the beamSearch
     * beamStack - the actual beamStack for the program
     */
    private Node search(Node start,Node goal,double U,int relay,Stack<ArrayList<Double>> beamStack)
    {
        start.setData(new DCBSSNodeData());
        ((DCBSSNodeData)start.getData()).gVal=0;
        ((DCBSSNodeData)start.getData()).hVal=distance(start,goal);
        ArrayList<ArrayList<Node>> open;
        ArrayList<ArrayList<Node>> closed;
        open = new ArrayList<ArrayList<Node>>();
        closed = new ArrayList<ArrayList<Node>>();
        open.add(0,new ArrayList<Node>());
        open.get(0).add(start);
        open.add(1,new ArrayList<Node>());
        closed.add(0,new ArrayList<Node>());
        Node best_goal = null;
        int l = 0;
       // displayMessage("Starting to go");
        while(!open.get(l).isEmpty() || !open.get(l).isEmpty())
        {
            while(!open.get(l).isEmpty())
            {
                Node n = open.get(l).get(0);
                if(!(n.getState()==alvis301.State.relay) || (n.getState()==alvis301.State.path))
                {
                	changeState(n,alvis301.State.closed);
                	updateNode(n);
                	display();
                }
                open.get(l).remove(0);
                closed.get(l).add(n);
                //Check if we found a goal
                if(goalTest(n,goal))
                {
                    U  = ((DCBSSNodeData)n.getData()).gVal;
                    best_goal = n;
                  //  displayMessage("Found node "+n.getData().gVal);
                }
                ArrayList<Node> nodes = moveGen1(n,beamStack.peek(),goal);
                for(int i = 0;i<=l;i++)
                	nodes.removeAll(closed.get(i));
                if(l>1 && l<relay+1)
                {
                	for(Node no : nodes)
                		((DCBSSNodeData)no.getData()).prev=start;
                }
                else if(l==relay+1)
                {
                	for(Node no : nodes)
                		((DCBSSNodeData)no.getData()).prev=n;
                }
                else if(l>relay+1)
                {
                	for(Node no : nodes)
                		((DCBSSNodeData)no.getData()).prev=((DCBSSNodeData)n.getData()).prev;
                }
                open.get(l+1).addAll(nodes);
                HashSet<Node> set = new HashSet<Node>();
                set.addAll(open.get(l+1));
                open.get(l+1).clear();
                open.get(l+1).addAll(set);
                Collections.sort(open.get(l+1),new FComparator());
                if(open.get(l+1).size()>b)
                {
                    pruneLayer(l+1,beamStack,open);
                }
            }
            for(Node n : open.get(l+1))
            {
            	if(!(n.getState()==alvis301.State.relay) || (n.getState()==alvis301.State.path))
                {
                	changeState(n,alvis301.State.open);
            		updateNode(n);
                }
            }
            display();
            if((l>1 && l<=relay+1) || l>(relay+2))
            {
                for(Node n : closed.get(l-1))
                {
                	if(!(n.getState()==alvis301.State.relay) || (n.getState()==alvis301.State.path))
                    {
                		changeState(n,alvis301.State.old);
                		updateNode(n);
                    }
                }
                closed.get(l-1).clear();
                display();
            }
            else if(l>1)
            {
                for(Node n : closed.get(l-1))
                {
                	if(!(n.getState()==alvis301.State.relay) || (n.getState()==alvis301.State.path))
                    {
                //		n.setState(alvis301.State.relay);
                		updateNode(n);
                    }
                }
                display();
            }
            l=l+1;
            open.add(l+1,new ArrayList<Node>());
            closed.add(l,new ArrayList<Node>());
            ArrayList<Double> c = new ArrayList<Double>();
            c.add(0,0D);
            c.add(1,U);
            beamStack.push(c);
        }
        return best_goal;
    }
    /*
     * The main function of the DCBSS algorithm. It calls search repeatedly with different upper bounds
     * to find the optimal path.
     * It then calls itself recursively to reconstrcut the best path found.
     * init - is this the initial run of the function. Displays some messages and marks nodes within
     * the upper bound in that case
     */
    private ArrayList<Node> dcbss(Node first,Node goal,boolean init)
    {
    	//some Book keeping
        ArrayList<Node> out = new ArrayList<Node>();
    	out.add(first);
        if(first.getNodeID()==goal.getNodeID())
        {
        	changeState(first,alvis301.State.relay);
        	updateNode(first);
        	display();
        	updatePath(out);
        	return out;
        }
        BoundData bound = beamSearch(first,goal,true,init);
        double U = bound.U+1; // Upper bound found by the beam search
        int relay = (bound.N-1)/2;
    	//double U = 2500;
        HashMap<Integer,Node> nds = g.getNodes();
        for (Map.Entry pairs : nds.entrySet()) {
            Node n = (Node) pairs.getValue();
            n.setData(new DCBSSNodeData());
            if(n.getState()==alvis301.State.open|| n.getState()==alvis301.State.closed)
            {
            	changeState(n,alvis301.State.unvisited);
            }
            updateNode(n);
        }
        if(init)
        {
        	boundryFunction(first,goal,(float)U); //Marking function
            HashMap<Integer,Node> nds1 = g.getNodes();
            for (Map.Entry pairs : nds1.entrySet()) {
                Node n = (Node) pairs.getValue();
                n.setData(new DCBSSNodeData());
                if(n.getState()==alvis301.State.open|| n.getState()==alvis301.State.closed)
                {
                	changeState(n,alvis301.State.unvisited);
                }
                updateNode(n);
            }
        }
        g.setNodes(nodes);
        Graph.setInstance(g);
        display();
        Stack<ArrayList<Double>> beamStack = new Stack<ArrayList<Double>>();
        ArrayList<Double> c = new ArrayList<Double>();
        c.add(0,0D);
        c.add(1,U);
        //The main routine starts here
        beamStack.push(c);
        Node best = null;
        while(!beamStack.isEmpty())
        {
            Node n = search(first,goal,U,relay,beamStack);
            if(n!=null)
            {
                best = n;
                U = ((DCBSSNodeData)best.getData()).gVal; //Updating U if a better path is found
            }
            while(!beamStack.isEmpty()) //Pop parts from the stack where cannot find better path
            {
                ArrayList<Double> l = beamStack.peek();
                if(l.get(1) <U)
                    break;
                beamStack.pop();
            }
            if(beamStack.isEmpty())
            {
            	if(init)
            		displayMessage("Found best path : "+((DCBSSNodeData)best.getData()).gVal);
                System.out.println(first + " " +((DCBSSNodeData)best.getData()).prev);
                Node anchor = ((DCBSSNodeData)best.getData()).prev; //The relay node
                if(anchor!=null)
                {
                	changeState(anchor,alvis301.State.relay);
                	updateNode(best);
                	display();
                	ArrayList<Node> l1 = dcbss(first,anchor,false); //recursive calls
                	ArrayList<Node> l2 = dcbss(anchor,best,false);
                	out.addAll(l1);
                	out.addAll(l2);
                }
                
                else
                {
                	changeState(best,alvis301.State.relay);
                	updateNode(best);
                	display();
                	out.add(best);
                }
                updatePath(out);
                return out;
            }
            ArrayList<Double> l = beamStack.peek(); //Update the bounds of beam Stack
            l.set(0,l.get(1));
            l.set(1, U);
            beamStack.pop();
            beamStack.push(l);
        }
        updatePath(out); //draw the path
		return out;
    }
}
