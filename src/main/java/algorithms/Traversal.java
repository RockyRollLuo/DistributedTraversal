package algorithms;

import org.apache.log4j.Logger;
import util.Edge;
import util.Graph;

import java.util.Hashtable;
import java.util.LinkedList;

public class Traversal {
    private static Logger LOGGER = Logger.getLogger(Traversal.class);

    private Graph graph;
    private Hashtable<Integer, Integer> pcd;
    private Hashtable<Integer, Integer> mcd;



    //
    public Traversal(Graph graph) {
        this.graph = graph;
    }

    private void computePCD() {
        Hashtable<Integer, LinkedList<Integer>> adjMap = graph.getAdjMap();

        for (Integer node : adjMap.keySet()) {

        }
    }


    private void computeMCD() {


    }

    public void runInsert(Graph graph) {

    }

    public void runInsert(Graph graph, Edge e0) {

    }

    public void runDeletion(Graph graph) {



    }

    public void runDeletion(Graph graph, Edge e0) {

    }

}
