package camunda_avg.entity;

import java.util.Date;

public class Candidate {
  public int cId;
  public String prename;
  public String surname;
  public Date birthdate;
  public int desiredSalary;
  public String email;
  public String role;

  @Override
  public String toString() {
    return "Candidate{prename='" + prename + '\'' + ", surname='" + surname + '\'' + '}';
  }
}
