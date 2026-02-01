package Excercise_06;

public class Ex3 {
    public static void main(String[] args) {
        product p1 = new product();
        product p2 = new product("P001", "Laptop", 1500.00);
        product p3 = new product("P002", "Smartphone", -200.00);

        System.out.println("Product 1:");
        System.out.println("ID: " + p1.getId());
        System.out.println("Name: " + p1.getName());
        System.out.println("Price: " + p1.getPrice());

        System.out.println("\nProduct 2:");
        System.out.println("ID: " + p2.getId());
        System.out.println("Name: " + p2.getName());
        System.out.println("Price: " + p2.getPrice());

        System.out.println("\nProduct 3:");
        System.out.println("ID: " + p3.getId());
        System.out.println("Name: " + p3.getName());
        System.out.println("Price: " + p3.getPrice());
    }
}
class product {
    private String id;
    private String name;
    private double price;

    public product() {
    }

    public product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        setPrice(price);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price > 0) {
            this.price = price;
        } else {
            System.out.println("Gia ban khong hop le ");;
        }
    }
}