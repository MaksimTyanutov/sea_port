package cursJava.ThirdService;

import cursJava.util.Ship;
import cursJava.util.Time;

import java.util.Date;

public class Statistics//класс статистики для 1 корабля
{
  public String name;
  public Date date_of_arriving;
  public Time time_of_waiting;
  public Date date_of_unloading;
  public Time time_of_service;
  public Time time_of_delay;
  public int queue_length;

  public Statistics (Ship ship)
  {
    this.name = ship.getName();
    this.date_of_arriving = new Date(ship.getTime());
    this.time_of_waiting = new Time(0);
    this.date_of_unloading = new Date(0);
    this.date_of_unloading.setTime(ship.getTime());
    this.time_of_service = new Time(0);
    this.time_of_delay = new Time(0);
    this.queue_length = 0;
  }
}
