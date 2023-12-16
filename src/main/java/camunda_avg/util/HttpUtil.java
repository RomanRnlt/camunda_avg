package camunda_avg.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

public class HttpUtil {
  public static String sendGetRequest(String url) {
    try {
      // Die URL für den GET-Request

      // Erstelle einen HttpClient
      HttpClient client = HttpClients.createDefault();

      // Erstelle einen GET-Request
      HttpGet request = new HttpGet(url);

      // Sende den Request und erhalte die Antwort
      HttpResponse response = client.execute(request);

      // Lese die Antwort
      BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
      String line;
      StringBuilder result = new StringBuilder();

      while ((line = reader.readLine()) != null) {
        result.append(line);
      }
      reader.close();

      // Gib die Antwort aus
      return result.toString();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void sendPostRequest(String url, int id, String prename, String surname, String department, String role,
      int salary) {
    try {
      // Erstellen Sie einen HttpClient
      HttpClient httpClient = HttpClients.createDefault();

      // Erstellen Sie einen HttpPost mit der Ziel-URL
      HttpPost httpPost = new HttpPost(url);

      // Setzen Sie den Content-Type Header auf application/json
      httpPost.setHeader("Content-Type", "application/json");

      // Erstellen Sie das JSON-Objekt mit den übergebenen Parametern, einschließlich
      // der ID
      String jsonInputString = String.format(
          "{\"id\": %d, \"prename\": \"%s\", \"surname\": \"%s\", \"departement\": \"%s\", \"role\": \"%s\", \"salary\": %d}",
          id, prename, surname, department, role, salary);

      // Setzen Sie die JSON-Daten als StringEntity im HttpPost
      StringEntity stringEntity = new StringEntity(jsonInputString);
      httpPost.setEntity(stringEntity);

      // Führen Sie die Anfrage aus und erhalten Sie die Antwort
      HttpResponse response = httpClient.execute(httpPost);

      // Lesen Sie die Antwort vom Server
      BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
      StringBuilder responseStringBuilder = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        responseStringBuilder.append(line);
      }

      // Drucken Sie die Antwort aus
      System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
      System.out.println("Response: " + responseStringBuilder.toString());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
