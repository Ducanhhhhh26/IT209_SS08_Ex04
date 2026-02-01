public class Ex6 {
    public static void main(String[] args) {
        User u1 = new User(1, "huy123", "123456", "huy@gmail.com");
        u1.displayInfo();
        User u2 = new User(2, "user02", "", "user02@gmail.com");
        u2.displayInfo();
        User u3 = new User(3, "user03", "abcdef", "user03gmail.com");
        u3.displayInfo();
        System.out.println("Thử set email sai:");
        u1.setEmail("abc@");
        u1.displayInfo();
    }
}

class User {
    private int id;
    private String username;
    private String password;
    private String email;

    public User(int id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        setPassword(password);
        setEmail(email);
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.password = password;
        } else {
            System.out.println("Password không được rỗng");
            this.password = "default123";
        }
    }

    public void setEmail(String email) {
        if (email != null && email.contains("@") && email.contains(".")) {
            this.email = email;
        } else {
            System.out.println("Email không hợp lệ");
            this.email = "unknown@email.com";
        }
    }

    public void displayInfo() {
        System.out.println("ID: " + id);
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Password: ******");
        System.out.println("----------------------");
    }
}