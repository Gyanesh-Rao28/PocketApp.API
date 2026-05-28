package com.gyanesh.pocketManager.controller;

import com.gyanesh.pocketManager.dto.AuthDto;
import com.gyanesh.pocketManager.dto.ProfileDto;
import com.gyanesh.pocketManager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDto> registrationProfile(@RequestBody ProfileDto profileDto){
        ProfileDto registeredProfile = profileService.registrationProfile(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam("token") String token){

        Boolean isActivated = profileService.activateProfile(token);

        if(isActivated){
            log.info("Profile Activated: Activation Token = {}", token);
            return ResponseEntity.ok("Profile Activated Successfully : activation token = " + token);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Activation Token or Already Activate");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDto authDto){

        try{

            if(!profileService.isProfileActive(authDto.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("Message", "Account is not Activated. Please activate your account."));
            }

            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDto);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "Message",e.getMessage()
            ));
        }
    }

}
