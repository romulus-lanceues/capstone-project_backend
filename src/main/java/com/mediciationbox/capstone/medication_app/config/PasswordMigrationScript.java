package com.mediciationbox.capstone.medication_app.config;

import com.mediciationbox.capstone.medication_app.model.User;
import com.mediciationbox.capstone.medication_app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * One time component that'll be run to hash the password of existing users.
 */

//@Component
public class PasswordMigrationScript implements CommandLineRunner {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public PasswordMigrationScript(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception{
        //This method wil run once when the application starts.
        //(Comment it out or delete it after running.)

//        migratePasswords();

    }

    private void migratePasswords(){
        List<User> allUsers = userRepository.findAll();
        int migratedCount = 0;

        for(User user : allUsers){
            String currentPass = user.getPassword();

            if(!isPasswordHashed(currentPass)){
                String hashedPassword = passwordEncoder.encode(currentPass);
                user.setPassword(hashedPassword);
                userRepository.save(user);

                migratedCount++;
                System.out.println("Migrated password for user: " + user.getEmail());
            }

        }

        System.out.println("Migration complete for " + migratedCount + " users");
    }

    private boolean isPasswordHashed(String password){
        // BCrypt hashes are 60 characters long and start with $2a$, $2b$, or $2y$
//        return password != null && password.length() == 60 && password.matches("^\\$2[ayb]\\$.{56}$");
        return password != null &&
                password.length() == 60 &&
                password.matches("^\\$2[ayb]\\$.{56}$");
    }
}
