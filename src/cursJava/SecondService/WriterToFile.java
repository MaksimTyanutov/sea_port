package cursJava.SecondService;

import cursJava.FirstService.Timetable;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.google.gson.Gson;
import cursJava.ThirdService.Port;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

public class WriterToFile
{
  static public void writeTimetable(String pathToJson, Timetable timetable)
  {
    try
    {
      JsonWriter writer = new JsonWriter(new FileWriter(pathToJson));
      Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm:ss").create();
      Type SHIP_TYPE = timetable.shipsArray.getClass();
      gson.toJson(timetable.shipsArray, SHIP_TYPE, writer);
      writer.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  static public void writePort(String pathToJson, Port port)
  {
    try
    {
      JsonWriter writer = new JsonWriter(new FileWriter(pathToJson));
      writer.beginObject();
      writer.name("Ships statistic");
      writer.beginArray();
      for (int i = 0; i < port.globalStat.num ;i++)
      {
        writer.beginObject()
                .name("Name").value(port.globalStat.statistic_list.get(i).name)
                .name("Date of arriving").value(String.valueOf(port.globalStat.statistic_list.get(i).date_of_arriving))
                .name("Time of waiting").value(String.valueOf(port.globalStat.statistic_list.get(i).time_of_waiting))
                .name("Date of unloading").value(String.valueOf(port.globalStat.statistic_list.get(i).date_of_unloading))
                .name("Time of unloading").value(String.valueOf(port.globalStat.statistic_list.get(i).time_of_service));
        writer.endObject();
      }
      writer.endArray();
      writer.name("GlobalStatistic:").beginObject();
      writer.name("Num").value(port.globalStat.num)
              .name("Average queue length").value(port.globalStat.average_queue_length)
              .name("Average time of waiting").value(String.valueOf(port.globalStat.average_time_of_waiting))
              .name("Max unload delay").value(String.valueOf(port.globalStat.max_time_of_delay))
              .name("Average delay time").value(String.valueOf(port.globalStat.average_time_of_delay))
              .name("Fine").value(port.globalStat.fine)
              .name("Container Cranes").value(port.globalStat.contCranes)
              .name("Liquid Cranes").value(port.globalStat.liquidCranes)
              .name("Loose Cranes").value(port.globalStat.looseCranes);
      writer.endObject().endObject();
      writer.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  static public void printPort(Port port)
  {
      System.out.println("Статистика корбалей:\n");
      for (int i = 0; i < port.globalStat.num ;i++)
      {
        System.out.printf("""
                        Название корабля: %s\s
                        Время прибытия: %s\s
                        Время ожидания: %s\s
                        Дата начала разгрузки: %s\s
                        Время разгрузки:%s\s
                        
                        """, port.globalStat.statistic_list.get(i).name,
                port.globalStat.statistic_list.get(i).date_of_arriving,
                port.globalStat.statistic_list.get(i).time_of_waiting,
                port.globalStat.statistic_list.get(i).date_of_unloading,
                port.globalStat.statistic_list.get(i).time_of_service
        );
      }
      System.out.println("\nОбщая статистика:");
      System.out.printf("""
                      Количество обработанных кораблей: %d\s
                      Средняя длина очереди: %f\s
                      Среднее время ожидания: %s\s
                      Максимальное время задержки разгрузки: %s\s
                      Среднее время задержки разгрузки: %s\s
                      
                      Штраф: %d\s
                      Контейнерные краны: %d\s
                      Краны для жидкостей: %d\s
                      Краны для сыпучего: %d\s
                      """,
              port.globalStat.num, port.globalStat.average_queue_length,
              port.globalStat.average_time_of_waiting,
              port.globalStat.max_time_of_delay,
              port.globalStat.average_time_of_delay,
              port.globalStat.fine, port.globalStat.contCranes,
              port.globalStat.liquidCranes, port.globalStat.looseCranes
      );
  }
}
