package me.arin;

import me.prettyprint.cassandra.dao.ExampleDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * User: Arin Sarkissian
 * Date: Mar 26, 2010
 * Time: 1:52:39 PM
 */
public class ThreadTestCase {
  public static long startMs = System.currentTimeMillis();
  public static int approxMinutes = -1;

  public static void main(String[] args) {
    int numThreads = 30;
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

    for (int i = 0; i < numThreads; i++) {
      executorService.execute(new HectorRunner());
    }

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(new HectorRunner("scheduled"), 0, 30, SECONDS);
  }

  private static class HectorRunner implements Runnable {
    private String type = "normal";

    private HectorRunner() {
    }

    public HectorRunner(String type) {
      this.type = type;
    }

    @Override
    public void run() {
      ExampleDao ed = new ExampleDao();

      while (true) {
        int elapsedSeconds = (int) ((System.currentTimeMillis() - ThreadTestCase.startMs) / 1000);
        int elapsedMInutes = elapsedSeconds / 60;
        if (elapsedMInutes > ThreadTestCase.approxMinutes) {
          System.out.println("TIME CHECK\nelapsed:\n\t" + elapsedMInutes + "(min)\n\t" + elapsedSeconds + " (sec)");
          ThreadTestCase.approxMinutes++;
        }

        try {
          ed.insert("hello", "string");
          System.out.println("\t" + System.currentTimeMillis() + "\t" + type + ": success");
        } catch (Exception e) {
          System.err.println("\t" + System.currentTimeMillis() + "\t" + type + ": failed");
          e.printStackTrace();
        }

        try {
          Thread.sleep(1000 * 2);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
