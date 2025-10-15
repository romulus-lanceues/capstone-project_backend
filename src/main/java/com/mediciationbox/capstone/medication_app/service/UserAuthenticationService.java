package com.mediciationbox.capstone.medication_app.service;

import com.mediciationbox.capstone.medication_app.dto.ResponseDTO;
import com.mediciationbox.capstone.medication_app.exception.AccountAlreadyExistsException;
import com.mediciationbox.capstone.medication_app.exception.NoExistingAccountException;
import com.mediciationbox.capstone.medication_app.exception.WrongPasswordException;
import com.mediciationbox.capstone.medication_app.model.ActiveUser;
import com.mediciationbox.capstone.medication_app.model.User;
import com.mediciationbox.capstone.medication_app.repository.ActiveUserRepository;
import com.mediciationbox.capstone.medication_app.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserAuthenticationService {

    private UserRepository userRepository;
    private JWTService jwtService;
    private ActiveUserRepository activeUserRepository;
    private PasswordEncoder passwordEncoder;

    public UserAuthenticationService(UserRepository userRepository, JWTService jwtService,
                                     ActiveUserRepository activeUserRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.activeUserRepository = activeUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void ifAlreadyExists(String email){
        //Custom methods don't require Optional
        User account = userRepository.findByEmail(email);

        if(account != null){  //! .equals() and == !
            throw new AccountAlreadyExistsException("Account Already Exists");
        }
    }

    //Login Service
    public ResponseDTO verifyLogInCredentials(String email, String password){
        User account = userRepository.findByEmail(email);

        //Validate credentials
        if(account == null) throw new NoExistingAccountException("No registered account for " + email);
        if(!passwordEncoder.matches(password, account.getPassword())) throw new WrongPasswordException("Enter the right password.");

        //Tell the database this is the active user
        ActiveUser activeAccount = activeUserRepository.findByUserId(account.getId());
        activeAccount.setActive(true);
        activeUserRepository.save(activeAccount);

        String token = jwtService.generateToken(email);

        Map<String, Object> details = new HashMap<>();
        details.put("email", account.getEmail());
        details.put("id", String.valueOf(account.getId()));
        details.put("token", token );

       return new ResponseDTO(true, "success", details);

    }

    //Signup service
    public ResponseDTO signupProcess(User account){

        //generate token
        String token = jwtService.generateToken(account.getEmail());

        createActiveUserEntryForAccount(account);

        Map<String, Object> accountInfos = new HashMap<>();

        accountInfos.put("id", String.valueOf(account.getId()));
        accountInfos.put("email", account.getEmail());
        accountInfos.put("name", account.getName());
        accountInfos.put("token", token);

        return new ResponseDTO(true, "success", accountInfos);
    }

    private ActiveUser createActiveUserEntryForAccount(User account){
        ActiveUser createdLoginSession = new ActiveUser(account.getEmail(), account.getId(), true);
        activeUserRepository.save(createdLoginSession);
        return createdLoginSession;
    }

    public ResponseEntity<?> logoutService(Long userId){
        ActiveUser activeUser = activeUserRepository.findByUserId(userId);
        activeUser.setActive(false);
        activeUserRepository.save(activeUser);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
