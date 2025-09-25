package com.cognizant.hams.service;

import com.cognizant.hams.dto.Request.AdminUserRequestDTO;
import com.cognizant.hams.dto.Request.AuthRequest;
import com.cognizant.hams.entity.Doctor;
import com.cognizant.hams.entity.Patient;
import com.cognizant.hams.entity.Role;
import com.cognizant.hams.entity.User;
import com.cognizant.hams.exception.InvalidCredentialsException;
import com.cognizant.hams.exception.UserAlreadyExistsException;
import com.cognizant.hams.repository.DoctorRepository;
import com.cognizant.hams.repository.PatientRepository;
import com.cognizant.hams.repository.RoleRepository;
import com.cognizant.hams.repository.UserRepository;
import com.cognizant.hams.security.JwtTokenUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;

    public AuthService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,DoctorRepository doctorRepository,PatientRepository patientRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.doctorRepository=doctorRepository;
        this.patientRepository=patientRepository;
    }

    public String createAuthenticationToken(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new InvalidCredentialsException("USER_DISABLED");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("INVALID_CREDENTIALS");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtTokenUtil.generateToken(userDetails);
    }

    public Patient registerNewUser(AuthRequest requestedUser) {
        Optional<User> existingUser = userRepository.findByUsername(requestedUser.getUsername());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User with this username already exists.");
        }

        Role role = roleRepository.findByName("PATIENT")
                .orElseThrow(() -> new RuntimeException("Default role 'PATIENT' not found. Please seed the database."));
        User newUser = new User();
        newUser.setUsername(requestedUser.getUsername());
        newUser.setPassword(requestedUser.getPassword());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRole(role);
        User savedUser=userRepository.save(newUser);
        Patient patient=new Patient();
        patient.setUser(newUser);
        patient.setEmail(requestedUser.getEmail());
        patient.setContactNumber(requestedUser.getContactNumber());
        patient.setAddress(requestedUser.getAddress());
        patient.setName(requestedUser.getName());
        patient.setGender(requestedUser.getGender());
        patient.setBloodGroup(requestedUser.getBloodGroup());
        patient.setDateOfBirth(requestedUser.getDateOfBirth());
        return patientRepository.save(patient);

    }
    @Transactional
    public Doctor createPrivilegedUser(AdminUserRequestDTO doctorDTO)
    {
        Optional<User> existingUser = userRepository.findByUsername(doctorDTO.getUsername());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User with this username already exists.");
        }

        Role role = roleRepository.findByName(doctorDTO.getRoleName().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role '" + doctorDTO.getRoleName() + "' not found."));

        User newUser = new User();

        newUser.setUsername(doctorDTO.getUsername());
        newUser.setPassword(doctorDTO.getPassword());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRole(role);
        User savedUser=userRepository.save(newUser);

        Doctor doctor=new Doctor();
        doctor.setUser(savedUser);
        doctor.setContactNumber(doctorDTO.getContactNumber());
        doctor.setEmail(doctorDTO.getEmail());
        doctor.setDoctorName(doctorDTO.getDoctorName());
        doctor.setQualification(doctorDTO.getQualification());
        doctor.setClinicAddress(doctorDTO.getClinicAddress());
        doctor.setYearOfExperience(doctorDTO.getYearOfExperience());
        doctor.setSpecialization(doctorDTO.getSpecialization());
        Doctor savedDoctor = doctorRepository.save(doctor);
        return  savedDoctor;
    }
}