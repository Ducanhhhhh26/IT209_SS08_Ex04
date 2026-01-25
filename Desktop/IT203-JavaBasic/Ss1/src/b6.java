import java.util.Scanner;

public class b6 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Nhập số thứ tự sách
        System.out.print("Nhập số thứ tự của cuốn sách (stt): ");
        int stt = sc.nextInt();

        // Xác định kệ sách (mỗi kệ 25 cuốn)
        int shelfNumber = (stt - 1) / 25 + 1;

        // Xác định vị trí trên kệ (1 đến 25)
        int position = (stt - 1) % 25 + 1;

        // Xác định khu vực bằng toán tử điều kiện
        String area = (shelfNumber <= 10) ? "Khu Cận" : "Khu Viễn";

        // In địa chỉ lưu kho hoàn chỉnh
        String location = "Kệ số " + shelfNumber + " - Vị trí " + position + " - Khu vực " + area;

        System.out.println(location);
    }
}
