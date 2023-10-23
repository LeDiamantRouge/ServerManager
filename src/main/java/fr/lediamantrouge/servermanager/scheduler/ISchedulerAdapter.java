package fr.lediamantrouge.servermanager.scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public interface ISchedulerAdapter {

    Executor async();

    Executor sync();

    default void executeAsync(Runnable task) {
        async().execute(task);
    }

    default void executeSync(Runnable task) {
        sync().execute(task);
    }

    ISchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit);

    ISchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit);

    ISchedulerTask syncLater(Runnable task, long delay, TimeUnit unit);

    ISchedulerTask syncRepeating(Runnable task, long interval, TimeUnit unit);

    void shutdownScheduler();

    void shutdownExecutor();

}