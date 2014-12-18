import java.util.HashMap;
import java.util.LinkedList;

import mpi.MPI;


public class Worker {
    private Graph graph;

    public void findShortestPathInSubtree(){
        
    }
    
    public void run(int me){
        //Graph[] graphs = new Graph[1];
        
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
        //for (int i = 0 ; i < 3 ; i++){
        if (true){
//            System.out.println("================ TREE ================ " + me);
            TreeNode[] treeNodes = new TreeNode[1] ;
            MPI.COMM_WORLD.Recv(treeNodes, 0, 1, MPI.OBJECT, 0, 10);
            
//            System.out.println("Worker number " + me + " has sum value " + treeNodes[0].getSumValue());
            
            TreeNode treeNode = treeNodes[0];
            
            graph.getEdges(treeNode.getNodeNumberInGraph());
            
            //prepareChildren(treeNode);
            
            recursiveWatchTree(treeNode);
            

            
//            System.out.println("================ END OF TREE ================ " + me);
            
        }        
    }
    
    private void recursiveWatchTree(TreeNode node){
        
        prepareChildren(node);
        for (TreeNode child : node.getChildren()){
            
            if (child.getPathFromRoot().size() == graph.getVerts().size() + 1){
                foundBestSolution(child);
            }
            
            
            recursiveWatchTree(child);
        }
    }
    
    private void foundBestSolution(TreeNode node) {
        TreeNode[] nodes = new TreeNode[1];
        nodes[0] = node;
        
        MPI.COMM_WORLD.Send(nodes, 0, 1, MPI.OBJECT, 0, Main.BEST_SOLUTION_FIND);
    }

    private void prepareChildren(TreeNode parent){
        parent.setChildren(TreeUtil.addChildren(parent, graph));
        
        /*
        for(TreeNode child : parent.getChildren()){
            child.setNodeValue(0.0);
            LinkedList<Integer> path = (LinkedList) parent.getPathFromRoot().clone();
            path.add(0);
            child.setPathFromRoot(path);
            child.setNodeNumberInGraph(0);
            child.setSumValue(parent.getSumValue());
            //child.setChildren(TreeUtil.addChildren(root, graph.getEdges(root.getNodeNumberInGraph())));
        }
        */
    }
    

    private void receiveGraph() {
//        System.out.println("================ GRAPH ================");
        Graph[] graphs = new Graph[1];
        
        MPI.COMM_WORLD.Recv(graphs, 0, 1, MPI.OBJECT, 0, 9);
        
        graph = graphs[0];
        
        HashMap<Integer, Double> edgesFromV0 = graph.getEdges(0);

        for (Integer v : edgesFromV0.keySet()) {
//            System.out.println(v + " " + edgesFromV0.get(v));
        }        
        
        
//        System.out.println("================ END OF GRAPH ================");
    }
}
