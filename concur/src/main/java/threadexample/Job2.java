/** https://www.geeksforgeeks.org/countdownlatch-in-java/
 */
package threadexample;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class Job2 implements Runnable { /** Must implement Runnable to be thread **/
    private final CountDownLatch latch;
    private Data data;
    private Semaphore semaphore;

    /** Notice the shared heap data and the latch is passed **/
    public Job2(String threadName, Data data, CountDownLatch latch, Semaphore semaphore) {
        this.latch = latch;
        this.data = data;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {         /** run() must be overridden **/
        System.out.println("Executing Job2 in thread");

        try {
            semaphore.acquire();

            for (int i = 0; i < 5; i++) {
                data.setValue(data.getValue() + 1);
                System.out.printf("%d : %2d : %2d\n", Thread.currentThread().getId(), i, data.getValue());

                try {
                    Thread.sleep(10);
                    System.out.println();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            semaphore.release();
        }

        latch.countDown();      /** Notice the latch **/

    }
}
