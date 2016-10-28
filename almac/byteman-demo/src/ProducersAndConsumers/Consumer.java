package ProducersAndConsumers;

import ProducersAndConsumers.DemoGUI;

import java.util.ArrayList;

class Consumer implements Runnable {
    private final ArrayList queue;
    private int duration;
    private int numConsumed = 1;

    public Consumer(ArrayList<Integer> queue, int size, int speed) {
        this.queue = queue;
        this.duration = speed;
    }

    @Override
    public synchronized void run() {
        while (true) {
            while (queue.isEmpty()) {
                sleep();
            }
            removeItem();
            numConsumed++;
            if (numConsumed > 15) {
                Thread.currentThread().interrupt();
            } else {
                sleep();
            }
        }
    }

    private synchronized void removeItem() {
        queue.remove(0);
        DemoGUI.getInstance().appendText("CONSUMER has consumed item #" + (numConsumed) + " \n");
    }

    private void sleep() {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
