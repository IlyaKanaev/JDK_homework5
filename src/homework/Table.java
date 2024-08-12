package homework;

import java.util.concurrent.CountDownLatch;

public class Table extends Thread {
    private final int PHILOSOPHER_COUNT = 5;
    private Fork[] forks;
    private Philosopher[] philosophers;
    // мутное какое-то свойство ниже... но в нем вся суть многопоточности...
    private CountDownLatch cdl; // объект для понимания, что все философы успешно поели

    public Table() {
        forks = new Fork[PHILOSOPHER_COUNT];
        philosophers = new Philosopher[PHILOSOPHER_COUNT];
        cdl = new CountDownLatch(PHILOSOPHER_COUNT);
        init();
    }

    @Override
    public void run() {
        System.out.println("Заседание макаронных мудрецов объявляется открытым");
        try {
            thinkingProcess(); // стартует "думанье" (на самом деле думанье/хватание вилок/кушанье) всех философов
            cdl.await(); // это некий счетчик задач, от 5 до 0 который как-то взаимодействует с потоками. Как?
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Все философы накушались");
    }

    // Ключевой метод всей программы - "хватание вилок". Именно в этой "лотерее" участвуют все "не задумавшиеся"
    // Поэтому во всей программе только этому методу требуется синхронизация
    public synchronized boolean tryGetForks(int leftFork, int rightFork) {
        if (!forks[leftFork].isUsing() && !forks[rightFork].isUsing()) {
            forks[leftFork].setUsing(true);
            forks[rightFork].setUsing(true);
            return true;
        }
        return false; // одна из вилок была занята
    }

    // завершая "покушать", кладем вилки на стол
    public void putForks(int leftFork, int rightFork) {
        forks[leftFork].setUsing(false);
        forks[rightFork].setUsing(false);
    }
    private void init() { // тут создаются все объекты кроме стола
        for (int i = 0; i < PHILOSOPHER_COUNT; i++) {
            forks[i] = new Fork();
        }
        for (int i = 0; i < PHILOSOPHER_COUNT; i++) {
            philosophers[i] = new Philosopher("Philosopher №" + i, this,
                    i, (i + 1) % PHILOSOPHER_COUNT, cdl);
        }
    }
    private void thinkingProcess() {
        for (Philosopher philosopher : philosophers) {
            philosopher.start();
        }
    }
}
