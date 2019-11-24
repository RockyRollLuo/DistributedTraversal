package algorithms;

import org.apache.log4j.Logger;
import util.Edge;
import util.Graph;
import util.GraphHandler;
import util.Result;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Stack;

public class Traversal {
    private static Logger LOGGER = Logger.getLogger(Traversal.class);

    private Graph graph;

    //initial state
    public Traversal(Graph graph) {
        this.graph = graph;
    }

    /**
     * compute coreMap
     *
     * @param graph
     * @return
     */
    private Hashtable<Integer, Integer> computeCore(Graph graph) {
        LOGGER.info("Computing core");
        CoreDecomposition coreDecomposition = new CoreDecomposition(graph);
        Result resultCoreMap = coreDecomposition.run();
        return (Hashtable<Integer, Integer>) resultCoreMap.getOutput();
    }

    /**
     * compute mcdMap
     *
     * @param graph   a new graph
     * @param coreMap a old coreMap
     */
    private Hashtable<Integer, Integer> computeMCD(Graph graph, Hashtable<Integer, Integer> coreMap) {
        LOGGER.info("Computing mcd");
        Hashtable<Integer, LinkedList<Integer>> adjMap = graph.getAdjMap();
        Hashtable<Integer, Integer> mcdMap = new Hashtable<>();
        for (Integer node : adjMap.keySet()) {
            LinkedList<Integer> adjList = adjMap.get(node);
            int mcdCount = 0;
            for (Integer nei : adjList) {
                if (coreMap.get(nei) >= coreMap.get(node)) {
                    mcdCount++;
                }
            }
            mcdMap.put(node, mcdCount);
        }
        return mcdMap;
    }

    /**
     * compute pcdMap
     *
     * @param graph
     * @param coreMap
     * @param mcdMap
     * @return
     */
    private Hashtable<Integer, Integer> computePCD(Graph graph, Hashtable<Integer, Integer> coreMap, Hashtable<Integer, Integer> mcdMap) {
        LOGGER.info("Computing pcd");
        Hashtable<Integer, LinkedList<Integer>> adjMap = graph.getAdjMap();
        Hashtable<Integer, Integer> pcdMap = new Hashtable<>();
        for (Integer node : adjMap.keySet()) {
            LinkedList<Integer> adjList = adjMap.get(node);
            int pcdCount = 0;
            int core_node = coreMap.get(node);
            for (Integer nei : adjList) {
                int core_nei = coreMap.get(nei);
                int mcd_nei = mcdMap.get(nei);
                if ((core_nei > core_node) || (core_nei == core_node && mcd_nei > core_node)) {
                    pcdCount++;
                }
            }
            pcdMap.put(node, pcdCount);
        }
        return pcdMap;
    }


    /**
     * Traversal insert one edge
     *
     * @param graph
     * @param e0
     * @return
     */
    public Result runInsert(Graph graph, Edge e0) {
        LOGGER.info("Initialization computing Traversal inseting edge:" + e0.toString());

        Hashtable<Integer, LinkedList<Integer>> oldAdjMap = graph.getAdjMap();
        LinkedList<Edge> oldEdgesList = graph.getEdgeSet();


        LinkedList<Edge> newEdgesList = (LinkedList<Edge>) oldEdgesList.clone();
        if (newEdgesList.contains(e0)) {
            return new Result(computeCore(graph), 0, "TraversalInsert");
        }

        //new Graph
        newEdgesList.add(e0);
        Hashtable<Integer, LinkedList<Integer>> newAdjMap = GraphHandler.insertEdgeToAdjMap(oldAdjMap, e0);
        Graph newGraph = new Graph(newAdjMap, newEdgesList);
        this.graph = newGraph;

        //coreMap
        Hashtable<Integer, Integer> coreMap = computeCore(graph);
        //determine whether the node is new
        Integer v1_e0 = e0.getV1();
        Integer v2_e0 = e0.getV2();
        if (!newAdjMap.containsKey(v1_e0)) {
            coreMap.put(v1_e0, 1);
        }
        if (!newAdjMap.containsKey(v2_e0)) {
            coreMap.put(v2_e0, 1);
        }

        //mcdMap
        Hashtable<Integer, Integer> mcdMap = computeMCD(newGraph, coreMap);

        //pcdMap
        Hashtable<Integer, Integer> pcdMap = computePCD(newGraph, coreMap, mcdMap);

        //main process
        LOGGER.info("Starting computing Traversal inseting edge:" + e0.toString());
        long startTime = System.currentTimeMillis();

        Hashtable<Integer, Boolean> visitedMap = new Hashtable<>();
        Hashtable<Integer, Boolean> evictedMap = new Hashtable<>();
        Hashtable<Integer, Integer> cdMap = new Hashtable<>();
        //lazy initial
        for (Integer node : newAdjMap.keySet()) {
            visitedMap.put(node, false);
            evictedMap.put(node, false);
            cdMap.put(node, 0);
        }
        Integer root_node = v1_e0;
        if (v2_e0 < v1_e0) {
            root_node = v2_e0;
        }
        int root_k = coreMap.get(root_node);
        cdMap.put(root_node, pcdMap.get(root_node));

        Stack<Integer> stack = new Stack<>();
        stack.push(root_node);
        visitedMap.put(root_node, true);
        while (!stack.isEmpty()) {
            Integer node_stack = stack.pop();
            if (cdMap.get(node_stack) > root_k) {
                LinkedList<Integer> neiList_node_stack = newAdjMap.get(node_stack);
                for (Integer w : neiList_node_stack) {
                    if (coreMap.get(w) == root_k && mcdMap.get(w) > root_k && !visitedMap.get(w)) {
                        stack.push(w);
                        visitedMap.put(w, true);
                        int cd = cdMap.get(w);
                        cdMap.put(w, cd + pcdMap.get(w));
                    }
                }
            } else {
                if (!evictedMap.get(node_stack)) {
                    propagateEviction(newAdjMap, coreMap, cdMap, evictedMap, root_k, node_stack);
                }
            }
        }

        for (Integer v : newAdjMap.keySet()) {
            if (visitedMap.get(v) && !evictedMap.get(v)) {
                int core_v = coreMap.get(v);
                coreMap.put(v, core_v + 1);
            }
        }
        long endTime = System.currentTimeMillis();

        return new Result(coreMap, endTime - startTime, "TraversalInsert");
    }

    //TODO：it is wrong
    /**
     * Traversal delete one edge
     * @param graph
     * @param e0
     * @return
     */
    public Result runDeletion(Graph graph, Edge e0) {
        LOGGER.info("Initialization computing Traversal deleting edge:" + e0.toString());

        Hashtable<Integer, LinkedList<Integer>> oldAdjMap = graph.getAdjMap();
        LinkedList<Edge> oldEdgesList = graph.getEdgeSet();


        LinkedList<Edge> newEdgesList = (LinkedList<Edge>) oldEdgesList.clone();
        if (!newEdgesList.contains(e0)) {
            LOGGER.error("Graph do not contain this edge:" + e0.toString());
            return new Result(computeCore(graph), 0, "TraversalDelete");
        }

        //new Graph
        newEdgesList.remove(e0);
        Hashtable<Integer, LinkedList<Integer>> newAdjMap = GraphHandler.romveEdgeFromAdjMap(oldAdjMap, e0);
        Graph newGraph = new Graph(newAdjMap, newEdgesList);
        this.graph = newGraph;

        //coreMap from old Graph
        Hashtable<Integer, Integer> coreMap = computeCore(graph);
        //determine whether the node is new
        Integer v1_e0 = e0.getV1();
        Integer v2_e0 = e0.getV2();
        if (!newAdjMap.containsKey(v1_e0)) {
            coreMap.put(v1_e0, 1);
        }
        if (!newAdjMap.containsKey(v2_e0)) {
            coreMap.put(v2_e0, 1);
        }

        //mcdMap from new Graph
        Hashtable<Integer, Integer> mcdMap = computeMCD(newGraph, coreMap);

        //pcdMap
        Hashtable<Integer, Integer> pcdMap = computePCD(newGraph, coreMap, mcdMap);

        //main process
        LOGGER.info("Starting computing Traversal deleting edge:" + e0.toString());
        long startTime = System.currentTimeMillis();

        Hashtable<Integer, Boolean> visitedMap = new Hashtable<>();
        Hashtable<Integer, Boolean> evictedMap = new Hashtable<>();
        Hashtable<Integer, Integer> cdMap = new Hashtable<>();
        //lazy initial
        for (Integer node : newAdjMap.keySet()) {
            visitedMap.put(node, false);
            evictedMap.put(node, false);
            cdMap.put(node, 0);
        }
        Integer root_node = v1_e0;
        if (v2_e0 < v1_e0) {
            root_node = v2_e0;
        }
        int root_k = coreMap.get(root_node);
        cdMap.put(root_node, mcdMap.get(root_node));

        Stack<Integer> stack = new Stack<>();
        stack.push(root_node);
        visitedMap.put(root_node, true);
        while (!stack.isEmpty()) {
            Integer node_stack = stack.pop();
            if (cdMap.get(node_stack) < root_k) {
                LinkedList<Integer> neiList_node_stack = newAdjMap.get(node_stack);
                for (Integer w : neiList_node_stack) {
                    if (coreMap.get(w) == root_k && mcdMap.get(w) < root_k && !visitedMap.get(w)) {
                        stack.push(w);
                        visitedMap.put(w, true);
                        int cd = cdMap.get(w);
                        cdMap.put(w, cd + mcdMap.get(w));
                    }
                }
            } else {
                if (!evictedMap.get(node_stack)) {
                    propagateEviction(newAdjMap, coreMap, cdMap, evictedMap, root_k, node_stack);
                }
            }
        }

        for (Integer v : newAdjMap.keySet()) {
            if (visitedMap.get(v) && !evictedMap.get(v)) {
                int core_v = coreMap.get(v);
                coreMap.put(v, core_v - 1);
            }
        }
        long endTime = System.currentTimeMillis();

        return new Result(coreMap, System.currentTimeMillis() - startTime, "TraversalDelete");
    }


    /**
     * propagate eviction
     * @param adjMap
     * @param coreMap
     * @param cdMap
     * @param evictedMap
     * @param k
     * @param v
     */
    private void propagateEviction(Hashtable<Integer, LinkedList<Integer>> adjMap, Hashtable<Integer, Integer> coreMap, Hashtable<Integer, Integer> cdMap, Hashtable<Integer, Boolean> evictedMap, int k, Integer v) {
        evictedMap.put(v, true);
        LinkedList<Integer> neiList_v = adjMap.get(v);
        for (Integer w : neiList_v) {
            if (coreMap.get(w) == k) {
                int cd = cdMap.get(w) - 1;
                cdMap.put(w, cd);

                if (cd == k && !evictedMap.get(w)) {
                    propagateEviction(adjMap, coreMap, cdMap, evictedMap, k, w);
                }
            }
        }
    }

}
