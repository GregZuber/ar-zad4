import mpi.*;

public class Main {
    public static final int BEST_SOLUTION_FIND = 50;
    public static final int BEST_SOLUTION_SEND_TO_WORKER = 150;
    public static final int WORKER_END_WORK_TAG = 100;
    public static final int SEND_TO_WORKER_END_SIGNAL = 520;
    
    
    public static void main(String args[]) throws Exception {
        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        Manager manager;
        
        if (me == 0){
            manager = new Manager();
            manager.run(size);
        } else {
            Worker worker = new Worker();
            worker.run(me);
        }
        
        MPI.Finalize();
    }
}