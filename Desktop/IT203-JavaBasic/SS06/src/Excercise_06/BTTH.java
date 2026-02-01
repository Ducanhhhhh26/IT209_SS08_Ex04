public class BTTH {
    public static void main(String[] args) {

        // Tạo đối tượng thứ nhất
        BankAccount account1 = new BankAccount("001", "Nguyễn Văn A", 5000);
        account1.deposit(2000);
        account1.withdraw(1000);
        account1.displayInfo();

        // Tạo đối tượng thứ hai
        BankAccount account2 = new BankAccount("002", "Trần Thị B", 3000);
        account2.withdraw(500);
        account2.deposit(1000);
        account2.displayInfo();
    }
}

class BankAccount {

    // Thuộc tính (đóng gói)
    private String accountNumber;
    private String ownerName;
    private double balance;

    // Constructor không tham số
    public BankAccount() {
        this.accountNumber = "Chưa có";
        this.ownerName = "Chưa có";
        this.balance = 0;
    }

    // Constructor có tham số
    public BankAccount(String accountNumber, String ownerName, double balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        if (balance >= 0) {
            this.balance = balance;
        } else {
            this.balance = 0;
        }
    }

    // Getter & Setter
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        if (balance >= 0) {
            this.balance = balance;
        } else {
            System.out.println("Số dư không hợp lệ!");
        }
    }

    // Nạp tiền
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        } else {
            System.out.println("Số tiền nạp không hợp lệ!");
        }
    }

    // Rút tiền
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
        } else {
            System.out.println("Rút tiền không hợp lệ hoặc không đủ số dư!");
        }
    }

    // Hiển thị thông tin
    public void displayInfo() {
        System.out.println("Số tài khoản: " + accountNumber);
        System.out.println("Chủ tài khoản: " + ownerName);
        System.out.println("Số dư: " + balance);
        System.out.println("----------------------------");
    }
}