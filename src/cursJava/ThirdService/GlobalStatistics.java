package cursJava.ThirdService;

import cursJava.util.Time;

import java.util.ArrayList;

public class GlobalStatistics//класс общей статистики
{
  public int num;
  public float average_queue_length;
  public long fine;
  public int contCranes;
  public int looseCranes;
  public int liquidCranes;
  public Time average_time_of_waiting;
  public Time max_time_of_delay;
  public Time average_time_of_delay;
  public ArrayList<Statistics> statistic_list;
  public ArrayList<Statistics> statistic_list_cont;
  public ArrayList<Statistics> statistic_list_liquid;
  public ArrayList<Statistics> statistic_list_loose;

  public GlobalStatistics()
  {
    this.statistic_list = new ArrayList<>();
    this.statistic_list_cont = new ArrayList<>();
    this.statistic_list_liquid = new ArrayList<>();
    this.statistic_list_loose = new ArrayList<>();
    this.average_time_of_waiting = new Time(0);
    this.max_time_of_delay = new Time(0);
    this.average_time_of_delay = new Time(0);
    this.num = 0;
    this.average_queue_length = 0;
    this.fine = 0;
    this.contCranes = 1;
    this.looseCranes = 1;
    this.liquidCranes = 1;
  }

  public void calculateAverage()//высчитывание средних показателей
  {
    long sum_time_of_waiting = 0;
    long sum_time_of_delay = 0;
    long max_time_of_delay = 0;
    int sum_queue_length = 0;
    statistic_list.addAll(statistic_list_cont);
    statistic_list.addAll(statistic_list_loose);
    statistic_list.addAll(statistic_list_liquid);
    for (int i = 0; i < num; i++)
    {
      sum_time_of_waiting += statistic_list.get(i).time_of_waiting.getTime();
      sum_time_of_delay += statistic_list.get(i).time_of_delay.getTime();
      sum_queue_length +=statistic_list.get(i).queue_length;
      if (statistic_list.get(i).time_of_delay.getTime() > max_time_of_delay)
      {
        max_time_of_delay = statistic_list.get(i).time_of_delay.getTime();
      }
    }
    average_time_of_waiting.setTime(sum_time_of_waiting/num);
    average_time_of_delay.setTime(sum_time_of_delay/num);
    this.max_time_of_delay.setTime(max_time_of_delay);
    average_queue_length = ((float)sum_queue_length)/num;
  }

  public void reset()//обнуление статистика для повторного использования
  {
    statistic_list.clear();
    statistic_list_cont.clear();
    statistic_list_liquid.clear();
    statistic_list_loose.clear();
    fine = 0;
    max_time_of_delay.setTime(0);
  }
}
