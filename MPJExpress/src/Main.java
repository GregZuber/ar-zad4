import mpi.*;

public class Main {
    public static final int BEST_SOLUTION_FIND = 50;
    public static final int WORKER_END_WORK_TAG = 100;
    
    public static void main(String args[]) throws Exception {
        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        Manager manager;
        
        //System.out.println("Hi from <" + me + ">");
        
        if (me == 0){
            manager = new Manager();
            manager.run(size);
            //int[] send = new int[1];
            //send[0] = 123;
            //MPI.COMM_WORLD.Send(send, 0, 1, MPI.INT, 1, 10);
        } else {
            Worker worker = new Worker();
            worker.run(me);
            //int[] recv = new int[1];
            //MPI.COMM_WORLD.Recv(recv, 0, 1, MPI.INT, 0, 10);
            //System.out.println(recv[0]);
        }
        
        MPI.Finalize();
    }
}