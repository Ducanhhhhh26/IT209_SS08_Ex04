public class Ex1 {
    public static void main(String[] args) {
        Student student1 = new Student("SV001", "Nguyen Van A", 2003, 8.2);
        Student student2 = new Student("SV002", "Tran Thi B", 2004, 7.6);

        student1.displayInfo();
        student2.displayInfo();
    }
}

class Student {
    private String studentId;
    private String fullName;
    private int birthYear;
    private double averageScore;

    public Student(String studentId, String fullName, int birthYear, double averageScore) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.birthYear = birthYear;
        this.averageScore = averageScore;
    }

    public void displayInfo() {
        System.out.println("Student ID: " + studentId);
        System.out.println("Full Name: " + fullName);
        System.out.println("Birth Year: " + birthYear);
        System.out.println("Average Score: " + averageScore);
        System.out.println("----------------------");
    }
}
