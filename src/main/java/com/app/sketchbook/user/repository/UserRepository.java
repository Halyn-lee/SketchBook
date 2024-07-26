package com.app.sketchbook.user.repository;

import com.app.sketchbook.user.entity.SketchUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SketchUser, Long> {
    SketchUser findByEmailAndSocial(String email, String social);
    SketchUser findByEmail(String email);
    Optional<SketchUser> findByAuthCode(String code);

}