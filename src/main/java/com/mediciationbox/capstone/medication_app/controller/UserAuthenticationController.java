package com.mediciationbox.capstone.medication_app.controller;

import com.mediciationbox.capstone.medication_app.dto.LogInDTO;
import com.mediciationbox.capstone.medication_app.dto.ResponseDTO;
import com.mediciationbox.capstone.medication_app.model.User;
import com.mediciationbox.capstone.medication_app.repository.UserRepository;
import com.mediciationbox.capstone.medication_app.service.UserAuthenticationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserAuthenticationController {

    private UserRepository userRepository;
    private UserAuthenticationService userAuthenticationService;

    public UserAuthenticationController(UserRepository userRepository, UserAuthenticationService userAuthenticationService){
        this.userRepository = userRepository;
        this.userAuthenticationService = userAuthenticationService;
    }

    @GetMapping("/api/signup/user")
    public Page<User> users(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return userRepository.findAll(pageable);
    }

    //Initial Signup
    @PostMapping("/api/signup/user")
    public ResponseEntity<ResponseDTO> signup(@Validated @RequestBody User user){

        userAuthenticationService.ifAlreadyExists(user.getEmail());

        Map<String, Object> details = new HashMap<>();
        details.put("email", user.getEmail());
        details.put("password",user.getPassword());
        ResponseDTO response = new ResponseDTO(true, "success", details );

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);

    }

    //Signup for creating the user account
    @PostMapping("/api/add_user")
    public ResponseEntity<ResponseDTO> addNewUser(@Validated @RequestBody User user){
        userRepository.save(user);

        //Create a service method
        User account = userRepository.findByEmail(user.getEmail());

        ResponseDTO responseDTO = userAuthenticationService.signupProcess(account);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    //Login Controller
    @PostMapping("/api/login/user")
    public ResponseEntity<ResponseDTO>
    logInUser(@Validated @RequestBody LogInDTO accountCredentials){

        return new ResponseEntity<>(userAuthenticationService.verifyLogInCredentials(accountCredentials.email(), accountCredentials.password()), HttpStatus.ACCEPTED);

    }

    //Select by ID controller
    @GetMapping("/api/users/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        Optional<User> account = userRepository.findById(id);

        return new ResponseEntity<>(account, HttpStatus.ACCEPTED);

    }

    //Logout a user
    @PostMapping("/api/logout/{userId}")
    public ResponseEntity<?> logoutActiveUser(@PathVariable Long userId){
        return userAuthenticationService.logoutService(userId);
    }


}
