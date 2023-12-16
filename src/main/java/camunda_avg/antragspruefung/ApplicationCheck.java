package camunda_avg.antragspruefung;

import camunda_avg.entity.ApprovalApplication;
import camunda_avg.entity.Candidate;
import camunda_avg.entity.Employee;
import camunda_avg.util.HttpUtil;
import camunda_avg.util.EmployeeUtil;
import camunda_avg.EmailSender;
import camunda_avg.AgeCalculator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

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
    client.subscribe("kandidaten-daten-abfragen")
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
              System.out.println("Fehler bei Versuch " + currentAttempt + ": " +
                  e.getMessage());
            }
          }

          if (currentAttempt == MAX_ATTEMPTS) {
            // externalTaskService.handleBpmnError(externalTask, "failed_api_request");

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

    client.subscribe("kandidat-einstellen")
        .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
        .handler((externalTask, externalTaskService) -> {

          String employeesResponse = HttpUtil
              .sendGetRequest(DB_URL_EMPLOYEE);

          ObjectMapper objectMapper = new ObjectMapper();
          List<Employee> employees = null;
          try {
            employees = objectMapper.readValue(
                employeesResponse, new TypeReference<List<Employee>>() {
                });
            for (Employee employee : employees) {
              System.out.println("Mapped Candidate: " + employee);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }

          int nextAvailableId = EmployeeUtil.findNextAvailableId(employees);

          HttpUtil.sendPostRequest(DB_URL_EMPLOYEE, nextAvailableId,
              externalTask.getVariable("prename"),
              externalTask.getVariable("surname"),
              externalTask.getVariable("department"),
              externalTask.getVariable("role"),
              externalTask.getVariable("salary"));

          externalTaskService.complete(externalTask);
        })
        .open();

    client.subscribe("email-vertrag-senden")
        .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
        .handler((externalTask, externalTaskService) -> {

          String subject = "Vertragsangebot mit einer Gültigkeitsdauer von 14 Tagen";

          String body = "Hallo " + externalTask.getVariable("prename") + ",\n\n" +
              "wir freuen uns, dir unser aktuelles Vertragsangebot für die Position " + externalTask.getVariable("role")
              +
              " in der Abteilung " + externalTask.getVariable("department") + " vorzustellen.\n" +
              "Dieses Angebot ist ab heute für 14 Tage gültig.\n\n" +
              "Falls du weitere Informationen benötigst oder Fragen hast, stehe ich gerne zur Verfügung. " +
              "Wir hoffen auf eine positive Rückmeldung.\n\n" +
              "Das Vertragsangebot kannst du durch Senden eines POST-Requests an folgende URL " +
              "\"localhost:8080/engine-rest/message\" mit dem Raw-Body (siehe unten) beantworten\n " +
              "Setze dabei den Wert der Variable \"vertrag_unterschrieben\" auf \"unterschrieben\", " +
              "um den Vertrag erfolgreich abzuschließen, oder auf \"nicht-unterschrieben\", um das Angebot abzulehnen.\n\n"
              +
              "{\n" +
              "    \"messageName\": \"vertrag-von-kandidat-eingegangen\",\n" +
              "    \"businessKey\": \"1\",\n" +
              "    \"processVariables\": {\n" +
              "        \"vertrag_unterschrieben\": {\"value\": \"unterschrieben\", \"type\":\"String\"}\n" +
              "    }\n" +
              "}\n\n" +
              "Beste Grüße,\n" +
              "Max Musterpersonaler";

          EmailSender.sendEmail(externalTask.getVariable("email"), subject, body);

          externalTaskService.complete(externalTask);
        })
        .open();

    client.subscribe("email-absage-senden")
        .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
        .handler((externalTask, externalTaskService) -> {

          String subject = "Betreff: Mitteilung zur Bewerbung für die Position " + externalTask.getVariable("role");

          String body = "Hallo " + externalTask.getVariable("prename") + ",\n\n" +
              "wir bedauern, dir mitteilen zu müssen, dass wir dir keine Zusage für die Position " +
              externalTask.getVariable("role") + " in der Abteilung " + externalTask.getVariable("department") +
              " erteilen können.\n\n" +
              "Trotzdem danken wir dir für dein Interesse an unserem Unternehmen und stehen dir für etwaige Rückfragen zur Verfügung.\n\n"
              +
              "Beste Grüße,\n" +
              "Max Musterpersonaler";

          EmailSender.sendEmail(externalTask.getVariable("email"), subject, body);

          externalTaskService.complete(externalTask);
        })
        .open();
  }
}
