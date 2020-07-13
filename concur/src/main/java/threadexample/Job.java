package threadexample;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Job extends Thread {
    private final CountDownLatch latch;
    private Data data;
    private Semaphore semaphore;

    public Job(String threadName, Data data, CountDownLatch latch) {
        this.latch = latch;
        this.data = data;
        this.semaphore = semaphore;
        System.out.printf("%s created %n", currentThread());
    }

    @Override
    public void run() {
        System.out.printf("%s created %n", currentThread());
        synchronized (data) {   /** lock on the right heap data **/

            for (int i = 0; i < 5; i++) {
                data.setValue(data.getValue() + 1);
                System.out.printf("%d : %2d : %2d\n", Thread.currentThread().getId(), i, data.getValue());

                try {
                    Thread.sleep(100);
                    System.out.println();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                latch.countDown();      /** Notice the latch **/
            }
        }
    }
}


