import java.math.BigDecimal;

public class Ex4 {
    public static void main(String[] args) {

        Employee employee1 = new Employee();
        employee1.displayInfo();

        Employee employee2 = new Employee(1, "Nguyen Duc Anh");
        employee2.displayInfo();

        Employee employee3 = new Employee(2, "Nguyen Duc Em", 5000.0);
        employee3.displayInfo();
    }
}

class Employee {
    private int employeeId;
    private String employeeName;
    private BigDecimal salary;

    public Employee() {
        this.employeeId = 0;
        this.employeeName = "Chưa có";
        this.salary = BigDecimal.ZERO;
    }

    public Employee(int employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.salary = BigDecimal.ZERO;
    }

    public Employee(int employeeId, String employeeName, double salary) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.salary = BigDecimal.valueOf(salary);
    }

    public void displayInfo() {
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Employee Name: " + employeeName);
        System.out.println("Salary: " + salary);
        System.out.println("----------------------");
    }
}