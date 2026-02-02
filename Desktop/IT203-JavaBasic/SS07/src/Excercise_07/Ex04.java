package Excercise_07;

public class Ex04 {
    public static void main(String[] args) {

        ClassRoom s1 = new ClassRoom("Huy");
        ClassRoom s2 = new ClassRoom("Nam");
        ClassRoom s3 = new ClassRoom("Lan");

        s1.contribute(100_000);
        s2.contribute(50_000);
        s3.contribute(150_000);
        ClassRoom.showClassFund();
    }
}
class ClassRoom {
    static double classFund = 0;
    private String studentName;
    public ClassRoom(String studentName) {
        this.studentName = studentName;
    }
    public void contribute(double amount) {
        classFund += amount;
        System.out.println(studentName + " đã đóng " + amount + " vào quỹ.");
    }
    public static void showClassFund() {
        System.out.println("Tổng quỹ lớp hiện tại: " + classFund);
    }
}
