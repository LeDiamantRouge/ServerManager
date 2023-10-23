package fr.lediamantrouge.servermanager.scheduler;

import lombok.Getter;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@Getter
public class JavaScheduler implements ISchedulerAdapter {
    private static final int PARALLELISM = 16;

    private final ScheduledThreadPoolExecutor scheduler;
    private final ForkJoinPool worker;
    private final ForkJoinPool mainWorker;

    public JavaScheduler() {
        this.scheduler = new ScheduledThreadPoolExecutor(1, r -> {
            final Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setName("plugin-scheduler");
            return thread;
        });
        this.scheduler.setRemoveOnCancelPolicy(true);
        this.scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        this.worker = new ForkJoinPool(PARALLELISM, new WorkerThreadFactory(), new ExceptionHandler(), false);
        this.mainWorker = ForkJoinPool.commonPool();
    }

    @Override
    public Executor async() {
        return this.worker;
    }

    public Executor sync() {
        return mainWorker;
    }

    @Override
    public ISchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit) {
        final ScheduledFuture<?> future = this.scheduler.schedule(() -> this.worker.execute(task), delay, unit);
        return () -> future.cancel(false);
    }

    @Override
    public ISchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
        final ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(() -> this.worker.execute(task), interval, interval, unit);
        return () -> future.cancel(false);
    }

    @Override
    public ISchedulerTask syncLater(Runnable task, long delay, TimeUnit unit) {
        final ScheduledFuture<?> future = this.scheduler.schedule(() -> this.worker.execute(task), delay, unit);
        return () -> future.cancel(false);
    }

    @Override
    public ISchedulerTask syncRepeating(Runnable task, long interval, TimeUnit unit) {
        final ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(() -> this.worker.execute(task), interval, interval, unit);
        return () -> future.cancel(false);
    }

    @Override
    public void shutdownScheduler() {
        this.scheduler.shutdown();
        try {
            if (!this.scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                reportRunningTasks(thread -> thread.getName().equals("plugin-scheduler"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdownExecutor() {
        this.worker.shutdown();
        try {
            if (!this.worker.awaitTermination(1, TimeUnit.MINUTES)) {
                reportRunningTasks(thread -> thread.getName().startsWith("plugin-worker-"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void reportRunningTasks(Predicate<Thread> predicate) {
        Thread.getAllStackTraces().forEach((thread, stack) -> {
            if (predicate.test(thread)) {
                //ERROR
            }
        });
    }

    private static final class WorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        private static final AtomicInteger COUNT = new AtomicInteger(0);

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            final ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            thread.setDaemon(true);
            thread.setName("plugin-worker-" + COUNT.getAndIncrement());
            return thread;
        }
    }

    private static final class ExceptionHandler implements UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {

        }
    }

}