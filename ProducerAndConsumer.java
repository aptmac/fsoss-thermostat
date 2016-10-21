import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

public class ProducerAndConsumer {

    private int consumerSleep = 500;
    private int producerSleep = 500;

    ProducerAndConsumer(int producerSleep, int consumerSleep) {
        this.producerSleep = producerSleep;
        this.consumerSleep = consumerSleep;
    }

    void start() {
        ArrayList sharedQueue = new ArrayList<Integer>();
        int size = 3;
        Thread prodThread = new Thread(new Producer(sharedQueue, size, producerSleep), "Producer");
        Thread consThread = new Thread(new Consumer(sharedQueue, size, consumerSleep), "Consumer");
        prodThread.start();
        consThread.start();
        while (!consThread.isInterrupted()) {
            // wait until the consumer is interrupted
        }
        prodThread.interrupt();
        prodThread.stop();
        consThread.stop();
    }
}
