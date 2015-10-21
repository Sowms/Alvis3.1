package alvis301;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
/**
 *
 * @author Stanley
 */
public class SSSStar extends Algorithm
    {
    ArrayList<Edge> path;
    
    public SSSStar(int t)
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
        LinkedHashSet<Node> adjList = new LinkedHashSet(parentNode.getAdjList());
        GameNodeData gdp = (GameNodeData) parentNode.getData();
        ArrayList<Node> children = new ArrayList<Node>();
        for(Node node1 : adjList)
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
           gd.value = getMinimaxWithSSSStar(root);
           //System.out.println("a"+gd.value);
           root.setData(gd);
           updateNode(root);
           display();   
    }

    public ArrayList<Quad> insert(Quad quad, ArrayList<Quad> open)
        {
        ArrayList<Quad> updatedOpen = new ArrayList<Quad>();
        int i;
        for(i = 0 ; i < open.size() ; i++)
            {
            Quad entry = open.get(i);
            if(quad.h >= entry.h)
                break;
            updatedOpen.add(entry);
            }
        updatedOpen.add(quad);
        for( ; i < open.size() ; i++)
            updatedOpen.add(open.get(i));
        return updatedOpen;
        }
    
    public ArrayList<Quad> remove(Node node, ArrayList<Quad> open)
        {
        ArrayList<Quad> updatedOpen = new ArrayList<Quad>();
        for(Quad quad : open)
            {
            if(quad.j == node)
                {
                System.out.println("removing useless <" + quad.j.getNodeID() + ", " + quad.s + ", " + quad.h + " >");
                continue;
                }
            updatedOpen.add(quad);
            }
        return updatedOpen;
        }

    public void repaintOldPaths(Node root)
        {
        ArrayList<Node> queue = new ArrayList<Node>();
        queue.add(root);
        while(queue.isEmpty() == false)
            {
            Node node = queue.remove(0);
            ArrayList<Edge> adjList = node.getAdjEdgeList();
            for(Edge e : adjList)
                if(e.getState() == alvis301.State.boundary)
                    {
                    e.setState(node.getState());
                    updateEdge(e);
                    }
            queue.addAll(moveGen(node));
            }
        }

    public ArrayList<Edge> reconstructPath(Node leaf, Node root, HashMap<Node, Node> parent)
        {
        ArrayList<Edge> reconstructedPath = new ArrayList<Edge>();
        Node node, parent_node;
        node = leaf;
        while(node != root)
            {
            parent_node = parent.get(node);
            ArrayList<Edge> adjList = parent_node.getAdjEdgeList();
            for(Edge e : adjList)
                if(node.equals(g.getNode(e.getNodeID1())) || node.equals(g.getNode(e.getNodeID2())))
                    reconstructedPath.add(e);
            node = parent_node;
            }
        return reconstructedPath;
        }

    public void printPath(ArrayList<Edge> path)
        {
        for(int i = 0 ; i < path.size() ; i++)
            {
            Edge e = path.get(i);
            e.setState(alvis301.State.path);
            updateEdge(e);
            }
        display();
        }

    public void printRecentPath(ArrayList<Edge> path)
        {
        for(int i = path.size() - 1 ; i >= 0 ; i--)
            {
            Edge e = path.get(i);
            e.setState(alvis301.State.boundary);
            updateEdge(e);
            display();
            }
        //display();
        }

    public int getMinimaxWithSSSStar(Node root)
        {
        ArrayList<Quad> open = new ArrayList<Quad>();
        //get parents of all nodes
        HashMap<Node, Node> parent = new HashMap<Node, Node>();
        ArrayList<Node> queue = new ArrayList<Node>();
        queue.add(root);
        while(queue.isEmpty() == false)
            {
            Node t = queue.remove(0);
            if(testTerminal(t))
                continue;
            ArrayList<Node> children = moveGen(t);
            for(Node child : children)
                {
                parent.put(child, t);
                queue.add(child);
                }
            }
        //get children of MIN nodes
        HashMap<Node, ArrayList<Node>> minsUnsolvedChildren = new HashMap<Node, ArrayList<Node>>();
        queue.add(root);
        while(queue.isEmpty() == false)
            {
            Node t = queue.remove(0);
            ArrayList<Node> children = moveGen(t);
            if(t.getState() == alvis301.State.min)
                minsUnsolvedChildren.put(t, children);
            for(Node child : children)
                queue.add(child);
            }
        //open <- (< root, LIVE, +LARGE >)
        open.add(new Quad(root, Label.LIVE, Integer.MAX_VALUE, null));
        System.out.println("adding < " + root.getNodeID() + ", LIVE, " + Integer.MAX_VALUE + " >");
        //repeat
        do
            {
            //Remove node p = < J, s, h > from head of open
            Quad p = open.remove(0);
            System.out.println("removing <" + p.j.getNodeID() + ", " + p.s + ", " + p.h + " >");
            //if J = root and s = SOLVED then return h		i.e., return when root is SOLVED
            if(p.j == root && p.s == Label.SOLVED)
                {
                printPath(reconstructPath(p.leaf, root, parent));
                return p.h;
                }
            //else if s = LIVE
            if(p.s == Label.LIVE)
                {
                //then if J is terminal
                if(testTerminal(p.j))
                    {
                    //repaintOldPaths(root);
                    printRecentPath(reconstructPath(p.j, root, parent));
                    repaintOldPaths(root);
                    System.out.println();
                    //open <- Insert(< J, SOLVED, Min(h, e(J)) >, open)
                    p.s = Label.SOLVED;
                    GameNodeData gd = (GameNodeData) p.j.getData();
                    if(gd.value < p.h)
                        {
                        p.h = gd.value;
                        p.leaf = p.j;
                        }
                    open = insert(p, open);
                    System.out.println("adding <" + p.j.getNodeID() + ", " + p.s + ", " + p.h + " >");
                    }
                else if(p.j.getState() == alvis301.State.min)
                    {
                    ArrayList<Node> children = minsUnsolvedChildren.remove(p.j);
                    Node firstChild = children.remove(0);
                    minsUnsolvedChildren.put(p.j, children);
                    open.add(0, new Quad(firstChild, Label.LIVE, p.h, p.leaf));
                    System.out.println("adding <" + firstChild.getNodeID() + ", LIVE, " + p.h + " >");
                    }
                else
                    {
                    ArrayList<Node> children = moveGen(p.j);
                    for(int i = children.size() - 1 ; i >= 0 ; i--)
                        {
                        open.add(0, new Quad(children.get(i), Label.LIVE, p.h, p.leaf));
                        System.out.println("adding <" + children.get(i).getNodeID() + ", LIVE, " + p.h + " >");
                        }
                    }
                }
            //else		/* status s = SOLVED */
            else
                {
                Node parent_j = parent.get(p.j);
                if(p.j.getState() == alvis301.State.min)
                    {
//                    GameNodeData gd = (GameNodeData) parent_j.getData();
//                    gd.value = p.h;
//                    parent_j.setData(gd);
//                    updateNode(parent_j);
                    open.add(0, new Quad(parent_j, Label.SOLVED, p.h, p.leaf));
                    System.out.println("adding <" + parent_j.getNodeID() + ", SOLVED, " + p.h + " >");
                    //remove from open all the states that are associated with the children of parent_j
                    queue.clear();
                    queue.addAll(moveGen(parent_j));
                    while(queue.isEmpty() == false)
                        {
                        Node t = queue.remove(0);
                        open = remove(t, open);
                        queue.addAll(moveGen(t));
                        }
                    }
                else if(minsUnsolvedChildren.get(parent_j).isEmpty())
                    {
                    System.out.println("executing when minsUnsolvedChildren is empty");
//                    GameNodeData gd = (GameNodeData) parent_j.getData();
//                    gd.value = p.h;
//                    parent_j.setData(gd);
//                    updateNode(parent_j);
                    open.add(0, new Quad(parent_j, Label.SOLVED, p.h, p.leaf));
                    System.out.println("adding <" + parent_j.getNodeID() + ", SOLVED, " + p.h + " >");
                    }
                else
                    {
                    System.out.println("executing when minsUnsolvedChildren is not empty");
                    ArrayList<Node> unsolvedChildren = minsUnsolvedChildren.get(parent_j);
                    minsUnsolvedChildren.remove(parent_j);
                    Node nextChild = unsolvedChildren.get(0);
                    unsolvedChildren.remove(0);
                    minsUnsolvedChildren.put(parent_j, unsolvedChildren);
                    open.add(0, new Quad(nextChild, Label.LIVE, p.h, p.leaf));
                    System.out.println("adding <" + nextChild.getNodeID() + ", LIVE, " + p.h + " >");
                    }
                }
            }while(true);
        }
    }