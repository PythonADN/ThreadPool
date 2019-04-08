import java.util.LinkedList;
import java.util.Queue;

public class ScalableThreadPool implements ThreadPool {
    private final int minCount; // минимальное число тредов в пуле
    private final int maxCount; // максимальное число тредов в пуле
    private final PoolWorker[] pool; // пул тредов
    private final Queue<Runnable> queueOfTasks; // очередь задач

    public ScalableThreadPool(int minCount, int maxCount) {
        this.minCount = minCount;
        this.maxCount = (maxCount > minCount) ? maxCount : minCount;
        this.pool = new PoolWorker[this.maxCount];
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
        for (int i = 0; i < minCount; i++) { // наполняем пул минимальным количеством тредов
            pool[i] = new PoolWorker("Тред-" + (i + 1), false, this);
            pool[i].start();
        }
        Thread controller = new Controller(); // создаём и запускаем контроллер задач
        controller.start();
        System.out.println("пул тредов Scalable запущен");
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
     * а так=же создаёт дополнительные треды, если дефолтные не справляются
     */
    class Controller extends Thread {
        @Override
        public void run() {
            ThreadPool threadPool = ScalableThreadPool.this;
            while (true) {
                synchronized (threadPool) {
                    if (threadPool.getQueueOfTasks().size() > 0) {
                        threadPool.notify();
                    }
                }

                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (threadPool) {
                    if (threadPool.getQueueOfTasks().size() > 0) {
                        // нужно пробежаться по тредам и посмотреть есть ли свободные треды
                        if (notFindFreeThread()) addNewWorker(); // если свободных нет то добавляет нового
                    }
                }
                clearWorker(); // очистка дополнительных тредов
            }
        }

        private boolean notFindFreeThread() {
            for (int i = 0; i < minCount; i++) {
                if ( pool[i].getState().equals(State.WAITING) || pool[i].getState().equals(State.BLOCKED) || (pool[i].getState().equals(State.RUNNABLE) && pool[i].getTask() == null) )
                    return false; // какой-то основной спит, в ожидании монитора или пока не получил задачу (задач нет и новые ненужн)
            }
            return true;
        }

        private void addNewWorker() {
            for (int i = minCount; i < pool.length; i++) {
                if (pool[i] == null) {
                    pool[i] = new PoolWorker("ДопТред-" + (i - minCount + 1), true, ScalableThreadPool.this);
                    System.out.println(pool[i].getName() + " создан");
                    pool[i].start();
                    return;
                }
            }
        }

        private void clearWorker() {
            for (int i = minCount; i < pool.length; i++) {
                if (pool[i] != null)
                    if (pool[i].getState().equals(State.TERMINATED)) {
                        System.out.println(pool[i].getName() + " удалён");
                        pool[i] = null;
                    }
            }
        }
    }

}