package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Role;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.domain.VerificationToken;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.exception.UserAlreadyExistException;
import bnorbert.onlineshop.repository.UserRepository;
import bnorbert.onlineshop.repository.VerificationTokenRepository;
import bnorbert.onlineshop.transfer.user.login.AuthResponse;
import bnorbert.onlineshop.transfer.user.login.LoginRequest;
import bnorbert.onlineshop.transfer.user.request.ResendTokenRequest;
import bnorbert.onlineshop.transfer.user.request.ResetPasswordRequest;
import bnorbert.onlineshop.transfer.user.request.SaveUserRequest;
import bnorbert.onlineshop.transfer.user.request.VerifyTokenRequest;
import bnorbert.onlineshop.transfer.user.response.RoleResponse;
import bnorbert.onlineshop.transfer.user.response.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       VerificationTokenRepository verificationTokenRepository, AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    private boolean emailExists(final String email){
        return userRepository.findByEmail(email).isPresent();
    }

    public UserResponse createUser(SaveUserRequest request){
        LOGGER.info("Creating user: {}", request);
        if (emailExists(request.getEmail())) {
            throw new UserAlreadyExistException("That email is taken. Try another."); }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPasswordConfirm(passwordEncoder.encode(request.getPasswordConfirm()));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(false);
        user.setCreatedDate(Instant.now());

        VerificationToken verificationToken = new VerificationToken(user);
        user.addToken(verificationToken);

        User savedUser = userRepository.save(user);
        return mapUserResponse(savedUser);
    }

    private UserResponse mapUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setPassword(user.getPassword());

        for (Role role : user.getRoles()) {
            RoleResponse roleResponse = new RoleResponse();
            roleResponse.setId(role.getId());
            roleResponse.setName(role.getName());

            userResponse.getRoles().add(roleResponse);
        }
        return userResponse;
    }

    @Transactional
    public UserResponse resendToken(ResendTokenRequest request) {
        User user = userRepository.
                findByEmail(request.getEmail()).orElseThrow(() ->
                new ResourceNotFoundException("User " + request.getEmail() + " not found."));

        VerificationToken verificationToken = new VerificationToken(user);
        user.addToken(verificationToken);


        User savedUser = userRepository.save(user);
        return mapUserResponse(savedUser);
    }

    @Transactional
    public UserResponse confirmUser(VerifyTokenRequest request) {
        VerificationToken verificationToken = verificationTokenRepository
                .findByVerificationToken(request.getVerificationToken()).orElseThrow(() ->
                        new ResourceNotFoundException("Token " + request.getVerificationToken() + " not found."));
        User user = verificationToken.getUser();
        user.setEnabled(true);

        if (currentTime().isAfter(verificationToken.getExpirationDate())){
            throw new ResourceNotFoundException("Token " + request.getVerificationToken() + "has expired");
        }

        User savedUser = userRepository.save(user);
        return mapUserResponse(savedUser);
    }


    @Transactional
    public UserResponse getUserId(long userId) {
        LOGGER.info("Retrieving user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User" + userId + " id not found."));

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());

        for (Role role : user.getRoles()) {
            RoleResponse roleResponse = new RoleResponse();
            roleResponse.setId(role.getId());
            roleResponse.setName(role.getName());

            userResponse.getRoles().add(roleResponse);
        }
            return userResponse;
    }

    public User getUser(long id) {
        LOGGER.info("Retrieving user {}", id);
        return userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User" + id + " not found."));
    }

    @Transactional
    public UserResponse resetPassword(ResetPasswordRequest request) {
        LOGGER.info("Changing password for user {}", request);
        VerificationToken verificationToken = verificationTokenRepository
                .findByVerificationToken(request.getVerificationToken()).orElseThrow(() ->
                        new ResourceNotFoundException("Token " + request.getVerificationToken() + " not found."));
        User user = verificationToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPasswordConfirm(passwordEncoder.encode(request.getPasswordConfirm()));

        if (currentTime().isAfter(verificationToken.getExpirationDate())){
            throw new ResourceNotFoundException("Token " + request.getVerificationToken() + "has expired");
        }

        User savedUser = userRepository.save(user);
        return mapUserResponse(savedUser);
    }

    public void deleteUser(long id){
        LOGGER.info("Deleting user {}", id);
        userRepository.deleteById(id);
    }


    public AuthResponse login(LoginRequest request) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String authToken = jwtProvider.generateToken(authenticate);
        return new AuthResponse(authToken, request.getEmail());
    }


    @Transactional
    public User getCurrentUser() {
        User principal = (User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User email not found - " + principal.getEmail()));
    }


    //ON DELETE cascade(foreign keys) - ConstraintViolationException
    @Scheduled(cron = "0 0 0 * * ?")//Every day at midnight - 12am
    public void removeNotActivatedUsers() {
        userRepository
                .findAllByEnabledIsFalseAndCreatedDateBefore(Instant.now().minus(5, ChronoUnit.DAYS))
                .forEach(userRepository::delete);
        LOGGER.info("Schedule: Deleting not activated users");
    }


    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }

    private LocalDateTime currentTime() { return LocalDateTime.now(); }

}
