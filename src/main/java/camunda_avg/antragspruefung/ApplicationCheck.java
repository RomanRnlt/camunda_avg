package camunda_avg.antragspruefung;

import java.util.logging.Logger;

import org.camunda.bpm.client.ExternalTaskClient;

public class ApplicationCheck {
  private final static Logger LOGGER = Logger.getLogger(ApplicationCheck.class.getName());

  public static void main(String[] args) {

    System.out.println("Lets go!");

    ExternalTaskClient client = ExternalTaskClient.create()
        .baseUrl("http://localhost:8080/engine-rest")
        .asyncResponseTimeout(10000) // long polling timeout
        .build();

    // subscribe to an external task topic as specified in the process
    client.subscribe("request-senden")
        .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
        .handler((externalTask, externalTaskService) -> {

          System.out.println("Es geht");

          String prename = externalTask.getVariable("prename");
          String surname = externalTask.getVariable("surname");
          String email = externalTask.getVariable("email");
          Integer salary = externalTask.getVariable("salary");
          String role = externalTask.getVariable("role");
          Integer staffNumber = externalTask.getVariable("staff_number");
          String department = externalTask.getVariable("department");

          LOGGER.info("Vorname des Kandidaten: " + prename);
          LOGGER.info("Nachname des Kandidaten: " + surname);
          LOGGER.info("E-Mail des Kandidaten: " + email);
          LOGGER.info("Gehalt der Ausgeschriebenen Stelle: " + salary);
          LOGGER.info("Rolle des Kandidaten: " + role);
          LOGGER.info("Personalnummer des Antragsstellers: " + staffNumber);
          LOGGER.info("Abteilung des Antragsstellers: " + department);

          // Complete the task
          externalTaskService.complete(externalTask);
        })
        .open();
  }

}
