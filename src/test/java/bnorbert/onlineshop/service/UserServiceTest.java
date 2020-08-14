package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.domain.VerificationToken;
import bnorbert.onlineshop.repository.UserRepository;
import bnorbert.onlineshop.repository.VerificationTokenRepository;
import bnorbert.onlineshop.transfer.user.login.AuthResponse;
import bnorbert.onlineshop.transfer.user.login.LoginRequest;
import bnorbert.onlineshop.transfer.user.request.ResendTokenRequest;
import bnorbert.onlineshop.transfer.user.request.ResetPasswordRequest;
import bnorbert.onlineshop.transfer.user.request.SaveUserRequest;
import bnorbert.onlineshop.transfer.user.request.VerifyTokenRequest;
import bnorbert.onlineshop.transfer.user.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private VerificationTokenRepository mockVerificationTokenRepository;
    @Mock
    private AuthenticationManager mockAuthenticationManager;
    @Mock
    private JwtProvider mockJwtProvider;

    private UserService userServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        userServiceUnderTest = new UserService(mockUserRepository, mockPasswordEncoder, mockVerificationTokenRepository, mockAuthenticationManager, mockJwtProvider);
    }

    @Test
    void testCreateUser() {
        final SaveUserRequest request = new SaveUserRequest();
        request.setEmail("email@gmail.com");
        request.setPassword("password");
        request.setPasswordConfirm("password");

        when(mockUserRepository.findByEmail("email@gmail.com")).thenReturn(Optional.of(new User()));
        when(mockPasswordEncoder.encode("charSequence")).thenReturn("result");
        when(mockUserRepository.save(new User())).thenReturn(new User());

        final UserResponse result = userServiceUnderTest.createUser(request);

    }

    @Test
    void testResendToken() {

        final ResendTokenRequest request = new ResendTokenRequest();
        request.setEmail("email");

        when(mockUserRepository.findByEmail("email")).thenReturn(Optional.of(new User()));
        when(mockUserRepository.save(new User())).thenReturn(new User());


        final UserResponse result = userServiceUnderTest.resendToken(request);
    }

    @Test
    void testConfirmUser() {

        final VerifyTokenRequest request = new VerifyTokenRequest();
        request.setVerificationToken("verificationToken");

        final Optional<VerificationToken> verificationToken = Optional.of(new VerificationToken(new User()));
        when(mockVerificationTokenRepository.findByVerificationToken("verificationToken")).thenReturn(verificationToken);

        when(mockUserRepository.save(new User())).thenReturn(new User());


        final UserResponse result = userServiceUnderTest.confirmUser(request);

    }

    @Test
    void testGetUserId() {

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(new User()));

        final UserResponse result = userServiceUnderTest.getUserId(1L);
    }

    @Test
    void testGetUserIdThenReturnResourceNotFoundException() {

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(new User()));

        final UserResponse result = userServiceUnderTest.getUserId(2L);
    }

    @Test
    void testGetUser() {

        final User expectedResult = new User();
        when(mockUserRepository.findById(0L)).thenReturn(Optional.of(new User()));

        final User result = userServiceUnderTest.getUser(0L);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testResetPassword() {

        final ResetPasswordRequest request = new ResetPasswordRequest();
        request.setPassword("password");
        request.setPasswordConfirm("password");
        request.setVerificationToken("verificationToken");

        final Optional<VerificationToken> verificationToken = Optional.of(new VerificationToken(new User()));
        when(mockVerificationTokenRepository.findByVerificationToken("verificationToken")).thenReturn(verificationToken);

        when(mockPasswordEncoder.encode("charSequence")).thenReturn("result");
        when(mockUserRepository.save(new User())).thenReturn(new User());


        final UserResponse result = userServiceUnderTest.resetPassword(request);
    }

    @Test
    void testDeleteUser() {

        userServiceUnderTest.deleteUser(1L);

        verify(mockUserRepository).deleteById(1L);
    }

    @Test
    void testLogin() {

        final LoginRequest request = new LoginRequest();
        request.setEmail("email");
        request.setPassword("password");

        when(mockAuthenticationManager.authenticate(null)).thenReturn(null);
        when(mockJwtProvider.generateToken(null)).thenReturn("result");


        final AuthResponse result = userServiceUnderTest.login(request);
    }

    @Test
    void testLogin_AuthenticationManagerThrowsAuthenticationException() {

        final LoginRequest request = new LoginRequest();
        request.setEmail("email");
        request.setPassword("password");

        when(mockAuthenticationManager.authenticate(null)).thenThrow(AuthenticationException.class);
        when(mockJwtProvider.generateToken(null)).thenReturn("result");


        final AuthResponse result = userServiceUnderTest.login(request);
    }

    @Test
    void testGetCurrentUser() {

        final User expectedResult = new User();
        when(mockUserRepository.findByEmail("email")).thenReturn(Optional.of(new User()));

        final User result = userServiceUnderTest.getCurrentUser();

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testIsLoggedIn() {

        final boolean result = userServiceUnderTest.isLoggedIn();

        assertThat(result).isTrue();
    }
}
