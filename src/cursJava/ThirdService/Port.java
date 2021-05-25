package cursJava.ThirdService;


import cursJava.util.Ship;
import cursJava.util.Time;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;


import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Port {

    private int containerCranes;
    private int liquidCranes;
    private int looseCranes;

    public static final double containerSpeed = 15;//в минуту
    public static final double liquidSpeed = 25;
    public static final double looseSpeed = 15;

    private final ArrayList<Ship> queueContainer;
    private final ArrayList<Ship> queueLiquid;
    private final ArrayList<Ship> queueLoose;

    private Time[] delays;
    private Time[] delaysContainer;
    private Time[] delaysLiquid;
    private Time[] delaysLoose;

    public GlobalStatistics globalStat;

    public Port(int n1, int n2, int n3) {
        this.containerCranes = n1;
        this.liquidCranes = n2;
        this.looseCranes = n3;
        this.queueContainer = new ArrayList<>();
        this.queueLiquid = new ArrayList<>();
        this.queueLoose = new ArrayList<>();
        this.globalStat = new GlobalStatistics();
        this.delays = new Time[0];
    }

    private Time[] generateDelay(int n) //создание массива задержек для исключения случайности при оптимизации порта
    {
        Time[] delay = new Time[n];
        for (int i = 0; i < n; i++) {
            delay[i] = new Time((long) (Math.random() * 1440 * 60 * 1000));
        }
        return delay;
    }

    public long runSim() {//разовая симуляция обработки всех кораблей
        long fine = 0;
        globalStat.statistic_list = new ArrayList<>();
        globalStat.reset();
        if (!queueContainer.isEmpty()) {
            try {
                fine += cranesSimulation(queueContainer, containerCranes, containerSpeed, delaysContainer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!queueLiquid.isEmpty()) {
            try {
                fine += cranesSimulation(queueLiquid, liquidCranes, liquidSpeed, delaysLiquid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!queueLoose.isEmpty()) {
            try {
                fine += cranesSimulation(queueLoose, looseCranes, looseSpeed, delaysLoose);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        globalStat.fine = fine;
        globalStat.calculateAverage();
        return fine;
    }

    //симуляция обработки кораблей одного типа
    long cranesSimulation(ArrayList<Ship> queue_main, int Cranes, double speed, Time[] delays) throws InterruptedException {
        ArrayList<Statistics> statistic_list = new ArrayList<>();
        ArrayList<Ship> queue = new ArrayList<>();
        for (Ship value : queue_main) { //копирование очереди для симуляции
            statistic_list.add(new Statistics(value));
            queue.add(new Ship(value));
        }
        Semaphore sem = new Semaphore(0);//семафор, отвечающий за количество кораблей
        Semaphore sem2 = new Semaphore(0);//семафор, контролирующий тактирование времени
        ArrayList<CraneThread> CT = new ArrayList<>();
        CraneThread.initialize(Cranes, sem, sem2, queue.get(0).getDate(), delays);
        for (int j = 0; j < Cranes; j++) {
            CT.add(new CraneThread(j, speed, queue, statistic_list));
        }
        //int minutesCounter = 0;
        for (CraneThread ct : CT) {
            ct.start();
        }
        int shipIndex = 0;
        while (CraneThread.countNotWorkingThreads.get() != Cranes) {//основной цикл для поминутной симуляции
            if (!CraneThread.waitingForThread) {
                if (shipIndex < queue.size()) {
                    if (CraneThread.date.getTime() >= queue.get(shipIndex).getDate().getTime()) {
                        sem.release(2);
                        shipIndex++;
                    }
                }
                CraneThread.date.setTime(CraneThread.date.getTime() + 60000);
                sem2.acquire(CraneThread.countWorkingThreads.get());
            }
        }
        switch (queue.get(0).getType()) {
            case container -> globalStat.statistic_list_cont.addAll(statistic_list);
            case loose -> globalStat.statistic_list_loose.addAll(statistic_list);
            case liquid -> globalStat.statistic_list_liquid.addAll(statistic_list);
        }
        return CraneThread.fine;
    }

    private class OptimizeCranesSimulation {//класс для нахождения оптимального числа кранов заданного типа
        ArrayList<Ship> queue;
        int Cranes;
        double speed;
        long fine;
        Time[] delay;

        OptimizeCranesSimulation(ArrayList<Ship> queue, int Cranes, double speed, Time[] delay_) {
            this.queue = queue;
            this.Cranes = Cranes;
            this.speed = speed;
            this.fine = 0;
            this.delay = delay_;
        }

        public void run() {
            try {
                fine = cranesSimulation(queue, Cranes, speed, delay);
                long next_fine = cranesSimulation(queue, Cranes + 1, speed, delay);
                long crane_cost = 30000;
                while (fine - next_fine >= crane_cost) {
                    Cranes++;
                    fine = next_fine;
                    next_fine = cranesSimulation(queue, Cranes + 1, speed, delay);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void Optimize() {//нахождение оптимального числа кранов каждого типа
        OptimizeCranesSimulation[] simArray = new OptimizeCranesSimulation[3];
        if (!queueContainer.isEmpty()) {
            simArray[0] = new OptimizeCranesSimulation(queueContainer, containerCranes, containerSpeed, delaysContainer);
            simArray[0].run();
            containerCranes = simArray[0].Cranes;
        }

        if (!queueLiquid.isEmpty()) {
            simArray[1] = new OptimizeCranesSimulation(queueLiquid, liquidCranes, liquidSpeed, delaysLiquid);
            simArray[1].run();
            liquidCranes = simArray[1].Cranes;
        }

        if (!queueLoose.isEmpty()) {
            simArray[2] = new OptimizeCranesSimulation(queueLoose, looseCranes, looseSpeed, delaysLoose);
            simArray[2].run();
            looseCranes = simArray[2].Cranes;
        }

        globalStat.liquidCranes = liquidCranes;
        globalStat.contCranes = containerCranes;
        globalStat.looseCranes = looseCranes;
        globalStat.fine = runSim();
    }

    public void getTimetableFromFile(String pathToJson) {//инициализация расписания из файла
        try {
            JsonReader reader = new JsonReader(new FileReader(pathToJson));
            Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm:ss").create();
            Type SHIP_TYPE = new TypeToken<ArrayList<Ship>>() {
            }.getType();
            ArrayList<Ship> ships = gson.fromJson(reader, SHIP_TYPE);
            reader.close();
            for (Ship ship : ships) {
                switch (ship.getType()) {
                    case loose -> queueLoose.add(ship);
                    case liquid -> queueLiquid.add(ship);
                    case container -> queueContainer.add(ship);
                }
            }
            globalStat.num = ships.size();
            delays = generateDelay(ships.size());
            delaysContainer = new Time[queueContainer.size()];
            delaysLiquid = new Time[queueLiquid.size()];
            delaysLoose = new Time[queueLoose.size()];
            distributeDelays();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void distributeDelays() {
        for (int i = 0; i < delays.length; i++) {
            if (i < queueContainer.size()) {
                delaysContainer[i] = delays[i];
            } else if (i < queueContainer.size() + queueLiquid.size()) {
                delaysLiquid[i - queueContainer.size()] = delays[i];
            } else {
                delaysLoose[i - queueContainer.size() - queueLiquid.size()] = delays[i];
            }
        }
    }

}
