package com.gyanesh.pocketManager.service;

import com.gyanesh.pocketManager.dto.AuthDto;
import com.gyanesh.pocketManager.dto.ProfileDto;
import com.gyanesh.pocketManager.entity.ProfileEntity;
import com.gyanesh.pocketManager.repository.ProfileRepository;
import com.gyanesh.pocketManager.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final ProfileRepository profileRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    @Value("${pocket.activation.url}")
    private String backendUrl;


    public ProfileDto registrationProfile(ProfileDto profileDto){

        ProfileEntity newProfile = toEntity(profileDto);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepo.save(newProfile);

        // Send Activation email
        String activationLink= backendUrl+"/api/v1/activate?token=" + newProfile.getActivationToken();
        String subject="Activate Your Pocket Manager Account";
        String body="Click on the following link to activate your account: " + activationLink;

        emailService.sendEmail(newProfile.getEmail(), subject, body);

        return toDto(newProfile);

    }

    public ProfileEntity toEntity(ProfileDto profileDto){

        return ProfileEntity.builder()
                .id(profileDto.getId())
                .fullName(profileDto.getFullName())
                .email(profileDto.getEmail())
                .password(passwordEncoder.encode(profileDto.getPassword()))
                .profileImageUrl(profileDto.getProfileImageUrl())
                .createdAt(profileDto.getCreatedAt())
                .updatedAt(profileDto.getUpdatedAt())
                .build();

    }

    public ProfileDto toDto(ProfileEntity profileEntity){
        return ProfileDto.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public Boolean activateProfile(String token){

        return profileRepo.findByActivationToken(token)
                .map(profile ->{

                    if(profile.getIsActive()==true){
                        return false;
                    }

                    profile.setIsActive(true);
                    profileRepo.save(profile);
                    return true;
                })
                .orElse(false);
    }

    public Boolean isProfileActive(String email){
        return profileRepo.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        return profileRepo.findByEmail(auth.getName()).orElseThrow(()->new UsernameNotFoundException("Profile Not Found"));
    }

    public ProfileDto getPublicProfile(String email){

        ProfileEntity currProfile = null;
        if(email==null){
            currProfile = getCurrentProfile();
        }else{
            currProfile = profileRepo.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Profile Not Found"));
        }

        return toDto(currProfile);
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto){

        try{

            authManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));
            String JwtToken = jwtUtil.generateAccessToken(authDto);

            return  Map.of(
                    "token",JwtToken,
                    "User",getPublicProfile(authDto.getEmail())
            );

        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid email or Password");
        }
    }


}
