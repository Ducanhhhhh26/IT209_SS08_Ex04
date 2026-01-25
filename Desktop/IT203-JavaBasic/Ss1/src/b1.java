public class b1 {
    public static void main(String[] args) {
        String book1 = "Java Basic";
        String book2 = "Python Intro";

        // Trước khi hoán đổi
        System.out.println("Before swap:");
        System.out.println("book1 = " + book1);
        System.out.println("book2 = " + book2);

        // Biến tạm
        String temp = book1;   // temp tham chiếu tới cùng object với book1

        book1 = book2;        // book1 trỏ sang object của book2
        book2 = temp;         // book2 trỏ sang object ban đầu của book1

        // Sau khi hoán đổi
        System.out.println("After swap:");
        System.out.println("book1 = " + book1);
        System.out.println("book2 = " + book2);
    }
}
