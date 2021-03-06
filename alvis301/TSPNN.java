/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package alvis301;

import java.util.ArrayList;
//import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SavithaSam
 */
public class TSPNN extends Algorithm{

    public TSPNN(int t) {
        super(t);
    }

    @Override
    public boolean goalTest(Node goalNode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object moveGen(Node parentNode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void run() {
        TSPGraphV lg1 = new TSPGraphV("Nearest Neighbour","1000");
        ArrayList<Node> cur = getSolution(1);
        setTour(cur);
        lg1.addDataSet(new Double(1), costTour(cur));   
        for (int i = 2; i < g.getNoNodes(); i++) {
            ArrayList<Node> next = getSolution(i);
            lg1.addDataSet(new Double(i), costTour(next));   
            if ( costTour(next) < costTour(cur) ) {
                setTour(next);
                cur = next;
                displayTSP(costTour(cur),new Double(0));
                
               // printSolution(cur);
            }
        }
        lg1.displayGraph();
    }
    public void printSolution (ArrayList<Node> Tour) {
        
        System.out.println("inside");
        for (Node n : Tour) {
            System.out.print(n.getNodeID()+" ");
        }
        
    }
    public double costTour(ArrayList<Node> tour) {
        
        Node first = tour.get(0);
        Node prev = first;
        //tour.remove(0);
        double cost = 0;
        for (Node n : tour) {
            if (n.equals(prev))
                continue;
            cost = cost + distance(prev,n);
            prev = n;
        }
        cost = cost + distance(prev,first);
        return cost;
    }
    public double distance(Node n1, Node n2) {
        return Math.sqrt(Math.pow((n1.getX()-n2.getX()),2)+Math.pow((n1.getY()-n2.getY()),2));
    }
    public void setTour (ArrayList <Node> tour) {
        
        Node prev = tour.get(0);
        Node first = prev;
        //tour.remove(0);
        for (Map.Entry pairs : edges.entrySet()) {
            Edge e = (Edge) pairs.getValue();
            e.setState(alvis301.State.tsp);
            updateEdge(e);
        }
        for (Node n:tour) {
            if (n.equals(prev))
                continue;
            ArrayList<Edge> adjEdgeList = prev.getAdjEdgeList();
            for (Edge e : adjEdgeList) {
                int nodeID1 = e.getNodeID1();
                int nodeID2 = e.getNodeID2();
                if (nodeID1 == n.getNodeID() || nodeID2 == n.getNodeID()) {
                    e.setState(alvis301.State.path);
                    //System.out.println(e.getNodeID1()+"|"+e.getNodeID2());
                    updateEdge(e);
                    break;
                }
            }
            prev = n;
        }
        
        ArrayList<Edge> adjEdgeList = prev.getAdjEdgeList();
            for (Edge e : adjEdgeList) {
                int nodeID1 = e.getNodeID1();
                int nodeID2 = e.getNodeID2();
                if (nodeID1 == first.getNodeID() || nodeID2 == first.getNodeID()) {
                    e.setState(alvis301.State.path);
                   // System.out.println(e.getNodeID1()+"|"+e.getNodeID2());
                    updateEdge(e);
                    break;
                }
            }
    }
    public ArrayList<Node> getSolution(int start){
       
        ArrayList<Node> solution = new ArrayList<Node>();
        int counter = 0;
        int size = g.getNoNodes();    
        Random rand = new Random();
        Node prev = null;
        Node startNode = g.getNode(start);
        solution.add(startNode);
        while (solution.size() < size) {
            double minCost = Double.MAX_VALUE;
            Node nextNode = null;
            for (int i = 1; i <= size; i++) {
                if (solution.contains(g.getNode(i)))
                    continue;
                double curCost = distance(startNode,g.getNode(i));
                if (curCost < minCost) {
                    minCost = curCost;
                    nextNode = g.getNode(i);
                }
            }
            solution.add(nextNode);
        }
        solution.add(startNode);
        return solution;
    }
            
    
}
