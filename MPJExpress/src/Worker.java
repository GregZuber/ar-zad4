import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mpi.MPI;
import mpi.Request;
import mpi.Status;


public class Worker {
    private Graph graph;
    private double shortestPath = Double.MAX_VALUE;
    private int me;
    private int globalCountToCheck = 0;
    
    public void run(int me){
        //Graph[] graphs = new Graph[1];
        
        this.me = me;
        
        receiveGraph();
        
        receiveTrees(me);
        
        sendEndSignal();
        
    }
    
    

    private void sendEndSignal() {
        TreeNode[] endWork = new TreeNode[1];
        endWork [0] = new TreeNode();
        MPI.COMM_WORLD.Send(endWork, 0, 1, MPI.OBJECT, 0, Main.WORKER_END_WORK_TAG);        
    }

    private void receiveTrees(int me) {

        if (true){
            
            
            Status status = null;
            while (status == null){
                status = MPI.COMM_WORLD.Probe(0, 10);
            }
            
            TreeNode[] treeNodes = new TreeNode[status.count] ;
            
            MPI.COMM_WORLD.Recv(treeNodes, 0, status.count, MPI.OBJECT, 0, 10);
            
            List<TreeNode> nodeList = new LinkedList<TreeNode>();
            for (int i = 0 ; i < status.count ; i++){
                nodeList.add(treeNodes[i]);
            }
            Collections.sort(nodeList, new CustomComparator());
            
            
            
            
            for (TreeNode node : nodeList){
                //System.out.println(node.getSumValue());
                
                graph.getEdges(node.getNodeNumberInGraph());
                
                recursiveWatchTree(node.getNodeNumberInGraph(), node.getPathFromRoot()  , node.getSumValue());
            }
            
            
        }        
    }
    
    private class CustomComparator implements Comparator<TreeNode> {
        @Override
        public int compare(TreeNode arg0, TreeNode arg1) {
            if (arg0.getSumValue() < arg1.getSumValue()){
                return -1;
            } else {
                return 1;
            }
        }
    }
    
    private class CustomComparatorInt implements Comparator<Integer> {
        private int parent;
        
        public CustomComparatorInt(Integer numberInGraph) {
            this.parent = numberInGraph;
        }

        @Override
        public int compare(Integer arg0, Integer arg1) {
            if (graph.getEdges(arg0).get(parent) < graph.getEdges(arg1).get(parent)){
                return -1;
            } else {
                return 1;
            }
        }
    }
    
    private void recursiveWatchTree(Integer numberInGraph, List<Integer> path, double sum){
        
        /*
        globalCountToCheck++;
        if (globalCountToCheck % 1000 == 0){
            checkForBestSolution();
        }
        */
        
        
        checkForBestSolution();
        

        
        //System.out.println("PATH ========================= " + node.getPathFromRoot());
        
        
        if (sum >= shortestPath){
            //System.out.println("Odcinam drzewo watek " + me + " na nodzie: " + node.getPathFromRoot());
            return;
        }
        
        
        if ( path.size() > graph.getVerts().size()){
            //System.out.println("KONCZE" + node.getPathFromRoot());
            return;
        }
        

        
        List<Integer> children = prepareChildren(numberInGraph, path);
        
        Collections.sort(children, new CustomComparatorInt(numberInGraph));
        
        /*
        System.out.println("SUMaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa ");
        for (TreeNode child : nodeList){
            System.out.println("SUM "  + child.getSumValue());
        }
        */
        
        for (Integer intChild : children){
            
            globalCountToCheck++;
            if (globalCountToCheck % 1000000 == 0){
                //System.out.println("NODE "  + path.toString());
            }
            
            //System.out.println("NODE "  + path.toString());
            
            //sum += graph.getEdges(intChild).get(numberInGraph);
            
            List<Integer> listToReturn = new LinkedList<Integer>();
            
            for (Integer integer : path){
                listToReturn.add(integer);
            }
            
            listToReturn.add(intChild);
            
            if (listToReturn.size() == graph.getVerts().size() + 1){
                foundBestSolution(listToReturn, sum + graph.getEdges(intChild).get(numberInGraph));
            }
            
            
            recursiveWatchTree(intChild, listToReturn, sum + graph.getEdges(intChild).get(numberInGraph));
        }
    }
    
    private void checkForBestSolution() {
        TreeNode[] treeNodes = new TreeNode[1];
        
        Status status = MPI.COMM_WORLD.Iprobe(0, Main.BEST_SOLUTION_SEND_TO_WORKER);
        
        if (status != null){
//            System.out.println("TAKKKKKKKKKKKKKKKKK");
            status = MPI.COMM_WORLD.Recv(treeNodes, 0, 1, MPI.OBJECT, 0, Main.BEST_SOLUTION_SEND_TO_WORKER);
            
            TreeNode treeNode = treeNodes[0];

//            System.out.println("receive request watek " + me + " treeNode " + treeNode + " worker " + status);
            
            if (treeNode != null){
                shortestPath = treeNode.getSumValue();
                
                //System.out.println("===========THREAD" + me +  "=============SHORTEST PATH " + shortestPath);
            }
        }
        

        
    }

    private void foundBestSolution(List<Integer> path, double sum) {
        if (sum < shortestPath ){
            TreeNode[] nodes = new TreeNode[1];
            TreeNode node = new TreeNode();
            node.setSumValue(sum);
            node.setPathFromRoot(path);
            nodes[0] = node;
            
            
            MPI.COMM_WORLD.Send(nodes, 0, 1, MPI.OBJECT, 0, Main.BEST_SOLUTION_FIND);
        }

    }

    private List<Integer> prepareChildren(Integer numberInGraph, List<Integer> path){
        return TreeUtil.addChildren(numberInGraph, graph, path);
    }
    

    private void receiveGraph() {
        Graph[] graphs = new Graph[1];
        
        MPI.COMM_WORLD.Recv(graphs, 0, 1, MPI.OBJECT, 0, 9);
        
        graph = graphs[0];
        
        /*
        HashMap<Integer, Double> edgesFromV0 = graph.getEdges(0);

        for (Integer v : edgesFromV0.keySet()) {
//            System.out.println(v + " " + edgesFromV0.get(v));
        }        
        
        */
        
    }
}
