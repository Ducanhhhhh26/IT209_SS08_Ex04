package springboot.btth_ss12;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BtthSs12Application {

    public static void main(String[] args) {
        SpringApplication.run(BtthSs12Application.class, args);
        System.out.println("Hospital Web Service is ready on Embedded Tomcat!");
    }

}
