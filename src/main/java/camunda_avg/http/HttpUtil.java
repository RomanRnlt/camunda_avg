package camunda_avg.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
}
