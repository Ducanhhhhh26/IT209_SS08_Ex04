import java.util.Scanner;

public class b2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Nhập dữ liệu
        System.out.print("Nhập mã sách: ");
        String bookID = sc.nextLine();

        System.out.print("Nhập tên sách: ");
        String bookName = sc.nextLine();

        System.out.print("Nhập năm xuất bản: ");
        int publishYear = sc.nextInt();

        System.out.print("Nhập giá tiền: ");
        double price = sc.nextDouble();

        System.out.print("Sách còn trong kho? (true/false): ");
        boolean isAvailable = sc.nextBoolean();

        // Tính tuổi thọ sách
        int bookAge = 2026 - publishYear;

        // Xử lý dữ liệu
        String bookNameUpper = bookName.toUpperCase();
        String status = isAvailable ? "Còn sách" : "Đã mượn";

        // In phiếu thông tin
        System.out.println("\n========= PHIẾU THÔNG TIN SÁCH =========");
        System.out.println("Mã sách      : " + bookID);
        System.out.println("Tên sách     : " + bookNameUpper);
        System.out.printf("Giá tiền     : %.2f\n", price);
        System.out.println("Năm xuất bản : " + publishYear);
        System.out.println("Tuổi thọ sách: " + bookAge + " năm");
        System.out.println("Tình trạng   : " + status);
        System.out.println("======================================");
    }
}
