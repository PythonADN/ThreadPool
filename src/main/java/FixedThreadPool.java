import java.util.LinkedList;
import java.util.Queue;

public class FixedThreadPool implements ThreadPool {
    private final int countThreads; // число тредов в пуле
    private final PoolWorker[] pool; // пул тредов
    private final Queue<Runnable> queueOfTasks; // очередь задач

    public FixedThreadPool(int countThreads) {
        this.countThreads = countThreads;
        this.pool = new PoolWorker[countThreads];
        this.queueOfTasks = new LinkedList<>();
    }

    public Queue<Runnable> getQueueOfTasks() {
        return queueOfTasks;
    }

    /**
     * Наполняет пул тредами и запускает все треды
     * Потоки бездействуют пока нет задач в очереди
     */
    @Override
    public void start() {
        for(int i=0; i<countThreads; i++) {
            pool[i] = new PoolWorker("Тред-"+(i+1), false,this);
            pool[i].start();
        }
        Thread controller = new Controller(); // создаём и запускаем контроллер задач
        controller.start();
        System.out.println("пул тредов Fixed запущен");
    }

    /**
     * выполняет данное задание (runnable) одним свободным потоком
     * добавляем новое задание и размораживаем один тред
     */
    @Override
    public synchronized void execute(Runnable runnable) {
        queueOfTasks.add(runnable); // добавляем в конец очереди задачу
    }


    //-------------------------------------------------------------

    /**
     * контрллер задач размораживает треды при новых задачах
     */
    class Controller extends Thread {
        @Override
        public void run() {
            ThreadPool threadPool = FixedThreadPool.this;
            while(true) {
                synchronized(threadPool) {
                    if (threadPool.getQueueOfTasks().size() > 0) threadPool.notify();
                }
            }
        }
    }
}