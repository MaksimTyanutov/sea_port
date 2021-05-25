package cursJava.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Ship {
  private final Calendar time;
  private final Calendar planned_time;
  private String name;
  private cargoType type;
  private final Time planned_unload_time;
  private int cargoNum;

  public Ship() {
    this.time = new GregorianCalendar();
    this.planned_time = new GregorianCalendar();
    this.planned_time.setTime(time.getTime());
    this.planned_unload_time = new Time();
  }

  public Ship(Ship to_copy) {
    this.time = new GregorianCalendar();
    time.setTime(to_copy.getDate());
    this.planned_time = new GregorianCalendar();
    planned_time.setTime(to_copy.getPlanned_time());
    this.planned_unload_time = new Time(to_copy.planned_unload_time.getTime());
    this.name = to_copy.name;
    this.type = to_copy.type;
    this.cargoNum = to_copy.cargoNum;
  }

  public void printShip() {
    System.out.printf("""
            Название корабля: %s\s
            Время прибытия: %s\s
            Запланированное время прибытия: %s\s
            Груз %s: %,d\s""", name, time.getTime(), planned_time.getTime(), type, cargoNum);
    if (type == cargoType.container) {
      System.out.println("containers");
    } else {
      System.out.println("tons");
    }
    System.out.printf("Заланированное время разгрузки: %s\n\n", planned_unload_time);
  }

  public int getCargoNum() {
    return this.cargoNum;
  }

  public void setCargoNum(int num) {
    this.cargoNum = num;
  }

  public void setType(cargoType type) {
    this.type = type;
  }

  public cargoType getType() {
    return this.type;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setPlanned_time(long time) {
    this.planned_time.setTime(new Date(time));
  }

  public Date getPlanned_time() {
    return this.planned_time.getTime();
  }

  public void setTime(long time) {
    this.time.setTime(new Date(time));
  }

  public long getTime() {
    return this.time.getTime().getTime();
  }

  public Date getDate() {
    return this.time.getTime();
  }

  public void setPlanned_unload_time(long time) {
    this.planned_unload_time.setTime(time);
  }

  public Time getPlanned_unload_time() {
    return this.planned_unload_time;
  }

  public Calendar getTimeInCalendar(){
    return this.time;
  }
  public Calendar getPlanned_timeInCalendar(){
    return this.planned_time;
  }
}

