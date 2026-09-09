package com.technova.shopai;

import com.technova.shopai.entity.Product;
import com.technova.shopai.entity.Role;
import com.technova.shopai.entity.User;
import com.technova.shopai.repository.ProductRepository;
import com.technova.shopai.repository.RoleRepository;
import com.technova.shopai.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseLoader(RoleRepository roleRepository,
                          UserRepository userRepository,
                          ProductRepository productRepository,
                          PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Khởi tạo Roles nếu chưa có
        initRoleIfNotExist("ROLE_CUSTOMER");
        initRoleIfNotExist("ROLE_STAFF");
        initRoleIfNotExist("ROLE_MANAGER");

        // 2. Khởi tạo Users nếu chưa có
        initUserIfNotExist("customer", "customer@technova.com", "password", "ROLE_CUSTOMER", "Nguyen Customer");
        initUserIfNotExist("staff", "staff@technova.com", "password", "ROLE_STAFF", "Tran Warehouse Staff");
        initUserIfNotExist("manager", "manager@technova.com", "password", "ROLE_MANAGER", "Le Store Manager");

        // 3. Khởi tạo một số sản phẩm mẫu
        if (productRepository.count() == 0) {
            productRepository.save(Product.builder()
                    .name("iPhone 15 Pro Max")
                    .description("Apple iPhone 15 Pro Max 256GB - Titanium")
                    .price(new BigDecimal("1299.99"))
                    .stockQuantity(15)
                    .category("PHONE")
                    .status("ACTIVE")
                    .build());

            productRepository.save(Product.builder()
                    .name("Samsung Galaxy S24 Ultra")
                    .description("Samsung Galaxy S24 Ultra 512GB - Titanium Yellow")
                    .price(new BigDecimal("1199.50"))
                    .stockQuantity(20)
                    .category("PHONE")
                    .status("ACTIVE")
                    .build());

            productRepository.save(Product.builder()
                    .name("MacBook Pro 14 M3")
                    .description("Apple MacBook Pro 14 inch M3 Chip 16GB RAM 512GB SSD")
                    .price(new BigDecimal("1799.00"))
                    .stockQuantity(8)
                    .category("LAPTOP")
                    .status("ACTIVE")
                    .build());

            productRepository.save(Product.builder()
                    .name("Dell XPS 13 Plus")
                    .description("Dell XPS 13 Plus 9320 Core i7-1360P 16GB 512GB")
                    .price(new BigDecimal("1499.00"))
                    .stockQuantity(0) // Out of stock to test out of stock status
                    .category("LAPTOP")
                    .status("OUT_OF_STOCK")
                    .build());

            System.out.println(">>> Dữ liệu mẫu khởi tạo thành công!");
        }
    }

    private void initRoleIfNotExist(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = Role.builder().name(roleName).build();
            roleRepository.save(role);
        }
    }

    private void initUserIfNotExist(String username, String email, String rawPassword, String roleName, String fullName) {
        if (!userRepository.existsByUsername(username)) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found."));

            Set<Role> roles = new HashSet<>();
            roles.add(role);

            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .fullName(fullName)
                    .status("ACTIVE")
                    .roles(roles)
                    .build();

            userRepository.save(user);
        }
    }
}
