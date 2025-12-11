package com.example.clientcommande.controller;

import com.example.clientcommande.dto.AuthRequest;
import com.example.clientcommande.dto.AuthResponse;
import com.example.clientcommande.dto.RefreshRequest;
import com.example.clientcommande.dto.RegisterRequest;
import com.example.clientcommande.model.AppUser;
import com.example.clientcommande.model.Role;
import com.example.clientcommande.repository.AppUserRepository;
import com.example.clientcommande.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          AppUserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ======================
    //      LOGIN
    // ======================
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    );

            authenticationManager.authenticate(authToken);

            String accessToken = jwtService.generateAccessToken(request.getUsername());
            String refreshToken = jwtService.generateRefreshToken(request.getUsername());

            return new AuthResponse(accessToken, refreshToken);

        } catch (AuthenticationException e) {
            throw new RuntimeException("Identifiants invalides");
        }
    }

    // ======================
    //     REGISTER
    // ======================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Nom d'utilisateur déjà utilisé");
        }

        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Utilisateur créé avec succès. Vous pouvez maintenant vous connecter.");
    }

    // Admin
    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Nom d'utilisateur déjà utilisé");
        }

        AppUser admin = AppUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);

        return ResponseEntity.ok("Administrateur créé avec succès !");
    }

    // ======================
    //   REFRESH TOKEN
    // ======================
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        try {
            String username = jwtService.extractUsername(refreshToken);

            AppUser user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            // On vérifie que le refresh token est encore valide
            if (!jwtService.isTokenValid(refreshToken, (UserDetails) user)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Refresh token invalide ou expiré");
            }

            String newAccessToken = jwtService.generateAccessToken(username);

            // Ici on garde le même refreshToken (pas de rotation avancée)
            return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token invalide");
        }
    }
}
