package camunda_avg.entity;

public class Employee {
  public int id;
  public String prename;
  public String surname;
  public String department;
  public String role;
  public int salary;

  @Override
  public String toString() {
    return "Employee{prename='" + prename + '\'' + ", surname='" + surname + '\'' + '}';
  }
}
