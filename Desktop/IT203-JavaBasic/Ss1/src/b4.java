import java.util.Scanner;

public class b4 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Nhập dữ liệu
        System.out.print("Nhập giá sách (USD): ");
        double priceUSD = sc.nextDouble();

        System.out.print("Nhập tỷ giá (VND/USD): ");
        float exchangeRate = sc.nextFloat();

        // Tính tiền VNĐ (kết quả là double)
        double moneyVND = priceUSD * exchangeRate;

        // Ép kiểu từ double sang long để lấy số tiền chẵn
        long roundedMoney = (long) moneyVND;

        // Xuất kết quả
        System.out.println("Giá tiền sau khi quy đổi (VNĐ): " + roundedMoney);
    }
}
