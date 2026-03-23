package com.example.bai2.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String fullname;

    @Column(nullable = false, length = 20)
    private String role = "USER"; // "ADMIN" hoặc "USER"

    /**
     * Kiểm tra user có phải admin không.
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
