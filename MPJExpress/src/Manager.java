import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import mpi.MPI;
import mpi.Status;

public class Manager {

    //private static final String SOURCE_FILE = "graph_10.txt";
    //private static final String SOURCE_FILE = "graph_16.txt";
    private static final String SOURCE_FILE = "p01.tsp";
    
    int numbersOfChildren;
    
    int numberOfGraphChildrenAssigned = 0;
    
    Graph graph = GraphInstance.getInstance();
    int numberOfThreads;
    private int workersThatEndsWork = 0;
    private double bestSolution = Double.MAX_VALUE;
    private TreeNode bestSolutionNode;
    public long startTime;
    TreeNode root;
    
    public void run(int numberOfThreads) throws IOException {
        
        this.numberOfThreads = numberOfThreads;

        //createMyLittleGraph();
        
        //readMatrixFile();
        
        //readFile();
        
        readTSPFile();
        
        startTime = System.currentTimeMillis();
        
        sendGraph();
        
        root = prepareRoot();
        
        sendRootChildrenToWorkers(root);
        
        boolean stayInLoop = true;
        while (stayInLoop){
            stayInLoop = checkForResponsesFromWorkers();
        }
        
    }
    
    private void readTSPFile() throws NumberFormatException, IOException{
        File file = new File(
                SOURCE_FILE);
        BufferedReader br;

        br = new BufferedReader(new FileReader(file));
        String line;
        
        HashMap<Integer,Double[]> valuesFromFile = new HashMap<Integer, Double[]>();
        
        while ((line = br.readLine()) != null) {
            String[] vertices = line.split(" ");
            Double[] coordinates = new Double[2];
            coordinates[0] = new Double(vertices[1]);
            coordinates[1] = new Double(vertices[2]);
            valuesFromFile.put(new Integer(vertices[0]), coordinates);
        }        
        
        addValuesFromFileToGraph(valuesFromFile);
        
        br.close();
    }
    
    private void addValuesFromFileToGraph(
            HashMap<Integer, Double[]> valuesFromFile) {
        for (Integer value : valuesFromFile.keySet()){
            boolean wasValueFound = false;
            for (Integer value2 : valuesFromFile.keySet()){
                if (value2.equals(value)){
                    wasValueFound = true;
                    continue;
                }
                
                if (wasValueFound){
                    double xDistanceSqr = Math.pow(valuesFromFile.get(value)[0] - valuesFromFile.get(value2)[0], 2.0);
                    double yDistanceSqr = Math.pow(valuesFromFile.get(value)[1] - valuesFromFile.get(value2)[1], 2.0);
                    double distance = Math.sqrt(xDistanceSqr + yDistanceSqr);
                    
                    graph.addEdge(value, value2, distance);
                }
            }
        }
    }

    private void readMatrixFile() throws NumberFormatException, IOException{
        File file = new File(
                SOURCE_FILE);
        BufferedReader br;

        br = new BufferedReader(new FileReader(file));
        String line;
        
        int i = 0;
        while ((line = br.readLine()) != null) {
            String[] vertices = line.split(" ");
            for (int j = i + 1 ; j < vertices.length ; j++){
                //System.out.println(i + " " + j + " " + vertices[j]);
                graph.addEdge(i, j, new Double(vertices[j]));
            }
            
            i++;
        }        
        
        br.close();
    }
    
    private void sendRootChildrenToWorkers(TreeNode root) {
        numbersOfChildren = root.getChildren().size();
        

        
        int threadNumber = 1;
        for ( ; threadNumber < numberOfThreads ; threadNumber++){
            
            TreeNode[] treeNodes = null;
            treeNodes = new TreeNode[1];
            treeNodes[0] = root.getChildren().get(numberOfGraphChildrenAssigned);
            

            
            numberOfGraphChildrenAssigned++;
            
           
            MPI.COMM_WORLD.Send(treeNodes, 0, 1, MPI.OBJECT, threadNumber, 10);
//            System.out.println("in root, i=" + i);
       }
        
    }
    
    /*
    private void sendRootChildrenToWorkers(TreeNode root) {
        int i = 0;
        
        int numbersOfChildren = root.getChildren().size();
        int numbersPerSubtreesForOneThread = numbersOfChildren / numberOfThreads;
        int restDividingThreads = numbersOfChildren / numberOfThreads;
        
        int threadNumber = 1;
        for ( ; threadNumber < numberOfThreads ; threadNumber++){
            
            TreeNode[] treeNodes = null;
            if (restDividingThreads > 0){
                treeNodes = new TreeNode[numbersPerSubtreesForOneThread + 1];
            } else {
                treeNodes = new TreeNode[numbersPerSubtreesForOneThread];
            }
            
            int j = 0; 
            for ( ; j < numbersPerSubtreesForOneThread ; j++){
                treeNodes[j] = root.getChildren().get(i);
                i++;
            }
            
            if (restDividingThreads > 0){
                treeNodes[j] = root.getChildren().get(i);
                i++;
                j++;
            }
            
            restDividingThreads--;
           
            MPI.COMM_WORLD.Send(treeNodes, 0, j, MPI.OBJECT, threadNumber, 10);
//            System.out.println("in root, i=" + i);
       }
        
    }
    */
    
    private boolean checkForResponsesFromWorkers() {
        TreeNode[] nodes = new TreeNode[1];
        Status status = MPI.COMM_WORLD.Recv(nodes, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MPI.ANY_TAG);
        TreeNode node = nodes[0];
        
        
        if (status.tag == Main.BEST_SOLUTION_FIND){
            if (node.getSumValue() < bestSolution){
                bestSolution = node.getSumValue();
                bestSolutionNode = node;
                //System.out.println( "path " + node.getPathFromRoot() + " sum value " + node.getSumValue() + " watek " + status.source);
                for (int i = 1 ; i < numberOfThreads ; i++ ){
                    //System.out.println("sent to " + i);
                    MPI.COMM_WORLD.Isend(nodes, 0, 1, MPI.OBJECT, i, Main.BEST_SOLUTION_SEND_TO_WORKER);
                }
                
            }

            
        } else if (status.tag == Main.WORKER_END_WORK_TAG){
            //System.out.println("otrzymałem end od " + status.source);
            if (numberOfGraphChildrenAssigned == numbersOfChildren){
                workersThatEndsWork++;
                
                TreeNode[] treeNodes = new TreeNode[1];
                treeNodes[0] = new TreeNode();
                
                //System.out.println("MANAGER WYSŁAŁ SYGNAŁ O ZAKOŃCZENIU");
                
                MPI.COMM_WORLD.Send(treeNodes, 0, 1, MPI.OBJECT, status.source, Main.SEND_TO_WORKER_END_SIGNAL);
            } else {
                TreeNode[] treeNodes = null;
                treeNodes = new TreeNode[1];
                treeNodes[0] = root.getChildren().get(numberOfGraphChildrenAssigned);
                

                
                numberOfGraphChildrenAssigned++;
                
                //System.out.println("numberOfGraphChildrenAssigned " + numberOfGraphChildrenAssigned);
                //System.out.println("numbersOfChildren " + numbersOfChildren);
                
               
                MPI.COMM_WORLD.Send(treeNodes, 0, 1, MPI.OBJECT, status.source, 10);
            }
        }
        
        if (workersThatEndsWork == numberOfThreads - 1){
            if (bestSolutionNode != null){
                System.out.println( "path " + bestSolutionNode.getPathFromRoot() + " sum value " + bestSolutionNode.getSumValue());
            }
            System.out.println("execution time: " + (System.currentTimeMillis() - startTime));
            return false;
        }
        
        return true;
        
        
    }

    private TreeNode prepareRoot() {
        TreeNode root = new TreeNode();
        root.setNodeValue(0.0);
        LinkedList<Integer> path = new LinkedList<Integer>();
        path.addLast(0);
        root.setPathFromRoot(path);
        int lowest = Integer.MAX_VALUE;
        for (Integer node : graph.getVerts()){
            if (node < lowest){
                lowest = node;
            }
        }
        root.setNodeNumberInGraph(lowest);
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
        
        /*
        int sizeOfGraph = 10;
        
        Random random = new Random();
        
        for (int i = 0 ; i < sizeOfGraph; i++){
            for (int j = i ; j < sizeOfGraph; j++){
                graph.addEdge(i, j, random.nextDouble() * 10);
                
            }
        }
        */
        
        graph.addEdge(0, 1, 3.0);
        graph.addEdge(0, 2, 1.0);
        graph.addEdge(0, 3, 2.0);
        graph.addEdge(0, 5, 1.0);
        graph.addEdge(1, 2, 2.0);
        graph.addEdge(1, 3, 2.5);
        graph.addEdge(2, 3, 4.0);
        graph.addEdge(2, 5, 4.0);
        graph.addEdge(3, 5, 4.0);
        
        //graph.addEdge(3, 6, 4.0);
        
    }
    
    private void printEdges(Integer vParam){
        HashMap<Integer, Double> edgesFromV0 = graph.getEdges(vParam);

        for (Integer v : edgesFromV0.keySet()) {
            System.out.println(v + " " + edgesFromV0.get(v));
        }        
    }
    
    private void readFile() throws NumberFormatException, IOException{
        File file = new File(
                SOURCE_FILE);
        BufferedReader br;

        br = new BufferedReader(new FileReader(file));
        String line;
        
        while ((line = br.readLine()) != null) {
            String[] vertices = line.split(" ");
            graph.addEdge(new Integer(vertices[0]), new Integer(vertices[1]), new Double(vertices[2]));
        }        
        
        br.close();
    }

}
