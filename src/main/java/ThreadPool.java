import java.util.Queue;

public interface ThreadPool {
    void start();

    void execute(Runnable runnable);

    Queue<Runnable> getQueueOfTasks();
}
