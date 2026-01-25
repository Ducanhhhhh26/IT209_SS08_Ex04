import java.util.Scanner;

public class b5 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Nhập mã sách 4 chữ số: ");
        int number = sc.nextInt();

        // Tách từng chữ số bằng toán tử số học
        int thousands = number / 1000;          // hàng nghìn
        int hundreds  = (number / 100) % 10;    // hàng trăm
        int dozens    = (number / 10) % 10;     // hàng chục
        int units     = number % 10;            // hàng đơn vị (check-digit)

        // Tính tổng 3 chữ số đầu
        int sumOfFirstThreeNumber = thousands + hundreds + dozens;

        // Lấy số dư chia 10
        int remainder = sumOfFirstThreeNumber % 10;

        // Kiểm tra hợp lệ
        boolean isValid = (remainder == units);

        // In kết quả
        System.out.println("Thousands: " + thousands);
        System.out.println("Hundreds : " + hundreds);
        System.out.println("Dozens   : " + dozens);
        System.out.println("Units    : " + units);

        System.out.println("Mã hợp lệ: " + isValid);
    }
}
