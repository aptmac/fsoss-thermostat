import java.util.ArrayList;

public class ProducerAndConsumer {

    private int consumerSleep;
    private int producerSleep;

    ProducerAndConsumer(int producerSleep, int consumerSleep) {
        this.producerSleep = producerSleep;
        this.consumerSleep = consumerSleep;
        start();
    }

    void start() {
        ArrayList sharedQueue = new ArrayList<Integer>();
        int size = 5;
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
