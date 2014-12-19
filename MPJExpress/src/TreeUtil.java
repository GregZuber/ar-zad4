import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public final class TreeUtil {
    private static final int NUMBER_OF_FIRST_VERT = 0;
    
    public static List<Integer> addChildren(Integer numberInGraph, Graph graph, List<Integer> path){
        HashMap<Integer,Double> childNodes = graph.getEdges(numberInGraph);
        
        List<Integer> toReturn = new LinkedList<Integer>();
        //System.out.println("Before FOR ========================= " + parent.getPathFromRoot());
        
        for (Integer child : childNodes.keySet()){
            if (path.size() == graph.getVerts().size()
                    && child == NUMBER_OF_FIRST_VERT ){
                
                toReturn.add(child);
                
                break;
            }
            else if (path.contains(child)){
                continue;
            }
            
            toReturn.add(child);
            
            //System.out.println("Inside FOR ========================= " + treeNode.getPathFromRoot());
        }
        
        
        return toReturn;
    }
    
    public static LinkedList<TreeNode> addChildren(TreeNode parent, Graph graph){
        HashMap<Integer,Double> childNodes = graph.getEdges(parent.getNodeNumberInGraph());
        LinkedList<TreeNode> children = new LinkedList<TreeNode>();
        
        //System.out.println("Before FOR ========================= " + parent.getPathFromRoot());
        
        for (Integer child : childNodes.keySet()){
            if (parent.getPathFromRoot().size() == graph.getVerts().size()
                    && child == NUMBER_OF_FIRST_VERT ){
                TreeNode treeNode = new TreeNode();
                
                
                
                treeNode.setNodeValue(childNodes.get(child));
                LinkedList<Integer> path = (LinkedList)parent.getPathFromRoot();
                path.addLast(child);
                
                treeNode.setPathFromRoot(path);
                treeNode.setNodeNumberInGraph(child);
                treeNode.setSumValue(childNodes.get(child) + parent.getSumValue());
                children.add(treeNode);
                
                
                
                break;
            }
            else if (parent.getPathFromRoot().contains(child)){
                continue;
            }
            TreeNode treeNode = new TreeNode();
            
            treeNode.setNodeValue(childNodes.get(child));
            
            
            
            List<Integer> path = new LinkedList<Integer>();
            for (Integer nodeInt : parent.getPathFromRoot()){
                path.add(nodeInt);
            }
            
            path.add(child);
            
            treeNode.setPathFromRoot(path);
            treeNode.setNodeNumberInGraph(child);
            treeNode.setSumValue(childNodes.get(child) + parent.getSumValue());
            children.add(treeNode);
            
            //System.out.println("Inside FOR ========================= " + treeNode.getPathFromRoot());
        }
        
        
        return children;
    }
}
