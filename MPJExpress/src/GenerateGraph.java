import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;


public class GenerateGraph {
    public static void main(String[] argv) throws FileNotFoundException, UnsupportedEncodingException{
        int sizeOfGraph = 16;
        
        Random random = new Random();
        
        PrintWriter writer = new PrintWriter("graph_16.txt", "UTF-8");
        
        for (int i = 0 ; i < sizeOfGraph; i++){
            for (int j = i ; j < sizeOfGraph; j++){
                writer.println(i + " " + j + " " + random.nextDouble() *10);
            }
        }
        
        writer.close();
    }
}
