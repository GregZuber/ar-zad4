import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class TreeNode implements Serializable {
    private Integer nodeNumberInGraph;
    private LinkedList<Integer> pathFromRoot;
    private Double sumValue;
    private Double nodeValue;
    private LinkedList<TreeNode> children;
    
    public void addChild(TreeNode treeNode){
        this.children.add(treeNode);
    }
    public void addChildren(List<TreeNode> treeNodes){
        this.children.addAll(treeNodes);
    }
    public void addToPathFromRoot(Integer v){
        pathFromRoot.addLast(v);
    }
    public Integer getNodeNumberInGraph() {
        return nodeNumberInGraph;
    }
    public void setNodeNumberInGraph(Integer nodeNumberInGraph) {
        this.nodeNumberInGraph = nodeNumberInGraph;
    }
    public LinkedList<Integer> getPathFromRoot() {
        return pathFromRoot;
    }
    public void setPathFromRoot(LinkedList<Integer> pathFromRoot) {
        this.pathFromRoot = pathFromRoot;
    }
    public Double getSumValue() {
        return sumValue;
    }
    public void setSumValue(Double sumValue) {
        this.sumValue = sumValue;
    }
    public Double getNodeValue() {
        return nodeValue;
    }
    public void setNodeValue(Double nodeValue) {
        this.nodeValue = nodeValue;
    }
    public LinkedList<TreeNode> getChildren() {
        return children;
    }
    public void setChildren(LinkedList<TreeNode> children) {
        this.children = children;
    }
}
