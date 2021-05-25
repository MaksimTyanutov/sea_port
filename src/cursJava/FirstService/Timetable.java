package cursJava.FirstService;

import cursJava.util.Ship;
import cursJava.util.cargoType;
import cursJava.ThirdService.Port;

import java.util.Calendar;
import java.util.Scanner;

public class Timetable {
  public int num;
  public Ship[] shipsArray;

  public Timetable(int num) {
    this.num = num;
    this.shipsArray = new Ship[num];
    this.generateTime();
  }

  public void generateTime() {
    for (int i = 0; i < num; i++) {
      shipsArray[i] = new Ship();
      shipsArray[i].getTimeInCalendar().set(Calendar.YEAR, 121);
      shipsArray[i].getTimeInCalendar().set(Calendar.MONTH, Calendar.MARCH);
      shipsArray[i].getTimeInCalendar().set(Calendar.DAY_OF_MONTH, getRandomNum(0,30));
      shipsArray[i].getTimeInCalendar().set(Calendar.HOUR, getRandomNum(0,23));
      shipsArray[i].getTimeInCalendar().set(Calendar.MINUTE,getRandomNum(0,59));
      shipsArray[i].getTimeInCalendar().set(Calendar.SECOND, getRandomNum(0,59));
      shipsArray[i].setName("Ship №" + i);
      shipsArray[i].setCargoNum(getRandomNum(5000,100000));
      switch (getRandomNum(0,2)) {
        case 0 -> {
          shipsArray[i].setType(cargoType.loose);
          shipsArray[i].setPlanned_unload_time((long) ((shipsArray[i].getCargoNum()) * 500L / Port.looseSpeed)
                  + 1000 * 60 * 60 * 12);//добавление 12 часов ко времени разгрузки
        }
        case 1 -> {
          shipsArray[i].setType(cargoType.liquid);
          shipsArray[i].setPlanned_unload_time((long) ((shipsArray[i].getCargoNum()) * 500L / Port.liquidSpeed)
                  + 1000 * 60 * 60 * 12);
        }
        default -> {
          shipsArray[i].setType(cargoType.container);
          shipsArray[i].setPlanned_unload_time((long) ((shipsArray[i].getCargoNum()) * 500L / Port.containerSpeed)
                  + 1000 * 60 * 60 * 12);
        }
      }

      shipsArray[i].setPlanned_time(shipsArray[i].getTime() + (long)getRandomNum(-7,7) * 24 * 60 * 60 * 1000);

    }
    this.sort();
  }

  public void sort() {
    for (int j = num - 1; j >= 1; j--) {
      for (int i = 0; i < j; i++) {
        if (shipsArray[i].getTime() > shipsArray[i + 1].getTime()) {
          Ship tmp = shipsArray[i];
          shipsArray[i] = shipsArray[i + 1];
          shipsArray[i + 1] = tmp;
        }
      }
    }
  }
  
  public void print(){
    for (int i = 0; i < num; i++)
        {
            this.shipsArray[i].printShip();
        }
  }

  public void addShipFromConsole()
  {
    Ship ship = new Ship();
    Scanner in = new Scanner(System.in);
    System.out.println("Введите имя корабля: ");
    ship.setName(in.nextLine());
    System.out.println("Задайте тип груза корабля(введите число от 0 до 2, где 0 - LOOSE, 1 - LIQUID, 2 - CONTAINER): ");
    int cargoIndex = in.nextInt();
    if (cargoIndex < 0 || cargoIndex > 2)
    {
      throw new IllegalArgumentException("Число должно быть от 0 до 2! Попробуйте заново");
    }
    switch (cargoIndex) {
      case 0 -> {
        ship.setType(cargoType.loose);
        ship.setPlanned_unload_time((long) ((ship.getCargoNum()) * 500L / Port.looseSpeed)
                + 1000 * 60 * 60 * 12);
      }
      case 1 -> {
        ship.setType(cargoType.liquid);
        ship.setPlanned_unload_time((long) ((ship.getCargoNum()) * 500L / Port.liquidSpeed)
                + 1000 * 60 * 60 * 12);
      }
      case 2 -> {
        ship.setType(cargoType.container);
        ship.setPlanned_unload_time((long) ((ship.getCargoNum()) * 500L / Port.containerSpeed)
                + 1000 * 60 * 60 * 12);
      }
    }
    System.out.println("Задайте вес корабля в тоннах(от 5000 до 100000): ");
    ship.setCargoNum(in.nextInt());
    if (ship.getCargoNum() < 5000 || ship.getCargoNum() > 100000) {
      throw new IllegalArgumentException("Число должно быть от 5000 до 100000! Попробуйте заново");
    }
    System.out.println("Установите дату пребытия:\nВведите год: ");
    Calendar date = ship.getTimeInCalendar();
    date.set(Calendar.YEAR, in.nextInt());
    System.out.println("Введите месяц(0-11): ");
    date.set(Calendar.MONTH, in.nextInt());
    System.out.println("Введите день: ");
    date.set(Calendar.DAY_OF_MONTH, in.nextInt());
    System.out.println("Введите час: ");
    date.set(Calendar.HOUR, in.nextInt());
    System.out.println("Введите минуты: ");
    date.set(Calendar.MINUTE, in.nextInt());
    System.out.println("Введите секунды: ");
    date.set(Calendar.SECOND, in.nextInt());
    ship.setPlanned_time(ship.getTime() + (long)getRandomNum(-7,7) * 24 * 60 * 60 * 1000);
    Ship[] temp = new Ship[num+1];
    System.arraycopy(shipsArray, 0, temp, 0, shipsArray.length);
    temp[num] = ship;
    num++;
    shipsArray = temp;
  }

  private int getRandomNum(int min, int max) {
    return (int) (Math.random() * (max - min + 1) + min);
  }
}
