package cursJava;

import cursJava.FirstService.*;
import cursJava.SecondService.*;
import cursJava.ThirdService.*;

import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    int num = 100;//количество кораблей
    Timetable timetable = new Timetable(num);
    System.out.println("Добавить корабль в расписание? (д/н)");
    Scanner in = new Scanner(System.in);
    while (in.nextLine().equals("д")){
      try {
        timetable.addShipFromConsole();
      } catch (Exception e){
        if (e.getMessage() != null) {
          System.out.println(e.getMessage());
        }
      }
      System.out.println("Добавить корабль в расписание? (д/н)");
    }
    timetable.print();
    WriterToFile.writeTimetable(".\\out\\Timetable.json", timetable);
    Port port = new Port(1, 1, 1);
    port.getTimetableFromFile(".\\out\\Timetable.json");
    port.Optimize();
    WriterToFile.writePort(".\\out\\PortStatistic.json", port);
    WriterToFile.printPort(port);
  }
}

