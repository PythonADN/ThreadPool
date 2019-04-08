public class Main {
    public static void main(String[] args) {
//        ThreadPool pool = new FixedThreadPool(2); // создаём пул на 10 тредов
        ThreadPool pool = new ScalableThreadPool(2, 5); // создаём пул на 10 тредов
        pool.start();

        // добавляем задачи
        pool.execute(getTask(1000, "Задача 1"));
        pool.execute(getTask(700, "Задача 2"));
        pool.execute(getTask(1000, "Задача 3"));
        pool.execute(getTask(1000, "Задача 4"));
        pool.execute(getTask(1000, "Задача 5"));
        pool.execute(getTask(2000, "Задача 6"));
        pool.execute(getTask(1000, "Задача 7"));
        pool.execute(getTask(500, "Задача 8"));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nНовые задачи\n");

        pool.execute(getTask(300, "Задача 9"));
        pool.execute(getTask(200, "Задача 10"));
        pool.execute(getTask(300, "Задача 11"));
        pool.execute(getTask(500, "Задача 12"));
        pool.execute(getTask(300, "Задача 13"));


    }

    private static Runnable getTask(long millis, String nameTask) {
        return new Runnable() {
            @Override
            public void run() {
                System.out.format("%s, на треде - %s НАЧАТА\n", nameTask, Thread.currentThread().getName());
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.format("%s, на треде - %s ОКОНЧЕНА\n", nameTask, Thread.currentThread().getName());
            }
        };
    }

}
