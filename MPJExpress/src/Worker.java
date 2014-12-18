import java.util.HashMap;
import java.util.LinkedList;

import mpi.MPI;
import mpi.Request;
import mpi.Status;


public class Worker {
    private Graph graph;
    private double shortestPath = Double.MAX_VALUE;
    private int me;
    
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
            
            for (int i = 0 ; i < status.count ; i++){
                TreeNode treeNode = treeNodes[i];
                
                graph.getEdges(treeNode.getNodeNumberInGraph());
                
                recursiveWatchTree(treeNode);
            }
            
            
        }        
    }
    
    private void recursiveWatchTree(TreeNode node){
        
        checkForBestSolution();
        
        //System.out.println("PATH ========================= " + node.getPathFromRoot());
        
        
        if (node.getSumValue() >= shortestPath){
            //System.out.println("Odcinam drzewo watek " + me + " na nodzie: " + node.getPathFromRoot());
            return;
        }
        
        
        if (node.getPathFromRoot().size() > graph.getVerts().size()){
            //System.out.println("KONCZE" + node.getPathFromRoot());
            return;
        }
        
        
        
        prepareChildren(node);
        for (TreeNode child : node.getChildren()){
            
            if (child.getPathFromRoot().size() == graph.getVerts().size() + 1){
                foundBestSolution(child);
            }
            
            
            recursiveWatchTree(child);
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

    private void foundBestSolution(TreeNode node) {
        TreeNode[] nodes = new TreeNode[1];
        nodes[0] = node;
        
        MPI.COMM_WORLD.Send(nodes, 0, 1, MPI.OBJECT, 0, Main.BEST_SOLUTION_FIND);
    }

    private void prepareChildren(TreeNode parent){
        parent.setChildren(TreeUtil.addChildren(parent, graph));
    }
    

    private void receiveGraph() {
        Graph[] graphs = new Graph[1];
        
        MPI.COMM_WORLD.Recv(graphs, 0, 1, MPI.OBJECT, 0, 9);
        
        graph = graphs[0];
        
        HashMap<Integer, Double> edgesFromV0 = graph.getEdges(0);

        for (Integer v : edgesFromV0.keySet()) {
//            System.out.println(v + " " + edgesFromV0.get(v));
        }        
        
        
    }
}
