package ProducersAndConsumers;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

class Producer implements Runnable {
    private final ArrayList queue;
    private final int MAX;
    private int duration;

    public Producer(ArrayList<Integer> sharedQueue, int size, int speed) {
        this.queue = sharedQueue;
        this.duration = speed;
        this.MAX = size;
    }

    @Override
    public void run() {
        while (true) {
            while (queue.size() == MAX) {
                sleep();
            }
            addItem();
        }
    }

    private synchronized void addItem() {
        int i = ThreadLocalRandom.current().nextInt(0, 3 + 1);
        queue.add(i);
        sleep();
    }

    private void sleep() {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
