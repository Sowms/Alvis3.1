package alvis301;


import alvis301.Graph;
import alvis301.TSPRunExample;
import alvis301.TSPWindow;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
public class TSPFileRun {
    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomeWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomeWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomeWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomeWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        ColorMap cm = new ColorMap();
        ColorMap.setInstance(cm);
        
        double[][] points = new double[200][2];
        String filename = "TSPinput", tour = "";
        BufferedReader br = null;
	try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(filename));
            int counter = 0;
            while ((sCurrentLine = br.readLine()) != null) {
	//	System.out.println(sCurrentLine);
                if (sCurrentLine.equals("")) 
                    continue;
                if (sCurrentLine.split(",").length > 2)
                    break;
                points[counter][0] = Double.parseDouble(sCurrentLine.split(",")[0]);
                points[counter][1] = Double.parseDouble(sCurrentLine.split(",")[1]);
                counter++;
	    }
            TSPRunExample tspRun = new TSPRunExample();
            tspRun.constructGraph(points, counter);
            tour = sCurrentLine;
            
            tspRun.displayTour(tour);
            TSPWindow t = new TSPWindow();
            t.setVisible(true);
            t.setTitle("TSP");
            Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            t.setLocation(screenSize.width/3, screenSize.height/3);
            //t.setVisible(true);
            t.setExtendedState(JFrame.MAXIMIZED_BOTH);
            TSPWindow.setInstance(t);
            Graph.setInstance(tspRun.g);
            t.showGraph();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (InterruptedException ex) {
            Logger.getLogger(TSPRunExample.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
		if (br != null)br.close();
	    } catch (IOException ex) {
		ex.printStackTrace();
	    }
        }
    }
}
