package com.app.sketchbook;

import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class SketchBookApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        SketchUser sketchUser = new SketchUser();
        sketchUser.setUsername("TEST");
        sketchUser.setPassword("test");
        sketchUser.setRole("ROLE_ADMIN");
        this.userRepository.save(sketchUser);
    }

}
