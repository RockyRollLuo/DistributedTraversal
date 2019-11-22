package algorithms;

import org.apache.log4j.Logger;
import util.Graph;
import util.GraphHandler;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

public class CoreDecomposition {
    private static Logger LOGGER = Logger.getLogger(CoreDecomposition.class);

    private Graph graph;

    public CoreDecomposition(Graph graph) {
        this.graph = graph;
    }

    public void run() {
        Hashtable<Integer, LinkedList<Integer>> adjMap = graph.getAdjMap();

        Hashtable<Integer, LinkedList<Integer>> tempAdjMap = new Hashtable<Integer, LinkedList<Integer>>();
        for (Integer node : adjMap.keySet()) {
            LinkedList<Integer> adjList = adjMap.get(node);
            tempAdjMap.put(node, (LinkedList<Integer>) adjList.clone());
        }
        Set<Integer> nodeSet = adjMap.keySet();

        //degMap
        Hashtable<Integer, Integer> degMap = new Hashtable<>();
        for (Integer node : nodeSet) {
            degMap.put(node, adjMap.get(node).size());
        }

        //coreMap
        LinkedList<Integer> remainNodes = new LinkedList<>(adjMap.keySet());
        Hashtable<Integer, Integer> coreMap = new Hashtable<>();
        for (int i = 1; ; ) {

            if (remainNodes.isEmpty()) {
                break;
            }

            LinkedList<Integer> delQueue = new LinkedList<>();
            for (Integer node : remainNodes) {
                if (tempAdjMap.get(node).size() <= i) {
                    delQueue.offer(node);
                }
            }

            while (!delQueue.isEmpty()) {
                Integer node_delQue = delQueue.poll();

                tempAdjMap = GraphHandler.removeNodeFromAdjMap(tempAdjMap, node_delQue);
                for (Integer remainNode : tempAdjMap.keySet()) {


                }

                coreMap.put(node_delQue, i);
                remainNodes.remove(node_delQue);



            }


        }


    }


}
