import java.util.HashMap;
import java.util.LinkedList;


public final class TreeUtil {
    private static final int NUMBER_OF_FIRST_VERT = 0;
    
    public static LinkedList<TreeNode> addChildren(TreeNode parent, Graph graph){
        HashMap<Integer,Double> childNodes = graph.getEdges(parent.getNodeNumberInGraph());
        LinkedList<TreeNode> children = new LinkedList<TreeNode>();
        
        for (Integer child : childNodes.keySet()){
            if (parent.getPathFromRoot().size() == graph.getVerts().size()
                    && child == NUMBER_OF_FIRST_VERT ){
                TreeNode treeNode = new TreeNode();
                
                treeNode.setNodeValue(childNodes.get(child));
                LinkedList<Integer> path = (LinkedList)parent.getPathFromRoot().clone();
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
            LinkedList<Integer> path = (LinkedList)parent.getPathFromRoot().clone();
            path.addLast(child);
            
            treeNode.setPathFromRoot(path);
            treeNode.setNodeNumberInGraph(child);
            treeNode.setSumValue(childNodes.get(child) + parent.getSumValue());
            children.add(treeNode);
        }
        
        
        return children;
    }
}
