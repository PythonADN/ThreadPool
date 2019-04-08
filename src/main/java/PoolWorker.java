public class PoolWorker extends Thread {
    private final ThreadPool threadPool;
    private boolean temporary; // тред временный
    private Runnable task;

    public PoolWorker(String name, boolean temporary, ThreadPool threadPool) {
        super(name);
        this.threadPool = threadPool;
        this.temporary = temporary;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public Runnable getTask() {
        return task;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            synchronized (threadPool) {
                try {
                    threadPool.wait(); // замораживаем тред
                } catch (InterruptedException e) {
                    System.out.println("Завершение " + currentThread().getName());
                }
                task = threadPool.getQueueOfTasks().poll(); // получить первую задачу в очереди
            }
            if (task != null) {
                task.run(); // тред выполняет задачу
                task = null;
                if (temporary) return; // тред временный -> одноразовый
            }
        }
    }

}