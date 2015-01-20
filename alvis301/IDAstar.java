package alvis301;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
/**
*
* @author Melchior Thambipillai
*/
public class IDAstar extends Algorithm{
	
	public LinkedList<ArrayList<Node>> open;
    public LinkedList<ArrayList<Node>> closed;
	private double cutoff;
	private double nextCutoff;
	private final static double LARGE = 999999999;

	public IDAstar(int t) {
		super(t);
		// TODO Auto-generated constructor stub
	}

	@Override
    public boolean goalTest(Node goalNode) {
        int nodeID = goalNode.getNodeID();
        return (nodeID == g.getGoalID());
    }

    @Override
    public LinkedList<ArrayList<Node>> moveGen(Node parentNode) {
        
        ArrayList<Node> adjList = parentNode.getAdjList();
        LinkedList<ArrayList<Node>> children = new LinkedList<ArrayList<Node>>();
        for (Node n : adjList) {
        	ArrayList<Node> nodePair  = new ArrayList<Node>();
            nodePair.add(n);
            nodePair.add(parentNode);
            children.add(nodePair);
        }
        return children;
    }
    static String ans = "";
public String getInput(final String message) {
    	
        try {
            Runnable showPanelRun = new Runnable() {
                @Override
                public void run() {
                    ans = JOptionPane.showInputDialog(null,message,"",1);
                }
            };
            SwingUtilities.invokeAndWait(showPanelRun);
            } catch ( InterruptedException ix ) {
                System.out.println("main interrupted while waiting on invokeAndWait()");
            } catch ( InvocationTargetException x ) {
                System.out.println("main exception thrown from run()");
            }
        return ans;
    }

	@Override
	public void run() {
		String answer = getInput("Enter delta for cutoff incrementation, type \"optimal\" for no fixed incrementation");
		int delta;
		if(answer.equals("optimal")){
			//in this case the cutoff will be extended to the next lowest non-expanded f-Value
			delta = -1;
		}else{
			delta = Integer.parseInt(answer);
		}
		
		int totalNodes = g.getNoNodes();
		boolean newNodes = true;
		int count;
		cutoff = f(g.getNode(g.getStartID()));
		double fValue;
		while(newNodes==true){
			//start DFS iteration :
			nextCutoff = LARGE;
			count = 1;
			ArrayList<Node> first;
	        first = new ArrayList<Node>();
	        first.add(g.getNode(g.getStartID()));
	        first.add(null);
	        open = new LinkedList<ArrayList<Node>>();
	        closed = new LinkedList<ArrayList<Node>>();
	        open.add(first);
	        //start exploring open list :
	        while (!open.isEmpty()) {
	            ArrayList <Node> nodePair = open.remove(0);
	            Node node = nodePair.get(0);
	            if (goalTest(node)) {
	                ReconstructPath(nodePair);
	                try {
						display();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                return;
	            }
	            
	            closed.add(nodePair);
	            node.setState(alvis301.State.closed);
	            Node aNode = nodePair.get(1);
	            if (aNode!=null) {
	                ArrayList<Edge> adjEdgeList = node.getAdjEdgeList();
	                for (Edge e : adjEdgeList) {
	                    Integer nodeID1 = e.getNodeID1();
	                    Integer nodeID2 = e.getNodeID2();
	                    if((nodeID1.equals(aNode.getNodeID())) || (nodeID2.equals(aNode.getNodeID()))) {
	                        e.setState(alvis301.State.closed);
	                        updateEdge(e);
	                        break;
	                    }
	                }
	            }
	            updateNode(node);
	            try {
					display();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            fValue = f(node);
	            //only expand nodes with f-Values within cutoff
	            if(fValue<=cutoff){
	            	LinkedList<ArrayList<Node>> children = moveGen(node);
		            LinkedList<ArrayList<Node>> noLoops = removeSeen(children);
		            open.addAll(0,noLoops);
		            count+=noLoops.size();
		            for (ArrayList<Node> open1 : open) {
		                Node nParent = open1.get(1);
		                if (nParent!=null) {
		                    ArrayList<Edge> adjEdgeList = nParent.getAdjEdgeList();
		                    for (Edge e : adjEdgeList) {
		                        int nodeID1 = e.getNodeID1();
		                        int nodeID2 = e.getNodeID2();
		                        if ((nodeID1 == open1.get(0).getNodeID()) || nodeID2 == open1.get(0).getNodeID()) {
		                            e.setState(alvis301.State.open);
		                            updateEdge(e);
		                            Node n = open1.get(0);
		                            n.setState(alvis301.State.open);
		                            updateNode(n);
		                            break;
		                        }
		                    }
		                }
		            }
	            }else{
	            	//nextCutoff is the min of the fValues
	            	if(fValue < nextCutoff){
	            		nextCutoff = fValue;
	            	}
	            }
	        }
	        if(delta==-1){
	        	cutoff = nextCutoff;
	        }else{
	        	cutoff +=delta;
	        }
		for (ArrayList<Node> visited1 : closed) {
                Node nParent = visited1.get(1);
                if (nParent!=null) {
                    ArrayList<Edge> adjEdgeList = nParent.getAdjEdgeList();
                    for (Edge edge : adjEdgeList) {
                        int nodeID1 = edge.getNodeID1();
                        int nodeID2 = edge.getNodeID2();
                        if ((nodeID1 == visited1.get(0).getNodeID()) || nodeID2 == visited1.get(0).getNodeID()) {
                            edge.setState(alvis301.State.old);
                            updateEdge(edge);
                            Node n = visited1.get(0);
                            n.setState(alvis301.State.old);
                            updateNode(n);
                            break;
                        }
                    }
                }
            }
	        //if we explored all nodes in the graph we stop IDAstar
	        if(count==totalNodes){
				newNodes = false;
			}
		}
	}
	
	//f value is the sum of the euclidean distances (start->node) and (node->goal)
	private double f(Node n){
		return g(n)+h(n);
	}
	
	private double g(Node n){
		Node start = g.getNode(g.getStartID());
		return Math.sqrt(Math.pow(n.getX()-start.getX(),2)+Math.pow(n.getY()-start.getY(),2));
	}
	private double h(Node n){
		Node goal = g.getNode(g.getGoalID());
		return Math.sqrt(Math.pow(goal.getX()-n.getX(),2)+Math.pow(goal.getY()-n.getY(),2));
	}
	
private LinkedList<ArrayList<Node>> removeSeen(LinkedList<ArrayList<Node>> nodes) {
        
        if(nodes.isEmpty())
            return nodes;
        Node n = nodes.get(0).get(0);
        if (OccursIn(n,open) || OccursIn(n,closed)) {
            LinkedList<ArrayList<Node>> newList = new LinkedList<ArrayList<Node>>();
            newList.addAll(nodes);
            newList.remove(nodes.get(0));
            return removeSeen(newList);
        }
        else {
            LinkedList<ArrayList<Node>> newList = new LinkedList<ArrayList<Node>>();
            LinkedList<ArrayList<Node>> tailList = new LinkedList<ArrayList<Node>>();
            tailList.addAll(nodes);
            tailList.remove(nodes.get(0));
            newList.add(nodes.get(0));
            newList.addAll(removeSeen(tailList));
            return newList;
        }
    }

    private boolean OccursIn(Node n, LinkedList<ArrayList<Node>> nodeList) {
    
        if(nodeList.isEmpty())
            return false;
        if (n.equals(nodeList.get(0).get(0)))
            return true;
        LinkedList<ArrayList<Node>> newList;
        newList = new LinkedList<ArrayList<Node>>();
        newList.addAll(nodeList);
        newList.remove(nodeList.get(0));
        return OccursIn(n,newList);
    }

    private void ReconstructPath(ArrayList<Node> nodePair) {
       
        Node node = nodePair.get(0);
        node.setState(alvis301.State.path);
        updateNode(node);
        Node parent = nodePair.get(1);
        Node child = node;
        while(parent!=null) {
            parent.setState(alvis301.State.path);
            ArrayList<Edge> adjEdgeList = parent.getAdjEdgeList();
            for (Edge e : adjEdgeList) {
                int node1 = e.getNodeID1();
                int node2 = e.getNodeID2();
                if (node1==child.getNodeID() || node2==child.getNodeID()) {
                    e.setState(alvis301.State.path);
                    updateEdge(e);
                    break;
                }
            }
            updateNode(parent);
            ArrayList<Node> nodePair1=findLink(parent,closed);
            child = parent; 
            parent=nodePair1.get(1); 
        }
    }
    private ArrayList<Node> findLink(Node parent, LinkedList<ArrayList<Node>> nodes)
    {
         if(parent.equals(nodes.get(0).get(0)))
            return nodes.get(0);
         else  
         { 
             
            LinkedList<ArrayList<Node>> temp=new LinkedList<ArrayList<Node>>();
            temp.addAll(nodes);
            temp.remove(nodes.get(0));
            return findLink(parent,temp);
         }  
    }

}
