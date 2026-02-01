// File: `TestEmployee.java`
package Ex4;

public class Main {
    public static void main(String[] args) {
        Employee e1 = new Employee();
        Employee e2 = new Employee("E001", "Alice");
        Employee e3 = new Employee("E002", "Bob", 3500.50);

        System.out.println("Employee 1:");
        e1.displayInformation();

        System.out.println("\nEmployee 2:");
        e2.displayInformation();

        System.out.println("\nEmployee 3:");
        e3.displayInformation();

        System.out.println(e1);
    }
}
