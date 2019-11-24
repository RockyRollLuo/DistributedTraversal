package algorithms;

import org.apache.log4j.Logger;
import sun.security.krb5.internal.crypto.RsaMd5CksumType;
import util.Graph;
import util.GraphHandler;
import util.Result;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

public class CoreDecomposition {
    private static Logger LOGGER = Logger.getLogger(CoreDecomposition.class);

    private Graph graph;

    public CoreDecomposition(Graph graph) {
        this.graph = graph;
    }

    public Result run() {
        LOGGER.info("Starting Core Decomposition");

        long startTime = System.currentTimeMillis();

        Hashtable<Integer, LinkedList<Integer>> adjMap = graph.getAdjMap();
        Hashtable<Integer, LinkedList<Integer>> tempAdjMap = GraphHandler.deepCloneAdjMap(adjMap);

        //initial degMap
        Hashtable<Integer, Integer> degMap = new Hashtable<>();
        for (Integer node : tempAdjMap.keySet()) {
            degMap.put(node, tempAdjMap.get(node).size());
        }

        //coreMap
        Hashtable<Integer, Integer> coreMap = new Hashtable<>();

        for (int i = 0; ; i++) {

            if (tempAdjMap.isEmpty()) {
                break;
            }

            LinkedList<Integer> delQueue = new LinkedList<>();
            for (Integer node : tempAdjMap.keySet()) {
                if (tempAdjMap.get(node).size() <= i) {
                    delQueue.offer(node);
                }
            }

            while (!delQueue.isEmpty()) {
                Integer node_delQue = delQueue.poll();

                LinkedList<Integer> del_node_neigbors = tempAdjMap.get(node_delQue);
                if (del_node_neigbors != null) {
                    for (Integer del_node_neighbor : del_node_neigbors) {
                        tempAdjMap.get(del_node_neighbor).remove(node_delQue);
                        if (tempAdjMap.get(del_node_neighbor).size() <= i) {
                            delQueue.offer(del_node_neighbor);
                        }
                    }
                }
                tempAdjMap.remove(node_delQue);
                coreMap.put(node_delQue, i);
            }
        }

        return new Result(coreMap, System.currentTimeMillis() - startTime, "CoreDecomp");
    }

}
