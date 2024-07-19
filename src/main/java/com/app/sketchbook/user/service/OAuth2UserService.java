package com.app.sketchbook.user.service;

import com.app.sketchbook.user.DTO.CustomOAuth2User;
import com.app.sketchbook.user.DTO.NaverResponse;
import com.app.sketchbook.user.DTO.OAuth2Response;
import com.app.sketchbook.user.DTO.UserDTO;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Log4j2
public class OAuth2UserService extends DefaultOAuth2UserService {
    //DefaultOAuth2UserService OAuth2UserService의 구현체

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {

            //oAuth2Response = new GoogleReponse(oAuth2User.getAttributes());
        } else {

            return null;
        }
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        String email = oAuth2Response.getEmail();
        String social = oAuth2Response.getProvider();

        //DB저장
        SketchUser existData = userRepository.findByEmailAndSocial(email, social);
        log.info(existData);
        String role = "ROLE_USER";
        if (existData == null) {

            SketchUser sketchUser = new SketchUser();
            sketchUser.setUsername(oAuth2Response.getName());
            sketchUser.setEmail(oAuth2Response.getEmail());
            sketchUser.setRole(role);
            sketchUser.setPassword(null);
            sketchUser.setSocial(oAuth2Response.getProvider());

            userRepository.save(sketchUser);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(userDTO);
        }
//        else {
//
//            existData.setUsername(username);
//            existData.setEmail(oAuth2Response.getEmail());
//
//            role = existData.getRole();
//
//            userRepository.save(existData);
//        }
        else{
            return null;
        }
    }
}