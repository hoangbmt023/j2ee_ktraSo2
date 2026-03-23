package com.example.bai2.services;

import com.example.bai2.models.User;
import com.example.bai2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Đăng ký: hash mật khẩu bằng BCrypt rồi lưu vào DB.
     * Trả về null nếu username đã tồn tại.
     */
    public User register(String username, String rawPassword, String fullname) {
        if (userRepository.existsByUsername(username)) {
            return null;
        }
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setFullname(fullname);
        return userRepository.save(user);
    }

    /**
     * Đăng nhập: tìm user theo username, so sánh hash mật khẩu.
     * Trả về User nếu hợp lệ, null nếu sai.
     */
    public User login(String username, String rawPassword) {
        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                return user;
            }
        }
        return null;
    }
}
