package algorithms;

import org.apache.log4j.Logger;
import util.Graph;

import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

public class Traversal {

    private static Logger LOGGER = Logger.getLogger(Traversal.class);

    private Graph graph;

    public Traversal(Graph graph) {
        this.graph = graph;
    }

    public void computePCD() {
        Hashtable<Integer, TreeSet<Integer>> adjMap = graph.getAdjMap();

        for (Integer node:adjMap.keySet()) {

        }


    }



}
