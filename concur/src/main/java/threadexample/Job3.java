/** https://www.geeksforgeeks.org/countdownlatch-in-java/
 */
package threadexample;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class Job3 implements Runnable { /** Must implement Runnable to be thread **/
    private final CountDownLatch latch;
    private Data data;
    private Semaphore semaphore;
    private Callback callback;
    private Driver driver;

    public Callback getCallback() {
        return callback;
    }

    public Data getData() {
        return data;
    }

    /** Notice the shared heap data and the latch is passed **/
    public Job3(String threadName, Data data, CountDownLatch latch, Semaphore semaphore, Callback callback) {
        this.data = data;
        this.latch = latch;
        this.semaphore = semaphore;
        this.callback = callback;
    }

    @Override
    public void run() {         /** run() must be overridden **/
        System.out.println("Executing Job3 in thread");
        int tid = (int) Thread.currentThread().getId();

        try {
            semaphore.acquire();

            for (int i = 0; i < 5; i++) {
                System.out.printf("%d : %2d : %2d\n", Thread.currentThread().getId(), i, data.getValue());
                data.setValue(data.getValue() + 1);

                try {
                    Thread.sleep(10);
                    System.out.println();
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Finished");
            callback.result(tid,data.getValue());

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            semaphore.release();
        }


        latch.countDown();      /** Notice the latch **/

        callback.result(tid,1000 );
    }
}
