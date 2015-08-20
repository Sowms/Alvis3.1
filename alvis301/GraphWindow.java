/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alvis301;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author sowmya
 */
class AlgoStats {
        public int openSize;
        public int closeSize;
        public int pathSize;
        public String name;
}
    
public class GraphWindow extends javax.swing.JFrame {

    AlgoStats[] allAlgoStats = new AlgoStats[20];
    int numAlgo = -1;
    boolean start;
    boolean goal;
    boolean running;
    boolean obstacle;
    public static GraphWindow gw;
    public static int time = 100;
    public String density;
    public ArrayList <Edge> path;
    public BFSAlgorithm bfs;
    public DFSAlgorithm dfs;
    public SMGSAlgo smgs;
    public BFHS bfhs;
    public BeamSearch bs;
    public DCBS dcbs;
    public DCBFHS dcbfhs;
    public DCBSS dcbss;
    public IDAstar idastar;
    public DFID dfid;
    public BeamStackSearch bss;
    private void computeLabel() {
        ArrayList<JComponent> firstLine = new ArrayList<>();
        ArrayList<JComponent> secondLine = new ArrayList<>();
        ArrayList<JComponent> thirdLine = new ArrayList<>();
        for (int i = 0; i <= numAlgo; i++) {
            JPanel greenPanel1 = new JPanel();
            greenPanel1.setBackground(new Color(211,239,140));
            JPanel greenPanel2 = new JPanel();
            greenPanel2.setBackground(new Color(211,239,140));
            firstLine.add(greenPanel1);
            firstLine.add(new JLabel(allAlgoStats[i].name));
            firstLine.add(greenPanel2);
            secondLine.add(new JLabel("Open"));
            secondLine.add(new JLabel("Closed"));
            secondLine.add(new JLabel("Path"));
            thirdLine.add(new JLabel(allAlgoStats[i].openSize+""));
            thirdLine.add(new JLabel(allAlgoStats[i].closeSize+""));
            thirdLine.add(new JLabel(allAlgoStats[i].pathSize+""));
        }
        for (Component c : firstLine) {
            jPanel1.add(c);
        }
        for (int i = firstLine.size(); i < 30; i++) {
            JPanel greenPanel = new JPanel();
            greenPanel.setBackground(new Color(211,239,140));
            jPanel1.add(greenPanel);
        }
        for (Component c : secondLine) {
            jPanel1.add(c);
        }
        for (int i = secondLine.size(); i < 30; i++){
            JPanel greenPanel = new JPanel();
            greenPanel.setBackground(new Color(211,239,140));
            jPanel1.add(greenPanel);
        }
        for (Component c : thirdLine) {
            jPanel1.add(c);
        }
        for (int i = thirdLine.size(); i < 30; i++) {
            JPanel greenPanel = new JPanel();
            greenPanel.setBackground(new Color(211,239,140));
            jPanel1.add(greenPanel);
        }
       
    }
    public static GraphWindow getInstance() {
        return gw;
    }
    public static void setInstance(GraphWindow k) {
        gw=k;
    }
    public GraphWindow() {
        allAlgoStats = new AlgoStats[20];
        initComponents();
        // Dimension panelSize = Toolkit.getDefaultToolkit().getScreenSize();
        path = new ArrayList<Edge>();
        //jButton2.setVisible(false);
        jSlider1.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider)e.getSource();
            if (!source.getValueIsAdjusting()) {
                time = (int)source.getValue();
            }
        }  
        });
        graphPanel1.addMouseListener(new MouseListener(){   
            public void mouseClicked(MouseEvent e) {
                if(!start && !goal)
                    return;
                Graph g = Graph.getInstance();
                HashMap nodes = g.getNodes();
                double x = e.getX();
                double y = e.getY();
                for (int i=(int) (x-4); i<x+4;i++)
                    for (int j=(int) (y-4);j<y+4;j++) {
                        Integer id = g.getNodeID(i, j);
                        if (id == null || nodes.get(id)==null)
                            continue;
                        int ID = (start) ? g.getStartID() : g.getGoalID();
                        if (ID == -1) {  
                            if (start)
                                g.choose_start(i, j);
                            else
                                g.choose_goal(i, j);
                            }
                            else {
                            Node n = g.getNode(ID);
                            if (start)
                                n.setState(State.unvisited);
                            nodes.put(ID, n);
                            if (start)
                                g.choose_start(i, j);
                            else
                                g.choose_goal(i, j);
                            }
                            break;
                    }
                graphPanel1.repaint();
                }
                public void mousePressed(MouseEvent e) {
                    if(obstacle) {
                        graphPanel1.x1=e.getX();
                        graphPanel1.y1=e.getY();
                    }
                }
            public void mouseReleased(MouseEvent e) {
                if(obstacle) {                        
                    graphPanel1.x2=e.getX();
                    graphPanel1.y2=e.getY();
                    graphPanel1.repaint();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GraphWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    obstacle=false;
                    Graph g=Graph.getInstance();
                    HashMap <ArrayList<Double>,Integer> posID=g.getPosIDs();
                    HashMap<Integer,Node> nodes=g.getNodes();
                    HashMap<Integer,Edge> edges=g.getEdges();
                    for(int i=graphPanel1.x1;i<graphPanel1.x2;i++ ) {
                        for(int j=graphPanel1.y1;j<graphPanel1.y2;j++ ) {
                            ArrayList<Double> point=new ArrayList<>();
                            point.add((double)i);
                            point.add((double)j);
                            Integer nodeID=posID.get(point);
                            if (nodeID == null || g.getNode(nodeID)== null)
                                continue;
                            Node n=nodes.get(nodeID);
                            ArrayList<Edge> adjEdgeList=n.getAdjEdgeList();
                            for (Edge adjEdgeList2 : adjEdgeList) {
                                edges.remove(adjEdgeList2.getEdgeID());
                                Edge e1 = adjEdgeList2;
                                Node node1=g.getNode(e1.getNodeID1());
                                Node node2=g.getNode(e1.getNodeID2());
                                Node node_s=node1.equals(n)?node2:node1;
                                ArrayList<Edge> adjEdgeList1 = node_s.getAdjEdgeList();
                                adjEdgeList1.remove(e1);
                                ArrayList<Node> adjList = node_s.getAdjList();
                                adjList.remove(n);
                                node_s.setAdjEdgeList(adjEdgeList1);
                                node_s.setAdjList(adjList);
                                nodes.put(node_s.getNodeID(), node_s);
                            }
                            nodes.remove(n.getNodeID());
                        }
                    }
                    g.setEdges(edges);
                    g.setNodes(nodes);
                    Graph.setInstance(g);
                    graphPanel1.repaint();
                }
            }
            public void mouseEntered(MouseEvent e) {
            }
            public void mouseExited(MouseEvent e) {
            }
        });
        jButton2.setEnabled(true);
        jButton3.setEnabled(true);
        jButton4.setEnabled(false);
        jButton5.setEnabled(false);
        jButton6.setEnabled(false);
    }
     public void showGraph() throws InterruptedException {
        graphPanel1.repaint();
        resetPanel();
    }
    private void resetPanel() {
        if (numAlgo != -1) {
            allAlgoStats[numAlgo].openSize = GraphPanel.openSize;
            allAlgoStats[numAlgo].closeSize = GraphPanel.closedSize;
            allAlgoStats[numAlgo].pathSize = GraphPanel.pathSize;
        }
        jPanel1.setVisible(false);
        jPanel1.removeAll();
        jPanel1.invalidate();
        computeLabel();
        jPanel1.repaint();
        jPanel1.setVisible(true);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        graphPanel1 = new alvis301.GraphPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jSlider1 = new javax.swing.JSlider();
        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton2.setText("Start");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Goal");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Obstacle");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Run");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Pipe");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout graphPanel1Layout = new javax.swing.GroupLayout(graphPanel1);
        graphPanel1.setLayout(graphPanel1Layout);
        graphPanel1Layout.setHorizontalGroup(
            graphPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1469, Short.MAX_VALUE)
        );
        graphPanel1Layout.setVerticalGroup(
            graphPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 214, Short.MAX_VALUE)
        );

        jPanel1.setBackground(new java.awt.Color(211, 239, 140));
        jPanel1.setLayout(new java.awt.GridLayout(3, 30));

        jMenu1.setText("File");

        jMenuItem1.setText("New");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Save");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Home");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(217, 217, 217))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(graphPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1469, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSlider1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(jButton3)
                        .addComponent(jButton4)
                        .addComponent(jButton6)
                        .addComponent(jButton5)))
                .addGap(3, 3, 3))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

        
                                   
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
       HashMap <Integer,Node> nodes;
        HashMap <Integer,Node> edges;
        
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread currentThread : threadSet) {
            String threadName = currentThread.toString();
            if (threadName.contains("[Thread") && threadName.contains("main")) {
                System.out.println(threadName);
                currentThread.interrupt();
                break;
            }
        }
        
        path = new ArrayList<Edge>();
        Graph g = Graph.getInstance();
        nodes = g.getNodes();
        edges = g.getEdges();    
        for (Map.Entry pairs : nodes.entrySet()) {
            Node n = (Node) pairs.getValue();
            State state = n.getState();
            if(state!=State.unvisited) {
                n.setState(State.pipe);
            }
            if(n.getNodeID()==g.getGoalID())
                 n.setState(State.goal);
            if(n.getNodeID()==g.getStartID())
                 n.setState(State.start);
            nodes.put(n.getNodeID(), n);
        }
        for (Map.Entry pairs : edges.entrySet()) {
            Edge e = (Edge) pairs.getValue();
            State state = e.getState();
            if(state==State.path) {
                e.setState(State.pipepath);
                path.add(e);
            }  
            else if(state!=State.unvisited) {
                e.setState(State.pipe);
            }
        }
        g.setNodes(nodes);
        Graph.setInstance(g);
        graphPanel1.repaint();
        resetPanel();
        jButton2.setEnabled(false);
        jButton3.setEnabled(false);    
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        start = true;
        goal = false;
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
         start = false;
            goal = true;
            jButton4.setEnabled(true);
            jButton5.setEnabled(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        obstacle=true;
        start=false;
        goal=false;
        jButton2.setEnabled(false);
        jButton3.setEnabled(false);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        JList list = new JList(new String[] {"BFS", "DFS", "SMGS", "BS", "DCBS","BFHS","DCBFHS", "DCBSS", "IDAstar", "DFID", "BSS" });
        JOptionPane.showMessageDialog(null, list, "Choose Algorithm", JOptionPane.PLAIN_MESSAGE);
        String choice = Arrays.toString(list.getSelectedIndices());
        numAlgo++;
        allAlgoStats[numAlgo] = new AlgoStats();
        allAlgoStats[numAlgo].name = list.getSelectedValue().toString();
        switch (choice) {
            case "[0]":
                bfs = new BFSAlgorithm(1);
                bfs.setGraph();
                bfs.start();
                graphPanel1.repaint();
                resetPanel();
                break;
            case "[1]":
                dfs = new DFSAlgorithm(1);
                dfs.setGraph();
                dfs.start();
                graphPanel1.repaint();
                resetPanel();
                break;
            case "[2]":
                {
                    smgs = new SMGSAlgo(1);
                    HashMap<Integer,Node> nodes = Graph.getInstance().getNodes();
                    HashMap<Integer,Node> newNodes = new HashMap<Integer,Node>();
                    Iterator nodeIterator = nodes.entrySet().iterator();
                    while (nodeIterator.hasNext()) {
                        Map.Entry pairs = (Map.Entry)nodeIterator.next();
                        System.out.println(pairs.getKey() + " = " + pairs.getValue());
                        Node currentNode = (Node) pairs.getValue();
                        SMGSNodeData sdata = new SMGSNodeData(currentNode.getNodeID());
                        currentNode.setData(sdata);
                        newNodes.put(currentNode.getNodeID(), currentNode);
                    }       Graph g = Graph.getInstance();
                    g.setNodes(newNodes);
                    Graph.setInstance(g);
                    smgs.setGraph();
                    smgs.start();
                    graphPanel1.repaint();
                    resetPanel();
                    break;
                }
            case "[3]":
            {
                bs = new BeamSearch(1);
                HashMap<Integer,Node> nodes = Graph.getInstance().getNodes();
                HashMap<Integer,Node> newNodes = new HashMap<Integer,Node>();
                Iterator nodeIterator = nodes.entrySet().iterator();
                while (nodeIterator.hasNext()) {
                    Map.Entry pairs = (Map.Entry)nodeIterator.next();
                    System.out.println(pairs.getKey() + " = " + pairs.getValue());
                    Node currentNode = (Node) pairs.getValue();
                    DCNodeData sdata = new DCNodeData();
                    currentNode.setData(sdata);
                    newNodes.put(currentNode.getNodeID(), currentNode);
                }       Graph g = Graph.getInstance();
                g.setNodes(newNodes);
                Graph.setInstance(g);
                bs.setGraph();
                bs.start();
                graphPanel1.repaint();
                resetPanel();
                break;
                }
            case "[4]":
            {
                dcbs = new DCBS(1);
                HashMap<Integer,Node> nodes = Graph.getInstance().getNodes();
                HashMap<Integer,Node> newNodes = new HashMap<Integer,Node>();
                Iterator nodeIterator = nodes.entrySet().iterator();
                while (nodeIterator.hasNext()) {
                    Map.Entry pairs = (Map.Entry)nodeIterator.next();
                    System.out.println(pairs.getKey() + " = " + pairs.getValue());
                    Node currentNode = (Node) pairs.getValue();
                    DCNodeData sdata = new DCNodeData();
                    currentNode.setData(sdata);
                    newNodes.put(currentNode.getNodeID(), currentNode);
                }       Graph g = Graph.getInstance();
                g.setNodes(newNodes);
                Graph.setInstance(g);
                dcbs.setGraph();
                dcbs.start();
                graphPanel1.repaint();
                resetPanel();
                break;
                }
            case "[5]":
            {
                bfhs = new BFHS(1);
                HashMap<Integer,Node> nodes = Graph.getInstance().getNodes();
                HashMap<Integer,Node> newNodes = new HashMap<Integer,Node>();
                Iterator nodeIterator = nodes.entrySet().iterator();
                while (nodeIterator.hasNext()) {
                    Map.Entry pairs = (Map.Entry)nodeIterator.next();
                    System.out.println(pairs.getKey() + " = " + pairs.getValue());
                    Node currentNode = (Node) pairs.getValue();
                    DCNodeData sdata = new DCNodeData();
                    currentNode.setData(sdata);
                    newNodes.put(currentNode.getNodeID(), currentNode);
                }       Graph g = Graph.getInstance();
                g.setNodes(newNodes);
                Graph.setInstance(g);
                bfhs.setGraph();
                bfhs.start();
                graphPanel1.repaint();
                resetPanel();
                break;
                }
            case "[7]":
                {
                    dcbfhs = new DCBFHS(1);
                    HashMap<Integer,Node> nodes = Graph.getInstance().getNodes();
                    HashMap<Integer,Node> newNodes = new HashMap<Integer,Node>();
                    Iterator nodeIterator = nodes.entrySet().iterator();
                    while (nodeIterator.hasNext()) {
                        Map.Entry pairs = (Map.Entry)nodeIterator.next();
                        System.out.println(pairs.getKey() + " = " + pairs.getValue());
                        Node currentNode = (Node) pairs.getValue();
                        DCNodeData sdata = new DCNodeData();
                        currentNode.setData(sdata);
                        newNodes.put(currentNode.getNodeID(), currentNode);
                    }       Graph g = Graph.getInstance();
                    g.setNodes(newNodes);
                    Graph.setInstance(g);
                    dcbfhs.setGraph();
                    dcbfhs.start();
                    graphPanel1.repaint();
                    resetPanel();
                    break;
                }
            case "[8]":
            {
                dcbss = new DCBSS(1);
                HashMap<Integer,Node> nodes = Graph.getInstance().getNodes();
                HashMap<Integer,Node> newNodes = new HashMap<Integer,Node>();
                Iterator nodeIterator = nodes.entrySet().iterator();
                while (nodeIterator.hasNext()) {
                    Map.Entry pairs = (Map.Entry)nodeIterator.next();
                    System.out.println(pairs.getKey() + " = " + pairs.getValue());
                    Node currentNode = (Node) pairs.getValue();
                    DCNodeData sdata = new DCNodeData();
                    currentNode.setData(sdata);
                    newNodes.put(currentNode.getNodeID(), currentNode);
                }       Graph g = Graph.getInstance();
                g.setNodes(newNodes);
                Graph.setInstance(g);
                dcbss.setGraph();
                dcbss.start();
                    graphPanel1.repaint();
                    resetPanel();
                    break;
            }
            case "[9]":
                dfid = new DFID(1);
                dfid.setGraph();
                dfid.start();
                graphPanel1.repaint();
                resetPanel();
                break;
            case "[10]":
                idastar = new IDAstar(1);
                idastar.setGraph();
                idastar.start();
                graphPanel1.repaint();
                resetPanel();
                break;
            case "[11]":
                bss = new BeamStackSearch(1);
                bss.setGraph();
                bss.start();
                graphPanel1.repaint();
                resetPanel();
                break;
        }
        jButton6.setEnabled(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread currentThread : threadSet) {
            String threadName = currentThread.toString();
            System.out.println("all"+threadName);
            if (threadName.contains("[Thread") && threadName.contains("main")) {
                System.out.println("new"+threadName);
                currentThread.interrupt();
                break;
            }
        }
               // jButton1.setEnabled(false);
        jButton2.setEnabled(true);
        jButton3.setEnabled(true);
        jButton4.setEnabled(false);
        jButton5.setEnabled(false);
        jButton6.setEnabled(false);
        GraphCreator c = new GraphCreator();
        c.create(density);
        graphPanel1.repaint();
        allAlgoStats = new AlgoStats[20];
        numAlgo = -1;
        resetPanel();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        FileDialog fDialog = new FileDialog(this, "Save", FileDialog.SAVE);
        fDialog.setVisible(true);
        String fpath = fDialog.getDirectory() + fDialog.getFile();
        File f = new File(fpath);
        BufferedImage im = new BufferedImage(graphPanel1.getWidth(), graphPanel1.getHeight(), BufferedImage.TYPE_INT_ARGB);
        graphPanel1.paint(im.getGraphics());
        try {
            ImageIO.write(im, "PNG", f);
        } catch (IOException ex) {
            Logger.getLogger(GraphWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
       this.dispose();
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread currentThread : threadSet) {
            String threadName = currentThread.toString();
            if (threadName.contains("[Thread") && threadName.contains("main")) {
                System.out.println(threadName);
                currentThread.interrupt();
                break;
            }
        }
        
        HomeWindow Main = new HomeWindow();
        Main.setVisible(true);
        Main.setTitle("AlVis 3.0");
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Main.setLocation(screenSize.width/3, screenSize.height/3);
    }//GEN-LAST:event_jMenuItem3ActionPerformed
                                                                          
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GraphWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GraphWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GraphWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GraphWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GraphWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private alvis301.GraphPanel graphPanel1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSlider jSlider1;
    // End of variables declaration//GEN-END:variables
}
