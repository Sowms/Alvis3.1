package alvis301;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Stanley
 */
public class Minimax extends Algorithm
    {
    public Minimax(int t)
        {
        super(t);
        }
    
    public boolean goalTest(Node goalNode)
        {
        throw new UnsupportedOperationException("Not supported yet.");
        }
    
    public ArrayList<Node> moveGen(Node parentNode)
        {
        ArrayList<Node> adjList = parentNode.getAdjList();
        GameNodeData gdp = (GameNodeData) parentNode.getData();
        ArrayList<Node> children = new ArrayList<Node>();
        for(Node node : adjList)
            {
            GameNodeData gd = (GameNodeData) node.getData();
            if(gd.level > gdp.level)
                children.add(node);
            }
        return children;
        }
    
    public boolean testTerminal(Node n)
        {
        GameNodeData gd = (GameNodeData) n.getData();
        return gd.terminal;
        }

    public int returnMax(int x, int y)
        {
        if(x > y)
            return x;
        return y;
        }

    public int returnMin(int x, int y)
        {
        if(x < y)
            return x;
        return y;
        }

    public int getMinimax(Node j)
        {
        if(testTerminal(j))
            {
            GameNodeData gd = (GameNodeData) j.getData();
            return gd.value;
            }
        ArrayList<Node> children = moveGen(j);
        ArrayList<Edge> adjList = j.getAdjEdgeList();
        Iterator<Node> childIterator = children.iterator();
        Node child = childIterator.next();
        Edge modified = null;
        for(Edge e : adjList)
            if(child.equals(g.getNode(e.getNodeID1())) || child.equals(g.getNode(e.getNodeID2())))
                {
                e.setState(alvis301.State.boundary);
                updateEdge(e);
                modified = e;
                break;
                }
        display();
        if(modified != null)
            {
            modified.setState(j.getState());
            updateEdge(modified);
            }
        int minimaxValue = getMinimax(child);
        if(j.getState() == alvis301.State.max)
            while(childIterator.hasNext())
                {
                child = childIterator.next();
                modified = null;
                for(Edge e : adjList)
                    if(child.equals(g.getNode(e.getNodeID1())) || child.equals(g.getNode(e.getNodeID2())))
                        {
                        e.setState(alvis301.State.boundary);
                        updateEdge(e);
                        modified = e;
                        break;
                        }
                display();
                if(modified != null)
                    {
                    modified.setState(j.getState());
                    updateEdge(modified);
                    }
                minimaxValue = returnMax(minimaxValue, getMinimax(child));
                }
        else
            while(childIterator.hasNext())
                {
                child = childIterator.next();
                modified = null;
                for(Edge e : adjList)
                    if(child.equals(g.getNode(e.getNodeID1())) || child.equals(g.getNode(e.getNodeID2())))
                        {
                        e.setState(alvis301.State.boundary);
                        updateEdge(e);
                        modified = e;
                        break;
                        }
                display();
                if(modified != null)
                    {
                    modified.setState(j.getState());
                    updateEdge(modified);
                    }
                minimaxValue = returnMin(minimaxValue, getMinimax(child));
                }
        GameNodeData gd = (GameNodeData) j.getData();
        gd.value = minimaxValue;
        j.setData(gd);
        updateNode(j);
        return minimaxValue;
        }
    
    public void reset(Node root)
        {
        System.out.println("resetting graph");
        ArrayList<Node> queue = new ArrayList<Node>();
        queue.add(root);
        while(queue.isEmpty() == false)
            {
            Node node = queue.remove(0);
            ArrayList<Edge> adjList = node.getAdjEdgeList();
            for(Edge e : adjList)
                if(e.getState() == alvis301.State.deleted || e.getState() == alvis301.State.old)
                    {
                    e.setState(alvis301.State.unvisited);
                    updateEdge(e);
                    }
            queue.addAll(moveGen(node));
            }
        queue.add(root);
        while(queue.isEmpty() == false)
            {
            Node node = queue.remove(0);
            ArrayList<Edge> adjList = node.getAdjEdgeList();
            for(Edge e : adjList)
                if(e.getState() == alvis301.State.max || e.getState() == alvis301.State.min)
                    {
                    e.setState(alvis301.State.old);
                    updateEdge(e);
                    }
            queue.addAll(moveGen(node));
            }
        queue.addAll(moveGen(root));
        while(queue.isEmpty() == false)
            {
            Node node = queue.remove(0);
            if(testTerminal(node))
                continue;
            GameNodeData gd = (GameNodeData) node.getData();
            //GameNodeData gd = new GameNodeData();
            gd.value = 0;
            node.setData(gd);
            updateNode(node);
            queue.addAll(moveGen(node));
            }
        display();
        }
    
    public void run()
        {
        Node root = g.getNode(g.getStartID());
        reset(root);
        GameNodeData gd = (GameNodeData) root.getData();
        gd.value = getMinimax(root);
        root.setData(gd);
        updateNode(root);
        printPath(root);
        display();
        }
    
    public void printPath(Node root){
        Node cur=root;
        GameNodeData gd=(GameNodeData) cur.getData();
        while(!gd.terminal){
            ArrayList<Node> children=moveGen(cur);
            if(children.isEmpty())
                return;
            Node next = children.get(0);
            gd = (GameNodeData) next.getData();
            /*if (cur.getState() == alvis301.State.min) {
                        ArrayList<Edge> adjList = cur.getAdjEdgeList();
                        /*for (Edge e : adjList) {
                            if (children.contains(g.getNode(e.getNodeID1())) || children.contains(g.getNode(e.getNodeID2()))) {
                                e.setState(alvis301.State.path);
                                updateEdge(e);
                            }
                        }    
                    }*/
            int max = gd.value; Node maxNode = next; int min = gd.value; Node minNode=next;
            for(int i=1;i<children.size();i++){
                //if (cur.getState() == alvis301.State.max) {
                    gd = (GameNodeData) children.get(i).getData();
                    if (gd.value > max) {
                        max = gd.value;
                        maxNode = children.get(i);
                    }
                    if (gd.value < min) {
                        min = gd.value;
                        minNode = children.get(i);
                    }
            }
            if (cur.getState() == alvis301.State.max) {
                next = maxNode;
                ArrayList<Edge> adjList = cur.getAdjEdgeList();
                for (Edge e : adjList) {
                    if (e.getNodeID1() == next.getNodeID() || e.getNodeID2() == next.getNodeID()) {
                        e.setState(alvis301.State.path);
                        updateEdge(e);
                        break;
                    }
                }
            }
            else {
                next = minNode;
                ArrayList<Edge> adjList = cur.getAdjEdgeList();
                for (Edge e : adjList) {
                    if (e.getNodeID1() == next.getNodeID() || e.getNodeID2() == next.getNodeID()) {
                        e.setState(alvis301.State.path);
                        updateEdge(e);
                        break;
                    }
                }
            }
            cur = next;
            gd=(GameNodeData) cur.getData();
        
        }
    }
    }