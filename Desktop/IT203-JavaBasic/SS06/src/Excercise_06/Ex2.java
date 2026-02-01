public class Ex2 {
    public static void main(String[] args) {
        Account acc = new Account("huy123", "123456", "huy@gmail.com");
        System.out.println("Thông tin ban đầu:");
        acc.hienThiThongTin();
        acc.doiMatKhau("matkhauMoi2026");
        System.out.println("Sau khi đổi mật khẩu:");
        acc.hienThiThongTin();
    }
}
class Account {
    private String username;
    private String password;
    private String email;

    public Account(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void doiMatKhau(String matKhauMoi) {
        this.password = matKhauMoi;
    }

    public void hienThiThongTin() {
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Password: "+ password);
        System.out.println("----------------------");
    }
}