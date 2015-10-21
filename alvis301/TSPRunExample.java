package alvis301;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sowmya
 */
public class TSPRunExample {
    //static double[][] points = new double[100][2];
    Graph g = new Graph();
    HashMap <Integer, Node> nodes = g.getNodes();    
    public void constructGraph (double[][] points, int number) {
        for (int i=0 ; i<number; i++) {
            Node n = new Node(points[i][0], points[i][1], i);
            n.setState(State.open);
            g.setNode(n);
        }
        int eid = 1;
        for (int i=0 ; i<number; i++) {
            Node n = g.getNode(i);
            ArrayList<Node> adjList = n.getAdjList();
            ArrayList<Edge> adjEdgeList = n.getAdjEdgeList();
            for (int j=0; j<number; j++) {
                if (i != j) 
                    adjList.add(g.getNode(j));
                if (i < j) {
                    Node n1 = g.getNode(j);
                    Edge e = new Edge(n.getNodeID(),n1.getNodeID(),eid);
                    e.setState(State.tsp);
                    g.createNormalEdge(e);
                    ArrayList<Edge> otherAdjEdgeList = n1.getAdjEdgeList();
                    otherAdjEdgeList.add(e);
                    adjEdgeList.add(e);
                    n.setAdjEdgeList(adjEdgeList);
                    n1.setAdjEdgeList(otherAdjEdgeList);
                    nodes.put(n.getNodeID(), n);
                    nodes.put(j, n1);
                    g.setNodes(nodes);
                    eid++;
                } 
            }
        }
      //  Graph.setInstance(g);
    }
    public void displayTour(String tourString) throws InterruptedException {
        String[] nodeOrder = tourString.split(",");
        ArrayList<Node> tour = new ArrayList<>();
        HashMap<Integer,Edge> edges = g.getEdges();
        int startId = -1;
        for (String nodeString : nodeOrder) {
            int id = (int) Double.parseDouble(nodeString);
            if (startId == -1)
                startId = id;
            tour.add(g.getNode(id));
        }
        tour.add(g.getNode(startId));
        Node prev = tour.get(0);
        //Node first = prev;
        //tour.remove(0);
       // System.out.println(tour.get(0));
        for (Map.Entry pairs : edges.entrySet()) {
            Edge e = (Edge) pairs.getValue();
            e.setState(alvis301.State.tsp);
            edges.put(e.getEdgeID(),e);
            g.setEdges(edges);
           // Graph.setInstance(g);
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
                    edges.put(e.getEdgeID(),e);
                    g.setEdges(edges);
                    break;
                }
            }
            prev = n;
        }
        
    }
    
}
