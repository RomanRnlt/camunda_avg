package camunda_avg.antragspruefung;

import camunda_avg.entity.ApprovalApplication;
import camunda_avg.entity.Candidate;
import camunda_avg.AgeCalculator;
import camunda_avg.http.HttpUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

//import java.util.logging.Logger;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.engine.variable.Variables;

public class ApplicationCheck {
  // private final static Logger LOGGER =
  // Logger.getLogger(ApplicationCheck.class.getName());
  private final static String DB_URL_EMPLOYEE = "http://localhost:3000/employees";
  private final static String DB_URL_CANDIDATE = "http://localhost:3000/candidates";
  private final static Integer MAX_ATTEMPTS = 3;

  List<Candidate> candidates = null;

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

          ApprovalApplication approvalApplication = new ApprovalApplication(
              externalTask.getVariable("c_id"),
              externalTask.getVariable("prename"),
              externalTask.getVariable("surname"),
              externalTask.getVariable("email"),
              externalTask.getVariable("salary"),
              externalTask.getVariable("role"),
              externalTask.getVariable("staff_number"),
              externalTask.getVariable("department"));

          System.out.println(approvalApplication);
          Integer currentAttempt = 0;
          String candidateResponse = null;

          while (currentAttempt < MAX_ATTEMPTS) {
            try {
              candidateResponse = HttpUtil
                  .sendGetRequest(DB_URL_CANDIDATE + "?c_id=" + approvalApplication.getC_id());
              break;
            } catch (Exception e) {
              currentAttempt++;
              System.out.println("Fehler bei Versuch " + currentAttempt + ": " + e.getMessage());
            }
          }

          if (currentAttempt == MAX_ATTEMPTS) {
            System.out.println("NEEEEEIN");
            externalTaskService.complete(externalTask,
                Variables.putValue("getRequestRetries", currentAttempt));
          }

          ObjectMapper objectMapper = new ObjectMapper();
          List<Candidate> candidates = null;
          try {
            candidates = objectMapper.readValue(
                candidateResponse, new TypeReference<List<Candidate>>() {
                });
            for (Candidate candidate : candidates) {
              System.out.println("Mapped Candidate: " + candidate);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }

          Integer candidateAge = AgeCalculator.calculateAge(candidates.get(0).birthdate);

          externalTaskService.complete(externalTask,
              Variables.putValue("candidateAge", candidateAge)
                  .putValue("desired_salary", candidates.get(0).desired_salary)
                  .putValue("candidateQualification", candidates.get(0).qualification)
          // add more variables as needed
          );

          // Complete the task
          // externalTaskService.complete(externalTask);
        })
        .open();
  }

  // private static void genhemigungsantragAusgeben(String prename, String
  // surname, String email, Integer salary,
  // String role, Integer staffNumber, String department) {
  // System.out.println("Vorname des Kandidaten: " + prename);
  // System.out.println("Nachname des Kandidaten: " + surname);
  // System.out.println("E-Mail des Kandidaten: " + email);
  // System.out.println("Gehalt der Ausgeschriebenen Stelle: " + salary);
  // System.out.println("Rolle des Kandidaten: " + role);
  // System.out.println("Personalnummer des Antragsstellers: " + staffNumber);
  // System.out.println("Abteilung des Antragsstellers: " + department);
  // }
}
