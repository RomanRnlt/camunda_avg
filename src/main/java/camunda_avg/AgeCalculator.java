package camunda_avg;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class AgeCalculator {

  public static int calculateAge(String birthdateString) {
    // Geburtsdatum-String in LocalDate umwandeln
    LocalDate birthdate = LocalDate.parse(birthdateString, DateTimeFormatter.ofPattern("yyyyMMdd"));

    // Aktuelles Datum
    LocalDate currentDate = LocalDate.now();

    // Alter berechnen
    return calculateAge(birthdate, currentDate);
  }

  // Methode zur Berechnung des Alters
  private static int calculateAge(LocalDate birthdate, LocalDate currentDate) {
    return Period.between(birthdate, currentDate).getYears();
  }
}
