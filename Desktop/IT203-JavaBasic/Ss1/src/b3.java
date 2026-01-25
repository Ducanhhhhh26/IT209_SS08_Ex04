import java.util.Scanner;

public class b3 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Nhập dữ liệu
        System.out.print("Nhập số ngày chậm trễ (n): ");
        int n = sc.nextInt();

        System.out.print("Nhập số lượng sách mượn (m): ");
        int m = sc.nextInt();

        // Tính tiền phạt cơ bản
        double total = n * m * 5000;

        // Kiểm tra điều kiện đặc biệt: n > 7 và m >= 3
        if (n > 7 && m >= 3) {
            total = total * 1.2; // tăng thêm 20%
        }

        // Kiểm tra yêu cầu khóa thẻ
        boolean needBlockCard = total > 50000;

        // Xuất kết quả
        System.out.println("Tổng tiền phạt: " + total + " VND");
        System.out.println("Yêu cầu khóa thẻ: " + needBlockCard);
    }
}
