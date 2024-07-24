package com.app.sketchbook.user.service;

import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();;

    @Autowired
    private JavaMailSender mailSender;

    public SketchUser createUser(String username, String email, String password, String gender){
        SketchUser user = new SketchUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setGender(gender);
        user.setEnabled(false);
        user.setAuthCode(UUID.randomUUID().toString());

        userRepository.save(user);

        sendVerificationEmail(user);

        return user;
    }

    public void sendVerificationEmail(SketchUser user) {
        String subject = "Email Verification";
        String message = "Your verification code is: " + user.getAuthCode();

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(message, true);
            helper.setFrom("mailsender123@naver.com");

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyUser(String code) {
        Optional<SketchUser> userOptional = userRepository.findByAuthCode(code);
        if (userOptional.isPresent()) {
            SketchUser user = userOptional.get();
            user.setEnabled(true);
            user.setAuthCode(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean isEmailExists(String email) {
        SketchUser user = userRepository.findByEmail(email);
        if(user==null)
            return false;
        else
            return true;
    }

    public SketchUser findUser(String email){
        SketchUser user = userRepository.findByEmail(email);
        if(user==null)
            return null;
        else
            return user;
    }
}
