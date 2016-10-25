package ProducersAndConsumers;

import ProducersAndConsumers.DemoGUI;

import java.util.ArrayList;

class Consumer implements Runnable {
    private final ArrayList queue;
    private int duration;
    private int numConsumed = 0;

    public Consumer(ArrayList<Integer> queue, int size, int speed) {
        this.queue = queue;
        this.duration = speed;
    }

    @Override
    public void run() {
        while (true) {
            //synchronized (queue) {
                while (queue.isEmpty()) {
                    /*
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    */
                    System.out.println("empty");
                    sleep();
                }
                removeItem();
            if (numConsumed == 15) {
                Thread.currentThread().interrupt();
                //break;
            }
        }
        //}
    }

    private synchronized void removeItem() {
        numConsumed++;
        queue.remove(0);
        //queue.notifyAll();
        DemoGUI.getInstance().appendText("CONSUMER has consumed item #" + numConsumed + " \n");
    }

    private void sleep() {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
