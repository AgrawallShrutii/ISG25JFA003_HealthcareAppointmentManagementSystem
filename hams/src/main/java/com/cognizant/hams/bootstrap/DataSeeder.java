package com.cognizant.hams.bootstrap;

import com.cognizant.hams.entity.Role;
import com.cognizant.hams.entity.User;
import com.cognizant.hams.repository.RoleRepository;
import com.cognizant.hams.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        Role role = roleRepository.findByName("PATIENT").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("PATIENT");
            newRole.setDescription("Standard user role for patients.");
            return roleRepository.save(newRole);
        });

        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ADMIN");
            newRole.setDescription("Administrator role with full access.");
            return roleRepository.save(newRole);
        });

        // Create an initial admin user if one doesn't exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            // IMPORTANT: Change this password in a real application!
            adminUser.setPassword(passwordEncoder.encode("admin@123"));
            adminUser.setRole(adminRole);
            userRepository.save(adminUser);
        }
    }
}