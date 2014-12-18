import java.util.ArrayList;
import java.util.List;

public class SomeoneTree<T> {
    private Node<T> root;

    public SomeoneTree(T rootData) {
        root = new Node<T>();
        root.data = rootData;
        root.children = new ArrayList<Node<T>>();
    }

    public static class Node<T> {
        private T data;
        private Node<T> parent;
        private List<Node<T>> children;
    }
}