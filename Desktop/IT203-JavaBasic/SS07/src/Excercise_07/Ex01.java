public class Ex01{
    public static void main(String[] args) {
        student student1 = new student("S001", "Alice");
        student student2 = new student("S002", "Bob");
        student student3 = new student("S003", "Charlie");

        student1.displayInfo();
        student2.displayInfo();
        student3.displayInfo();
        student.showTotalStudents();
    }
}
class student {
    private String id;
    private String name;

    static int totalStudents = 0;

    public student (String id, String name) {
        this.id = id;
        this.name = name;
        totalStudents++;
    }
    public void displayInfo() {
        System.out.println("Student ID: " + id);
        System.out.println("Student Name: " + name);
        System.out.println("-------------------");
    }
    public static void showTotalStudents() {
        System.out.println("Total Students: " + totalStudents);
    }
}