/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alvis301;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Stanley
 */
public class AlphaBeta extends Algorithm
    {
    public AlphaBeta(int t)
        {
        super(t);
        }
    
    @Override
    public boolean goalTest(Node goalNode)
        {
        throw new UnsupportedOperationException("Not supported yet.");
        }
    
    @Override
    public ArrayList<Node> moveGen(Node parentNode)
        {
        ArrayList<Node> adjlist;
        GameNodeData gdp = (GameNodeData) parentNode.getData();
        ArrayList<Node> children = new ArrayList<Node>();
        adjlist = parentNode.getAdjList();
        for(Node node1 : adjlist)
            {
            GameNodeData gd = (GameNodeData) node1.getData();
            if(gd.level > gdp.level)
                children.add(node1);
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
    
    public Pair getMinimaxWithAlphaBeta(Node j, int alpha, int beta)
        {
        //if Terminal(j) then return e(j)
        if(testTerminal(j))
            {
            GameNodeData gd = (GameNodeData) j.getData();
            return new Pair(gd.value, gd.value);
            }
        ArrayList<Edge> adjList = j.getAdjEdgeList();
        Pair pair;
        int minimaxValue;
        //else if j is a MAX node
        ArrayList<Node> children = moveGen(j);
        int i = 1;
        int b = children.size();
        if(j.getState() == alvis301.State.max)
            {
            minimaxValue = Integer.MIN_VALUE;
            //then for i <- 1 to b      i.e., for each child j[i]
            Iterator<Node> childIterator = children.iterator();
            while(childIterator.hasNext())
                {
                Node child = childIterator.next();
                Edge modified = null;
                for(Edge e : adjList)
                    {
                    if(child.equals(g.getNode(e.getNodeID1())) || child.equals(g.getNode(e.getNodeID2())))
                        {
                        e.setState(alvis301.State.boundary);
                        updateEdge(e);
                        modified = e;
                        break;
                        }
                    }
                display();
                if(modified != null)
                    {
                    modified.setState(alvis301.State.max);
                    updateEdge(modified);
                    }
                //do alpha <- Max(alpha, AlphaBeta(j[i], alpha, beta)
                pair = getMinimaxWithAlphaBeta(child, alpha, beta);
                alpha = returnMax(alpha, pair.returnValue);
                minimaxValue = returnMax(minimaxValue, pair.minimaxValue);
                //if alpha >= beta then return beta
                if(alpha >= beta)
                    {
                    GameNodeData gd1 = (GameNodeData) j.getData();
                    gd1.value = minimaxValue;
                    j.setData(gd1);
                    updateNode(j);
                    while(childIterator.hasNext())
                        {
                        child = childIterator.next();
                        for(Edge e : adjList)
                            {
                            if(child.equals(g.getNode(e.getNodeID1())) || child.equals(g.getNode(e.getNodeID2())))
                                {
                                e.setState(alvis301.State.deleted);
                                updateEdge(e);
                                break;
                                }
                            }
                        }
                    display();
                    return new Pair(gd1.value, beta);
                    }
                //if i = b then return alpha
                if(i == b)
                    {
                    GameNodeData gd1 = (GameNodeData) j.getData();
                    gd1.value = minimaxValue;
                    j.setData(gd1);
                    updateNode(j);
                    return new Pair(gd1.value, alpha);
                    }
                i++;
                }
            }
        //else      i.e., j is MIN
        minimaxValue = Integer.MAX_VALUE;
        //for i <- 1 to b
        Iterator<Node> childIterator = children.iterator();
        while(childIterator.hasNext())
            {
            Node child = childIterator.next();
            Edge modified = null;
            for(Edge e : adjList)
                {
                if(child.equals(g.getNode(e.getNodeID1())) || child.equals(g.getNode(e.getNodeID2())))
                    {
                    e.setState(alvis301.State.boundary);
                    updateEdge(e);
                    modified = e;
                    break;
                    }
                }
            display();
            if(modified != null)
                {
                modified.setState(alvis301.State.min);
                updateEdge(modified);
                }
            //do beta <- Min(beta, AlphaBeta(j[i], alpha, beta)
            pair = getMinimaxWithAlphaBeta(child, alpha, beta);
            beta = returnMin(beta, pair.returnValue);
            minimaxValue = returnMin(minimaxValue, pair.minimaxValue);
            //if alpha >= beta then return alpha
            if(alpha >= beta)
                {
                GameNodeData gd1 = (GameNodeData) j.getData();
                gd1.value = minimaxValue;
                j.setData(gd1);
                updateNode(j);
                while(childIterator.hasNext())
                    {
                    child = childIterator.next();
                    for(Edge e : adjList)
                        {
                        if(child.equals(g.getNode(e.getNodeID1())) || child.equals(g.getNode(e.getNodeID2())))
                            {
                            e.setState(alvis301.State.deleted);
                            updateEdge(e);
                            break;
                            }
                        }
                    }
                display();
                return new Pair(gd1.value, alpha);
                }
            //if i = b then return beta
            if(i == b)
                {
                GameNodeData gd1 = (GameNodeData) j.getData();
                gd1.value = minimaxValue;
                j.setData(gd1);
                updateNode(j);
                return new Pair(gd1.value, beta);
                }
            i++;
            }
        return null;
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

    @Override
    public void run() {
           Node root=g.getNode(g.getStartID());
           reset(root);
           GameNodeData gd=(GameNodeData)root.getData();
           Pair pair = getMinimaxWithAlphaBeta(root,Integer.MIN_VALUE,Integer.MAX_VALUE);
           gd.value = pair.minimaxValue;
           System.out.println("a"+gd.value);
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
            /*if (cur.getState() == alvis3.iitm.ac.in.State.min) {
                        ArrayList<Edge> adjList = cur.getAdjEdgeList();
                        /*for (Edge e : adjList) {
                            if (children.contains(g.getNode(e.getNodeID1())) || children.contains(g.getNode(e.getNodeID2()))) {
                                e.setState(alvis3.iitm.ac.in.State.path);
                                updateEdge(e);
                            }
                        }    
                    }*/
            int max = gd.value; Node maxNode = next; int min = gd.value; Node minNode=next;
            for(int i=1;i<children.size();i++){
                //if (cur.getState() == alvis3.iitm.ac.in.State.max) {
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