package com.EmailVerification.user;

import com.EmailVerification.exception.UserAlreadyExistsException;
import com.EmailVerification.registration.RegistrationRequest;
import com.EmailVerification.registration.token.VerificationToken;
import com.EmailVerification.registration.token.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserService implements  IUserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest request) {
        //check if the user exists in the database
        Optional<User> user= this.findByEmail(request.email());
        if (user.isPresent()){
            throw  new UserAlreadyExistsException("user with email" +request.email()+ "already exists");

        }
        var newUser= new User();
        newUser.setFirstName(request.firstName());
        newUser.setLastName(request.lastName());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(request.role());
        return userRepository.save(newUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveUserVerificationToken(User theUser, String token) {
        var verificationToken= new VerificationToken(token, theUser);
        tokenRepository.save(verificationToken);


    }

    @Override
    public String validateToken(String theToken) {
        VerificationToken token= tokenRepository.findByToken(theToken);
        if (token==null){
        return "invalid verification token";
    }
        User user= token.getUser();
        Calendar calendar=Calendar.getInstance();
        if ((token.getTokenExpirationTime().getTime()-calendar.getTime().getTime())<=0){
            tokenRepository.delete(token);
            return "token already expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }
}
