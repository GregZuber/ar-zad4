

public class GraphInstance {
    
    //żeby uniknąć automatycznego tworzenia domyślnego, publicznego, bezargumentowego konstruktora
    private GraphInstance() {}
 
    private static class SingletonHolder { 
        private final static Graph instance = new Graph();
    }
 
    public static Graph getInstance() {
        return SingletonHolder.instance;
    }
}