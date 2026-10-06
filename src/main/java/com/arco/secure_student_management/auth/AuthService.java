package com.arco.secure_student_management.auth;

import com.arco.secure_student_management.security.JwtService;
import com.arco.secure_student_management.user.Role;
import com.arco.secure_student_management.user.User;
import com.arco.secure_student_management.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Register a new user.
     */
    @Transactional
    public User register(RegisterRequest request) {

        // Check whether username already exists
        if (userRepository.existsByUsername(
                request.getUsername()
        )) {
            throw new IllegalArgumentException(
                    "Username is already registered"
            );
        }

        // Check whether email already exists
        if (userRepository.existsByEmail(
                request.getEmail()
        )) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        // Create new user
        User user = new User();

        user.setUsername(
                request.getUsername()
        );

        user.setEmail(
                request.getEmail()
        );

        // Never store a plain-text password
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        // Every newly registered user gets USER role
        user.setRole(Role.USER);

        // Enable the account
        user.setEnabled(true);

        return userRepository.save(user);
    }

    /**
     * Authenticate a user and generate
     * an access token and refresh token.
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {

        /*
         * AuthenticationManager verifies:
         *
         * username
         * password
         *
         * against Spring Security's UserDetailsService.
         */
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        /*
         * Find the actual User entity after
         * successful authentication.
         */
        User user =
                userRepository
                        .findByUsername(
                                authentication.getName()
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        // Generate JWT access token
        String accessToken =
                jwtService.generateToken(user);

        // Generate refresh token
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        /*
         * Return both tokens to the client.
         */
        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtService.getExpirationInSeconds()
        );
    }

    /**
     * Generate a new access token using
     * an existing refresh token.
     */
    @Transactional
    public LoginResponse refreshToken(
            String token
    ) {

        // Find refresh token in database
        RefreshToken refreshToken =
                refreshTokenService.findByToken(token);

        // Check whether token is expired or revoked
        refreshTokenService.verifyExpiration(
                refreshToken
        );

        // Get the user associated with the refresh token
        User user =
                refreshToken.getUser();

        // Generate a new access token
        String accessToken =
                jwtService.generateToken(user);

        /*
         * Return the new access token.
         *
         * The existing refresh token remains valid
         * until it expires or is revoked.
         */
        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtService.getExpirationInSeconds()
        );
    }

    /**
     * Revoke the refresh token during logout.
     */
    @Transactional
    public void logout(String token) {

        refreshTokenService.revokeToken(token);
    }
}

    

