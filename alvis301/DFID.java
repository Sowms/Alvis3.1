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

public class DFID extends Algorithm{
	
	private LinkedList<ArrayList<Node>> open;
	private LinkedList<ArrayList<Node>> closed;
	
	private class DFIDData implements NodeData{
		public int depth;
		public DFIDData(int depth_p){
			depth = depth_p;
		}
	}
	
	private void addAllFront(LinkedList<ArrayList<Node>> list, LinkedList<ArrayList<Node>> toAdd, int depth){
		
		for(ArrayList<Node> nodePair : toAdd){
			nodePair.get(0).setData(new DFIDData(depth));
		}
		list.addAll(0, toAdd);
	}

	public DFID(int t) {
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
		int depthBound =  1;
		String answer = getInput("Enter delta for depth incrementation");
		int delta = Integer.parseInt(answer);
		int previousCount = 0;
		boolean newNodes = true;
		int count;
		do{
			//start DFS iteration :
			count = 0;
			open = new LinkedList<ArrayList<Node>>();
			ArrayList<Node> firstNodePair = new ArrayList<Node>();
			Node start = g.getNode(g.getStartID());
			start.setData(new DFIDData(0));
			firstNodePair.add(start);
			firstNodePair.add(null);
			open.add(firstNodePair);
			closed = new LinkedList<ArrayList<Node>>();
			//start exploring the open list :
			while(!open.isEmpty()){
				ArrayList<Node> nodePair = open.remove(0);
				Node node = nodePair.get(0);
				if(goalTest(node)){
					ReconstructPath(nodePair);
					try {
						display();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return;
				}
				closed.add(nodePair);
	            node.setState(alvis301.State.closed);
	            Node aNode = nodePair.get(1);
	            if (aNode!=null) {
	                ArrayList<Edge> adjEdgeList = node.getAdjEdgeList();
	                for (Edge edge : adjEdgeList) {
	                    Integer nodeID1 = edge.getNodeID1();
	                    Integer nodeID2 = edge.getNodeID2();
	                    if((nodeID1.equals(aNode.getNodeID())) || (nodeID2.equals(aNode.getNodeID()))) {
	                        edge.setState(alvis301.State.closed);
	                        updateEdge(edge);
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
	            DFIDData data = (DFIDData) node.getData(); //node's depth is stored in its DFIDData field (extends NodeData)
	            if(data.depth<depthBound){
	            	LinkedList<ArrayList<Node>> children = moveGen(node);
	                LinkedList<ArrayList<Node>> noLoops = removeSeen(children);
	                addAllFront(open,noLoops,data.depth+1); //children have depth 'depth+1' compared to the parent
	                count+=noLoops.size();
	                for (ArrayList<Node> open1 : open) {
	                    Node nParent = open1.get(1);
	                    if (nParent!=null) {
	                        ArrayList<Edge> adjEdgeList = nParent.getAdjEdgeList();
	                        for (Edge edge : adjEdgeList) {
	                            int nodeID1 = edge.getNodeID1();
	                            int nodeID2 = edge.getNodeID2();
	                            if ((nodeID1 == open1.get(0).getNodeID()) || nodeID2 == open1.get(0).getNodeID()) {
	                                edge.setState(alvis301.State.open);
	                                updateEdge(edge);
	                                Node n = open1.get(0);
	                                n.setState(alvis301.State.open);
	                                updateNode(n);
	                                break;
	                            }
	                        }
	                    }
	                }
	            }
	            
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
			//if we didn't find any new nodes we stop DFID
			if(previousCount==count){
				newNodes = false;
			}
			previousCount = count;
			depthBound+=delta;
		}while(newNodes==true);
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
