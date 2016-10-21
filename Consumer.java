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
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                removeItem();
            }
            sleep();
            if (numConsumed == 15) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void removeItem() {
        numConsumed++;
        System.out.printf("CONSUMER has consumed item #%d: %d\n", numConsumed, (Integer) queue.remove(0));
        queue.notifyAll();
    }

    private void sleep() {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
