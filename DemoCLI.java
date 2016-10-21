import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

public class DemoCLI {

    public static void main(String args[]) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Press enter to start the program..");
        try {
            String input = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList sharedQueue = new ArrayList<Integer>();
        int size = 3;
        Thread producer = new Thread(new Producer(sharedQueue, size, 700), "Producer");
        Thread consumer = new Thread(new Consumer(sharedQueue, size, 500), "Consumer");
        producer.start();
        consumer.start();
        while (!consumer.isInterrupted()) {
            // wait until consumer has used 15 items
        }
        producer.interrupt();
        producer.stop();
        consumer.stop();
        while(true) {

        }
    }
}
