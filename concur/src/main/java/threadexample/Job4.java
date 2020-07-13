/** https://www.geeksforgeeks.org/countdownlatch-in-java/
 */
package threadexample;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class Job4 implements Callable { /** Must implement Runnable to be thread **/
    private final CountDownLatch latch;
    private final Semaphore semaphore;
    private Data data;
    private String threadName;

    /** Notice the shared heap data and the latch is passed **/
    public Job4(String threadName, Data data, CountDownLatch latch, Semaphore semaphore) {
        this.latch = latch;
        this.semaphore = semaphore;
        this.data = data;
        this.threadName = threadName;
    }

    @Override
    public Object call() throws Exception {         /** run() must be overridden **/
        System.out.println("Executing Job4 in thread");

        try {
            semaphore.acquire();
            for (int i = 0; i < 4; i++) {
                data.setValue(data.getValue() + 1);
                System.out.printf("%d : %2d : %2d\n", Thread.currentThread().getId(), i, data.getValue());

                try {
                    Thread.sleep(0);
                    System.out.println();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            latch.countDown();      /** Notice the latch **/
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            semaphore.release();
        }
        latch.countDown();

        System.out.println(threadName + " completed");
        return data;
    }
}
