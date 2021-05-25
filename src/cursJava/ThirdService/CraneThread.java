package cursJava.ThirdService;

import cursJava.util.Ship;
import cursJava.util.Time;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class CraneThread extends Thread {//класс крана
    private static final Object lock = new Object();
    private static final Object lock2 = new Object();
    //количество кранов, разгружающих груз
    public static AtomicInteger countWorkingThreads = new AtomicInteger(0);

    //количество кранов, закончивших работу
    public static AtomicInteger countNotWorkingThreads = new AtomicInteger(0);

    private static int threadCount;//количество кранов
    private static boolean[] workingAlone;//массив, показывающий какие краны работают по 1
    static Semaphore sem;//семафор кораблей
    static Semaphore sem2;//семафор времени
    static public Date date; //время в локальной симуляции
    public volatile static long fine;
    public volatile static int shipIndex;//индекс первого корабля, ожидающего в очереди
    private final int threadIndex;//индекс данного крана
    private final ArrayList<Ship> shipsInput;
    private final ArrayList<Statistics> statistics;
    private final double speed;
    private boolean isFirstCrane;//является ли кран первым, приступющим к разгрузке корабля
    public static boolean waitingForThread;//контролирует
    private static Time[] delays;

    CraneThread(int threadIndex, double speed_, ArrayList<Ship> shipsInput_, ArrayList<Statistics> statistics_) {
        this.threadIndex = threadIndex;
        this.shipsInput = shipsInput_;
        this.statistics = statistics_;
        this.speed = speed_;
        this.isFirstCrane = false;
    }

    @Override
    public void run() {
        while (true) {
            if (shipIndex - shipsInput.size() == -1) {
                countNotWorkingThreads.incrementAndGet();
                break;
            }
            int localShipIndex;//индекс корабля, обрабатывающегося данным кораблем
            synchronized (lock) {
                try {
                    if (countWorkingThreads.get() == 0){
                        waitingForThread = false;
                    }
                    sem.acquire();
                    countWorkingThreads.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isFirstCrane = true;//обрабатывается ли уже данный корабль одним краном
                for (int i = 0; i < threadCount; i++) {
                    if (workingAlone[i]) {
                        workingAlone[i] = false;
                        isFirstCrane = false;
                    }
                }
                if (!isFirstCrane) {
                    localShipIndex = shipIndex;
                } else {
                    workingAlone[threadIndex] = true;
                    shipIndex++;
                    localShipIndex = shipIndex;
                    isFirstCrane = true;
                    statistics.get(localShipIndex).date_of_arriving.setTime(shipsInput.get(localShipIndex).getDate().getTime());
                    if (shipsInput.get(localShipIndex).getDate().getTime() < date.getTime()) {
                        statistics.get(localShipIndex).time_of_waiting.setTime(date.getTime() - shipsInput.get(localShipIndex).getDate().getTime());
                        statistics.get(localShipIndex).date_of_unloading.setTime(date.getTime());
                    } else {
                        statistics.get(localShipIndex).date_of_unloading.setTime(shipsInput.get(localShipIndex).getDate().getTime());
                        statistics.get(localShipIndex).time_of_waiting.setTime(0);
                    }
                }
            }
            long unloadingTime = 0; // в минутах
            long localDelayTime = delays[localShipIndex].getTime() / (Time.MILLIS_IN_SEC * Time.SEC_IN_MIN);
            statistics.get(localShipIndex).time_of_delay.setTime(localDelayTime * 60 * 1000);
            while (shipsInput.get(localShipIndex).getCargoNum() > 0) {
                unloadingTime += 1;
                synchronized (lock2) {
                    shipsInput.get(localShipIndex).setCargoNum(shipsInput.get(localShipIndex).getCargoNum() - (int) speed);
                }
                long dateNow = date.getTime();
                sem2.release();
                while (dateNow == date.getTime()) {
                    Thread.yield();
                }
            }
            while (localDelayTime > 0) {
                long dateNow = date.getTime();
                sem2.release();
                while (dateNow == date.getTime()) {
                    Thread.yield();
                }
                localDelayTime--;
            }
            if (isFirstCrane) {
                int j = 0;
                while (date.getTime() > shipsInput.get(localShipIndex + j).getDate().getTime())  // добавляем длину очереди
                {
                    j++;
                    if (j + localShipIndex == shipsInput.size()) {
                        break;
                    }
                }
                statistics.get(localShipIndex).queue_length = j - 1;
                statistics.get(localShipIndex).time_of_service.setTime((unloadingTime + localDelayTime) * 60 * 1000);
                int fineHours = (int) ((-shipsInput.get(localShipIndex).getPlanned_unload_time().getTime()
                        + statistics.get(localShipIndex).time_of_service.getTime()
                        + statistics.get(localShipIndex).time_of_waiting.getTime()) / (1000 * 60 * 60));
                if (fineHours > 0) {
                    fine += fineHours * 100L;
                }
            }
            countWorkingThreads.decrementAndGet();
            sem2.release();
            synchronized (lock) {
                if (countWorkingThreads.get() == 0){
                    waitingForThread = true;
                }
                if (workingAlone[threadIndex]) {
                    workingAlone[threadIndex] = false;
                    try {
                        sem.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void initialize(int threadCount_, Semaphore sem_, Semaphore sem2_, Date date_, Time[] delays_) {
        countWorkingThreads = new AtomicInteger(0);
        countNotWorkingThreads = new AtomicInteger(0);
        threadCount = threadCount_;
        workingAlone = new boolean[threadCount];
        for (int i = 0; i < threadCount; i++) {
            workingAlone[i] = false;
        }
        sem = sem_;
        sem2 = sem2_;
        fine = 0;
        shipIndex = -1;
        date = new Date(date_.getTime());
        delays = delays_;
    }
}
