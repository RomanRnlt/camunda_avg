package camunda_avg.entity;

public class ApprovalApplication {
  private Integer c_id;
  private String prename;
  private String surname;
  private String email;
  private Integer salary;
  private String role;
  private Integer staffNumber;
  private String department;

  // Standardkonstruktor ohne Parameter
  public ApprovalApplication() {
  }

  public ApprovalApplication(Integer c_id, String prename, String surname, String email, Integer salary, String role,
      Integer staffNumber, String department) {
    this.c_id = c_id;
    this.prename = prename;
    this.surname = surname;
    this.email = email;
    this.salary = salary;
    this.role = role;
    this.staffNumber = staffNumber;
    this.department = department;
  }

  // Getter und Setter hier...
  public Integer getC_id() {
    return c_id;
  }

  @Override
  public String toString() {
    return "ApprovalApplication{" +
        "c_id='" + c_id + '\'' +
        "prename='" + prename + '\'' +
        ", surname='" + surname + '\'' +
        ", email='" + email + '\'' +
        ", salary=" + salary +
        ", role='" + role + '\'' +
        ", staffNumber=" + staffNumber +
        ", department='" + department + '\'' +
        '}';
  }
}
