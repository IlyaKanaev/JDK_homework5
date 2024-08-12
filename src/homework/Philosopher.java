package homework;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

// Вся хитрость тут в том, что философ может не только думать и кушать, но и "хватать вилки"
// Собственно "кушать", а затем "думать" пассивно привязано к результату "хватать вилки" - метод tryGetForks в Table
// т.к. в "хватании вилок" участвуют все, то и метод относится к Table
public class Philosopher extends Thread {
    private String name;
    private int leftFork;
    private int rightFork;
    private int countEat; // количество совершенных приемов пищи
    private Random random;
    private CountDownLatch cdl;
    private Table table;

    public Philosopher(String name, Table table, int leftFork,
                       int rightFork, CountDownLatch cdl) {
        this.table = table;
        this.name = name;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.cdl = cdl;
        countEat = 0;
        random = new Random();
    }

    @Override
    public void run() {
        while (countEat < 3) { // пока три раза не покушали
            try {
                thinking(); // думаем
                eating(); // кушаем. А что делаем если вилки заняты? не проверяем же каждую наносекунду?
            } catch (InterruptedException e) {
                e.fillInStackTrace();
            }
        }
        System.out.println(name + " наелся до отвала");
        cdl.countDown(); // объект cdl увеличивается на 1, т.е. сытых стало больше на одного
    }

    /* В этом методе пытаемся взять со стола две вилки
    Если это успешно, то мы начинаем кушать
     */
    private void eating() throws InterruptedException {
        if (table.tryGetForks(leftFork, rightFork)) {
            System.out.println(name + " уплетает спагетти, используя вилки: " // заменил вермишель на спагетти...
                    + leftFork + " и " + rightFork);
            sleep(random.nextLong(3000, 6000)); // делаем паузу на 3-6 секунд (рандомно)
            table.putForks(leftFork, rightFork); // кладем вилки
            System.out.println(name + " покушал, можно и помыслить " +
                    "Не забыв при этом вернуть вилки " + leftFork + " и " + rightFork);
            countEat++; // счетчик кушанья каждого философа
        }
    }

    private void thinking() throws InterruptedException { // тут просто спим
        sleep(random.nextLong(100, 2000));
    }
}
