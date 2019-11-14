package util;

import java.util.Hashtable;
import java.util.TreeSet;

public class Graph implements Cloneable{
    private Hashtable<Integer, TreeSet<Integer>> adjMap;

    public Graph(Hashtable<Integer, TreeSet<Integer>> adjMap) {
        this.adjMap = adjMap;
    }

    @Override
    public String toString() {
        return "Graph{"  + adjMap.toString() + '}';
    }

    @Override
    public Graph clone(){
        Hashtable<Integer, TreeSet<Integer>> newAdjMap = new Hashtable<>();
        for (int i : adjMap.keySet()) {
            TreeSet<Integer> adjList = (TreeSet<Integer>) adjMap.get(i).clone();
            newAdjMap.put(i, adjList);
        }
        return new Graph(newAdjMap);
    }

    /**
     * Getter() and Setter()
     */
    public Hashtable<Integer, TreeSet<Integer>> getAdjMap() {
        return adjMap;
    }

    public void setAdjMap(Hashtable<Integer, TreeSet<Integer>> adjMap) {
        this.adjMap = adjMap;
    }

}
