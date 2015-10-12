package meteocontroller.photo.uploader;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import meteocontroller.MeteoLogger;

/**
 *
 * @author Jan Novak <novano@mail.muni.cz>
 */
public class ThreadMonitor implements Runnable {

    private Map<Thread, Date> runningThreads = new HashMap<>();
    private long timeout = 10_000L;
    private Queue<Runnable> queue = new LinkedList<>();

    public synchronized void runThread(Runnable runnable) {
        if (runningThreads.size() > 0) {
            queue.add(runnable);
        } else {
            startRunnable(runnable);
        }
    }

    @Override
    public void run() {
        while (true) {
            checkThreads();
            processQueue();
            try {
                Thread.sleep(1_000L);
            } catch (InterruptedException ex) {
                MeteoLogger.log(ex);
            }
        }
    }

    private synchronized void checkThreads() {
        Set<Thread> remove = new HashSet<>();
        for (Map.Entry<Thread, Date> entrySet : runningThreads.entrySet()) {
            Thread thread = entrySet.getKey();
            Date started = entrySet.getValue();
            if (!thread.isAlive()) {
                remove.add(thread);
            } else if (new Date(System.currentTimeMillis() - timeout).after(started)) {
                thread.interrupt();
                remove.add(thread);
            }
        }

        for (Thread removeThread : remove) {
            runningThreads.remove(removeThread);
        }
    }

    private synchronized void processQueue() {
        if (runningThreads.size() == 0) {
            Runnable poll = queue.poll();
            startRunnable(poll);
        }
    }

    private void startRunnable(Runnable runnable) {
        Thread thread = new Thread(runnable);
        runningThreads.put(thread, new Date());
        thread.start();
    }

}
