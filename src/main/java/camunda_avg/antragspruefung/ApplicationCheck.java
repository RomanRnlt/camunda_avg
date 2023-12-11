package camunda_avg.antragspruefung;

import java.util.logging.Logger;
import java.awt.Desktop;
import java.net.URI;

import org.camunda.bpm.client.ExternalTaskClient;

public class ApplicationCheck {
  // private final static Logger LOGGER =
  // Logger.getLogger(ApplicationCheck.class.getName());

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
          // Complete the task
          externalTaskService.complete(externalTask);
        })
        .open();
  }

}
