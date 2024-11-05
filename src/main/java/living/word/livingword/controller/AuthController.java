package living.word.livingword.controller;

import living.word.livingword.entity.Role;
import living.word.livingword.entity.User;
import living.word.livingword.model.dto.*;
import living.word.livingword.repository.AppUserRepository;
import living.word.livingword.security.JwtUtils;
import living.word.livingword.service.EmailService;
import living.word.livingword.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AppUserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private RoleService roleService;
    @Autowired
    private EmailService emailService;


    // Registro de usuarios
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Crear nuevo usuario
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setAddress(signUpRequest.getAddress());
        user.setDateBirth(signUpRequest.getDateBirth());
        user.setGender(signUpRequest.getGender());
        user.setPhone(signUpRequest.getPhone());
        user.setMaritalstatus(signUpRequest.getMaritalstatus());
        user.setLastname(signUpRequest.getLastname());

        // Asignar rol dinámico
        try {
            Role role = roleService.getRoleByName(signUpRequest.getRole().toUpperCase())
                    .orElseThrow(() -> new IllegalArgumentException("Error: Role not found."));
            user.setRole(role);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: " + e.getMessage());
        }

        // Generate Token verification email
        String verificationToken = jwtUtils.generateVerificationToken(user);
        user.setVerificationToken(verificationToken);

        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        return ResponseEntity.ok("Successfully registered user! Please check your email to verify your account.");
    }

    // Inicio de sesión
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();

            String jwt = jwtUtils.generateJwtToken(user);

            Set<String> permissions = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            // Obtén el nombre del ministerio si existe
            String ministry = (user.getMinistry() != null) ? user.getMinistry().getName() : null;

            return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getName(), user.getEmail(),
                    user.getRole().getName(), permissions, ministry));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String token) {
        String email = jwtUtils.getEmailFromVerificationToken(token);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isVerified()) {
                user.setVerified(true);
                user.setVerificationToken(null); // Eliminar el token después de la verificación
                userRepository.save(user);
                return ResponseEntity.ok("Account verified successfully!");
            } else {
                return ResponseEntity.badRequest().body("Account is already verified.");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid verification token.");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String resetPasswordToken = jwtUtils.generateResetPasswordToken(user);
            user.setResetPasswordToken(resetPasswordToken);
            userRepository.save(user);

            // Enviar el email de recuperación de contraseña
            emailService.sendResetPasswordEmail(user.getEmail(), resetPasswordToken);

            return ResponseEntity.ok("Reset password email sent.");
        } else {
            return ResponseEntity.badRequest().body("Email not found.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                           @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        String email = jwtUtils.getEmailFromResetPasswordToken(token);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(encoder.encode(resetPasswordRequest.getNewPassword())); // Establecer nueva contraseña
            user.setResetPasswordToken(null); // Eliminar el token después de cambiar la contraseña
            userRepository.save(user);

            return ResponseEntity.ok("Password reset successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid reset password token.");
        }
    }

    @GetMapping("/reset-password-form")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);  // Pasamos el token para el formulario
        return "reset-password";  // Nombre de la plantilla Thymeleaf
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody SignupRequest updateRequest) {
        Optional<User> userOptional = userRepository.findByEmail(updateRequest.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(updateRequest.getName());
            user.setPassword(encoder.encode(updateRequest.getPassword()));
            user.setDateBirth(updateRequest.getDateBirth());
            user.setPhone(updateRequest.getPhone());
            user.setAddress(updateRequest.getAddress());
            user.setMaritalstatus(updateRequest.getMaritalstatus());
            user.setGender(updateRequest.getGender());
            user.setLastname(updateRequest.getLastname());

            userRepository.save(user);

            return ResponseEntity.ok("User updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Error: User not found.");
        }
    }



}