package camunda_avg.util;

import java.util.List;

import camunda_avg.entity.Employee;

public class EmployeeUtil {
  public static int findNextAvailableId(List<Employee> employeeList) {
    int nextAvailableId = 1;

    // Sortieren Sie die vorhandenen IDs, um Lücken zu identifizieren
    employeeList.sort((e1, e2) -> Integer.compare(e1.id, e2.id));

    // Iterieren Sie durch die sortierte Liste, um die erste Lücke zu finden
    for (Employee employee : employeeList) {
      if (employee.id == nextAvailableId) {
        // Wenn die ID vorhanden ist, erhöhen Sie die nächste verfügbare ID
        nextAvailableId++;
      } else {
        // Wenn eine Lücke gefunden wurde, brechen Sie die Schleife ab
        break;
      }
    }

    return nextAvailableId;
  }
}
