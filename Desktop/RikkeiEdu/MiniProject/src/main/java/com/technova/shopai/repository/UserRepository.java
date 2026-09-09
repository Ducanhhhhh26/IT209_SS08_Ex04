package com.technova.shopai.repository;

import com.technova.shopai.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Dùng @EntityGraph để lấy danh sách User kèm Roles, tránh lỗi N+1 Query khi hiển thị Admin Panel
    @EntityGraph(attributePaths = {"roles"})
    List<User> findAll();
}
