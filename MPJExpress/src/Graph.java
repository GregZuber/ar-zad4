import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import mpi.Datatype;


public class Graph implements Serializable {

    private HashMap<Integer, HashMap<Integer, Double>> edges
        = new HashMap<Integer, HashMap<Integer,Double>>();
    
    private HashSet<Integer> verts = new HashSet<Integer>();
    
    public HashSet<Integer> getVerts() {
        return verts;
    }

    public void setVerts(HashSet<Integer> verts) {
        this.verts = verts;
    }

    public void addEdge (Integer v1, Integer v2, Double value){
        addEdgeToHashMap(v1, v2, value);
        addEdgeToHashMap(v2, v1, value);
        
        verts.add(v1);
        verts.add(v2);
        
    }
    
    public HashMap<Integer,Double> getEdges(Integer v){
        return edges.get(v);
    }
    
    private void addEdgeToHashMap(Integer v1, Integer v2, Double value){
        if (edges.containsKey(v1)){
            edges.get(v1).put(v2, value);
        } else {
            HashMap<Integer, Double> newEdge = new HashMap<Integer, Double>();
            newEdge.put(v2, value);
            edges.put(v1, newEdge);
        }
    }
    
    
    
}
