public class Ex5 {
    public static void main(String[] args) {
        Book book = new Book(
                "B01",
                "Lập trình Java",
                "Nguyễn Đức Anh",
                120000
        );
        book.displayInfo();
    }
}
class Book {
    private String bookId;
    private String title;
    private String author;
    private double price;

    public Book(String bookId, String title, String author, double price) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public void displayInfo() {
        System.out.println("Book ID: " + bookId);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Price: " + price);
        System.out.println("----------------------");
    }
}