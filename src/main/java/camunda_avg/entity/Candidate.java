package camunda_avg.entity;

public class Candidate {
  public int c_id;
  public String prename;
  public String surname;
  public String birthdate;
  public String qualification;
  public int desired_salary;

  public Candidate() {
    // Standardkonstruktor ohne Parameter
  }

  public Candidate(int c_id, String prename, String surname, String birthdate, String qualification,
      int desired_salary) {
    this.c_id = c_id;
    this.prename = prename;
    this.surname = surname;
    this.birthdate = birthdate;
    this.qualification = qualification;
    this.desired_salary = desired_salary;
  }

  // Getter und Setter hier...

  @Override
  public String toString() {
    return "Candidate{" +
        "c_id=" + c_id +
        ", prename='" + prename + '\'' +
        ", surname='" + surname + '\'' +
        ", birthdate=" + birthdate +
        ", qualification=" + qualification +
        ", desired_salary=" + desired_salary +
        '}';
  }
}