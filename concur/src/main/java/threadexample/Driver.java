package threadexample;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.*;

public class Driver {
    public static int total;

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Driver driver = new Driver();

        /** Shared data in heap **/
        Data data = new Data(1000);

        /** Number of threads **/
        int NTHREAD = 4;
        ExecutorService executorService;
        CountDownLatch countDownLatch;
        Semaphore semaphore;



        /*********************************
         * Job uses a synchronized block *
         *********************************/

        /** Thread Pool and latch **/
        executorService = Executors.newFixedThreadPool(NTHREAD);
        countDownLatch = new CountDownLatch(NTHREAD);

        /** Instantiate the objects that need to run in separate threads **/
        ArrayList<Job> jobs = new ArrayList<>();
        for (int i=0; i<8; i++) {
            Job job = new Job("Thread-"+i, data, countDownLatch);
            jobs.add(job);
        }

        /** Run the objects **/
        for (Job job : jobs) {
            executorService.execute(job);
            job.currentThread();
        }

        /** Await and Shutdown **/
        countDownLatch.await();
        executorService.shutdown();


        /*************************
         * Job2 uses a semaphore *
         *************************/

        /** Thread Pool and latch **/
         executorService = Executors.newFixedThreadPool(NTHREAD);
         countDownLatch = new CountDownLatch(NTHREAD);

         semaphore = new Semaphore(1);

        /** Instantiate the objects that need to run in separate threads **/
        ArrayList<Job2> jobs2 = new ArrayList<>();
        for (int i=0; i<NTHREAD; i++) {
            Job2 job2 = new Job2("Thread-"+i, data, countDownLatch, semaphore);
            jobs2.add(job2);
        }

        /** Run the objects **/
        for (Job2 job : jobs2) {
            executorService.execute(job);
        }

        /** Await and Shutdown **/
        countDownLatch.await();
        executorService.shutdown();




        /**************************************
         * With Callback to get final results *
         **************************************/
        /** Thread Pool and latch **/
        executorService = Executors.newFixedThreadPool(NTHREAD);
        countDownLatch = new CountDownLatch(NTHREAD);
        semaphore = new Semaphore(1);

        ArrayList<Output> alOutput = new ArrayList<>();

        Callback callback = new Callback() {
            int total = 0;
            @Override
            public void result(long id, int value) {
                Output output = new Output();
                output.setId(id);
                output.setValue(value);
                alOutput.add(output);
                System.out.printf("### %d %d\n", output.getId(), output.getValue());
                total = total + output.getValue();
                this.total = total;
            }
        };

        ArrayList<Job3> jobs3 = new ArrayList<>();

        /** Instantiate the objects that need to run in separate threads **/
        for (int i = 0; i<NTHREAD; i++) {
            Job3 job = new Job3("Thread-"+i, data, countDownLatch, semaphore, callback);
            jobs3.add(job);
        }

        Iterator<Job3> iterator = jobs3.iterator();
        while (iterator.hasNext()) {
            Job3 job = iterator.next();
            executorService.execute(job);
        }

//        System.out.println(jobs3.get(0).getCallback().result());

        /** Await and Shutdown **/
        countDownLatch.await();
        executorService.shutdown();

        System.out.printf("Total=%d",Driver.total);







        /****************
         * Using Future *
         ****************/

        /** Thread Pool and latch **/
        executorService = Executors.newFixedThreadPool(NTHREAD);
        countDownLatch = new CountDownLatch(NTHREAD);
        semaphore = new Semaphore(1);

        /** Instantiate the objects that need to run in separate threads **/
        ArrayList<Callable<Job4>> jobs4 = new ArrayList<>();
        for (int i=0; i<4; i++) {
            Callable job = new Job4("Thread-"+i, data, countDownLatch, semaphore);
            jobs4.add(job);
        }

        /** Future **/
        FutureTask[] futureTasks = new FutureTask[4];

        /** Run the objects **/
        int i = 0;

        for (Callable<Job4> job : jobs4) {
            futureTasks[i] = new FutureTask(job);
            Thread t = new Thread(futureTasks[i]);
            t.start();
            i++;
        }

        int total = 0;
        for (int k = 0; k < 4; k++)
        {
            // As it implements Future, we can call get()
            // This method blocks till the result is obtained
            // The get method can throw checked exceptions
            // like when it is interrupted. This is the reason
            // for adding the throws clause to main
            Data d = (Data)futureTasks[k].get();
            total = total + (d.getValue());
            System.out.println("###" + total);
        }

        /** Await and Shutdown **/
        countDownLatch.await();
        executorService.shutdown();
        System.out.println("All Future tasks completed");

    }


}
