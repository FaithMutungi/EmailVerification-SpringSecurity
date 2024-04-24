package com.EmailVerification.registration;

import com.EmailVerification.event.RegistrationCompleteEvent;
import com.EmailVerification.registration.token.VerificationToken;
import com.EmailVerification.registration.token.VerificationTokenRepository;
import com.EmailVerification.user.User;
import com.EmailVerification.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository tokenRepository;

    @PostMapping
    public String registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest request){
        User user=userService.registerUser(registrationRequest);
        publisher.publishEvent(new RegistrationCompleteEvent(user,applicationUrl(request)));
        return "Success! please check your email to complete your registration";

    }
    @GetMapping("/verifyEmail")
    public String verifyToken(@RequestParam("token") String token){
        VerificationToken theToken= tokenRepository.findByToken(token);
        if (theToken.getUser().isEnabled()){
            return "this account has already been verified, please login";
        }
        String verificationResult= userService.validateToken(token);
        if (verificationResult.equalsIgnoreCase("valid")){
            return "email verified successfully. Now you can login to your account";
        }
        return "invalid verification token";

    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort()+request.getContextPath();
    }

}
