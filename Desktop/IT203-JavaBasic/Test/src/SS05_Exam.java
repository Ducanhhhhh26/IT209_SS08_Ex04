import java.util.Scanner;
import java.util.regex.Pattern;

public class SS05_Exam {

    static Scanner sc = new Scanner(System.in);
    static String[] mssvList = new String[100]; // mảng lưu MSSV
    static int count = 0; // số phần tử hiện có

    // Regex MSSV
    static final String REGEX_MSSV = "^B\\d{7}$";

    public static void main(String[] args) {
        int choice;

        do {
            showMenu();
            System.out.print("Chọn chức năng: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    display();
                    break;
                case 2:
                    addMSSV();
                    break;
                case 3:
                    updateMSSV();
                    break;
                case 4:
                    deleteMSSV();
                    break;
                case 5:
                    searchMSSV();
                    break;
                case 0:
                    System.out.println("Thoát chương trình. Bye 👋");
                    break;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        } while (choice != 0);
    }

    // ================= MENU =================
    static void showMenu() {
        System.out.println("\n===== MENU QUẢN LÝ MSSV =====");
        System.out.println("1. Hiển thị danh sách MSSV");
        System.out.println("2. Thêm MSSV mới");
        System.out.println("3. Cập nhật MSSV theo index");
        System.out.println("4. Xóa MSSV");
        System.out.println("5. Tìm kiếm MSSV");
        System.out.println("0. Thoát");
    }

    // ================= HIỂN THỊ =================
    static void display() {
        if (count == 0) {
            System.out.println("Danh sách rỗng.");
            return;
        }

        System.out.println("Danh sách MSSV:");
        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " + mssvList[i]);
        }
    }

    // ================= THÊM =================
    static void addMSSV() {
        if (count == 100) {
            System.out.println("Mảng đã đầy, không thể thêm.");
            return;
        }

        String mssv;
        while (true) {
            System.out.print("Nhập MSSV mới: ");
            mssv = sc.nextLine();

            if (Pattern.matches(REGEX_MSSV, mssv)) {
                break;
            } else {
                System.out.println("Sai định dạng! MSSV phải dạng Bxxxxxxx (7 chữ số).");
            }
        }

        mssvList[count] = mssv;
        count++;
        System.out.println("Thêm MSSV thành công!");
    }

    // ================= CẬP NHẬT =================
    static void updateMSSV() {
        if (count == 0) {
            System.out.println("Danh sách rỗng.");
            return;
        }

        System.out.print("Nhập index cần sửa (0 -> " + (count - 1) + "): ");
        int index = Integer.parseInt(sc.nextLine());

        if (index < 0 || index >= count) {
            System.out.println("Index không hợp lệ!");
            return;
        }

        String newMSSV;
        while (true) {
            System.out.print("Nhập MSSV mới: ");
            newMSSV = sc.nextLine();

            if (Pattern.matches(REGEX_MSSV, newMSSV)) {
                break;
            } else {
                System.out.println("Sai định dạng MSSV!");
            }
        }

        mssvList[index] = newMSSV;
        System.out.println("Cập nhật thành công!");
    }

    // ================= XÓA =================
    static void deleteMSSV() {
        if (count == 0) {
            System.out.println("Danh sách rỗng.");
            return;
        }

        System.out.print("Nhập MSSV cần xóa: ");
        String deleteValue = sc.nextLine();

        int pos = -1;
        for (int i = 0; i < count; i++) {
            if (mssvList[i].equalsIgnoreCase(deleteValue)) {
                pos = i;
                break;
            }
        }

        if (pos == -1) {
            System.out.println("Không tìm thấy MSSV.");
            return;
        }

        // Dồn mảng
        for (int i = pos; i < count - 1; i++) {
            mssvList[i] = mssvList[i + 1];
        }

        mssvList[count - 1] = null;
        count--;

        System.out.println("Xóa thành công!");
    }

    // ================= TÌM KIẾM =================
    static void searchMSSV() {
        if (count == 0) {
            System.out.println("Danh sách rỗng.");
            return;
        }

        System.out.print("Nhập chuỗi cần tìm: ");
        String key = sc.nextLine().toLowerCase();

        boolean found = false;

        for (int i = 0; i < count; i++) {
            if (mssvList[i].toLowerCase().contains(key)) {
                System.out.println((i + 1) + ". " + mssvList[i]);
                found = true;
            }
        }

        if (!found) {
            System.out.println("Không có MSSV phù hợp.");
        }
    }
}
