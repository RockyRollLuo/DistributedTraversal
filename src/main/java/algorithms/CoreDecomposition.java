package algorithms;

import org.apache.log4j.Logger;
import util.Graph;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

public class CoreDecomposition {
    private static Logger LOGGER = Logger.getLogger(CoreDecomposition.class);

    private Graph graph;

    public CoreDecomposition(Graph graph) {
        this.graph = graph;
    }

    public void run() {
        Hashtable<Integer, TreeSet<Integer>> adjMap = graph.getAdjMap();

        Hashtable<Integer, TreeSet<Integer>> tempAdjMap = new Hashtable<Integer, TreeSet<Integer>>();
        for (Integer node : adjMap.keySet()) {
            TreeSet<Integer> adjList=adjMap.get(node);
            tempAdjMap.put(node, (TreeSet<Integer>) adjList.clone());
        }
        Set<Integer> nodeSet = adjMap.keySet();

        //degMap
        Hashtable<Integer, Integer> degMap = new Hashtable<Integer, Integer>();
        for (Integer node : nodeSet) {
            degMap.put(node, adjMap.get(node).size());
        }

        //coreMap
        Hashtable<Integer, Integer> coreMap = new Hashtable<Integer, Integer>();
        for (int i = 1; ; ) {


            LinkedList<Integer> delQueue = new LinkedList<Integer>();

            for (Integer node : adjMap.keySet()) {
                if (adjMap.get(node).size() <= i) {
                    delQueue.offer(node);
                }
            }



        }



    }


}
