import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import mpi.MPI;
import mpi.Status;

public class Manager {

    Graph graph = GraphInstance.getInstance();
    int numberOfThreads;
    private int workersThatEndsWork = 0;
    public void run(int numberOfThreads) throws IOException {
        
        this.numberOfThreads = numberOfThreads;

        createMyLittleGraph();
        sendGraph();
        
        TreeNode root = prepareRoot();
        sendRootChildrenToWorkers(root);
        
        while (true){
            checkForResponsesFromWorkers();
        }
        
    }
    
    private void sendRootChildrenToWorkers(TreeNode root) {
        TreeNode[] treeNodes = new TreeNode[1];//[root.getChildren().size()] ;
        int i = 0;
        for (TreeNode child : root.getChildren()){
            i++;
            treeNodes[0] = child;
           
            MPI.COMM_WORLD.Send(treeNodes, 0, 1, MPI.OBJECT, i % numberOfThreads, 10);
//            System.out.println("in root, i=" + i);
        }
        
    }
    
    private void checkForResponsesFromWorkers() {
        TreeNode[] nodes = new TreeNode[1];
        Status status = MPI.COMM_WORLD.Recv(nodes, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MPI.ANY_TAG);
        TreeNode node = nodes[0];
        
        
        if (status.tag == Main.BEST_SOLUTION_FIND){
            System.out.println( "path " + node.getPathFromRoot() + " sum value " + node.getSumValue());
        } else if (status.tag == Main.WORKER_END_WORK_TAG){
            workersThatEndsWork ++;
        }
        
        if (workersThatEndsWork == numberOfThreads - 1){
            System.exit(0);
        }
        
        
        //MPI.COMM_WORLD.Recv(nodes, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, Main.BEST_SOLUTION_FIND);
        
        
    }

    private TreeNode prepareRoot() {
        TreeNode root = new TreeNode();
        root.setNodeValue(0.0);
        LinkedList<Integer> path = new LinkedList<Integer>();
        path.addLast(0);
        root.setPathFromRoot(path);
        root.setNodeNumberInGraph(0);
        root.setSumValue(0.0);
        root.setChildren(TreeUtil.addChildren(root, graph));
        
        return root;
    }

    private void sendGraph() {
        Graph[] graphs = new Graph[1];
        graphs[0] = graph;
        
        for (int i = 1 ; i < numberOfThreads ; i++){
            MPI.COMM_WORLD.Send(graphs, 0, 1, MPI.OBJECT, i, 9);
        }
    }

    private void createMyLittleGraph(){
        graph.addEdge(0, 1, 3.0);
        graph.addEdge(0, 2, 1.0);
        graph.addEdge(0, 3, 2.0);
        graph.addEdge(1, 2, 2.0);
        graph.addEdge(1, 3, 2.5);
        graph.addEdge(2, 3, 4.0);
        
    }
    
    private void printEdges(Integer vParam){
        HashMap<Integer, Double> edgesFromV0 = graph.getEdges(vParam);

        for (Integer v : edgesFromV0.keySet()) {
            System.out.println(v + " " + edgesFromV0.get(v));
        }        
    }
    
    private void readFile() throws NumberFormatException, IOException{
        File file = new File(
                "/home/g/workspace/praca/angular/MPJExpress/graphs/city100.tsp");
        BufferedReader br;

        br = new BufferedReader(new FileReader(file));
        String line;
        
        boolean firstLine = true;
        
        Integer numberOfVertices = null;
        
        while ((line = br.readLine()) != null) {
            if (firstLine){
                numberOfVertices = new Integer(line);
                firstLine = false;
            } else {
                String[] vertices = line.split(" ");
                graph.addEdge(new Integer(vertices[0]), new Integer(vertices[1]), 1.0);
            }
        }        
    }

}
