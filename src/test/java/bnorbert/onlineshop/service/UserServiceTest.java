package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.domain.VerificationToken;
import bnorbert.onlineshop.repository.UserRepository;
import bnorbert.onlineshop.repository.VerificationTokenRepository;
import bnorbert.onlineshop.transfer.user.login.AuthResponse;
import bnorbert.onlineshop.transfer.user.request.ResendTokenRequest;
import bnorbert.onlineshop.transfer.user.request.ResetPasswordRequest;
import bnorbert.onlineshop.transfer.user.request.SaveUserRequest;
import bnorbert.onlineshop.transfer.user.request.VerifyTokenRequest;
import bnorbert.onlineshop.transfer.user.response.UserResponse;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
        userServiceUnderTest = new UserService(mockUserRepository, mockPasswordEncoder, mockVerificationTokenRepository, mockAuthenticationManager, mockJwtProvider);
    }

    @Test()
    void testCreateUser_thenThrowUserAlreadyExist_exception() {
        final SaveUserRequest request = new SaveUserRequest();

        request.setEmail("email@gmail.com");
        request.setPassword("password");
        request.setPasswordConfirm("password");

        when(mockUserRepository.findByEmail("email@gmail.com")).thenReturn(Optional.of(new User()));
        when(mockPasswordEncoder.encode("password")).thenReturn("encodedPass");
        when(mockUserRepository.save(new User())).thenReturn(new User());

        final UserResponse result = userServiceUnderTest.createUser(request);

    }

    @Test()
    void testCreateUser() {
        SaveUserRequest request = new SaveUserRequest();

        request.setEmail("email@gmail.com");
        request.setPassword("password");
        request.setPasswordConfirm("password");

        User user = new User();
        user.setId(9999L);
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPasswordConfirm(request.getPasswordConfirm());
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(false);
        user.setCreatedDate(Instant.now());

        //when(mockUserRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(mockPasswordEncoder.encode("password")).thenReturn("encodedPass");
        //when(mockUserRepository.save(user)).thenReturn(user);
        userServiceUnderTest.createUser(request);
    }

    @Test
    void testResendToken() {

        User user = new User();
        user.setId(1L);
        user.setEmail("email@test.com");

        final ResendTokenRequest request = new ResendTokenRequest();
        request.setEmail("email@test.com");

        when(mockUserRepository.findByEmail("email@test.com")).thenReturn(Optional.of(user));
        when(mockUserRepository.save(user)).thenReturn(user);

        final UserResponse result = userServiceUnderTest.resendToken(request);
    }


    @Test
    void testConfirmUser() {

        User user = new User();
        user.setId(1L);
        user.setEmail("email@test.com");

        final VerifyTokenRequest request = new VerifyTokenRequest();
        request.setVerificationToken("v4457eab5");

        final Optional<VerificationToken> verificationToken = Optional.of(new VerificationToken(user));
        when(mockVerificationTokenRepository.findByToken("v4457eab5")).thenReturn(verificationToken);

        when(mockUserRepository.save(user)).thenReturn(user);

        final UserResponse result = userServiceUnderTest.confirmUser(request);

        verify(mockUserRepository).save(user);
        verify(mockVerificationTokenRepository).findByToken(isNotNull());
    }

    @Test
    void testGetUserId() {

        User user = new User();
        user.setId(1L);
        user.setEmail("email@test.com");

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));

        final UserResponse result = userServiceUnderTest.getUserId(1L);

        verify(mockUserRepository).findById(isNotNull());
        verify(mockUserRepository).findById(1L);

        MatcherAssert.assertThat(result, notNullValue());
    }

    @Test
    void testGetUserIdThenReturnResourceNotFoundException() {

        User user = new User();
        user.setId(1L);
        user.setEmail("email@test.com");

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));

        final UserResponse result = userServiceUnderTest.getUserId(2L);
    }

    @Test
    void testGetUser() {

        User user = new User();
        user.setId(1L);
        user.setEmail("email@test.com");

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));

        final User result = userServiceUnderTest.getUser(1L);

        assertThat(result).isEqualTo(user);
        MatcherAssert.assertThat(result, is(user));
    }

    @Test
    void testResetPassword() {

        User user = new User();
        user.setId(1L);
        user.setEmail("email@test.com");
        user.setPassword("pass");

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setPassword("password");
        request.setPasswordConfirm("password");
        request.setVerificationToken("12324");

        Optional<VerificationToken> verificationToken = Optional.of(new VerificationToken(user));
        when(mockVerificationTokenRepository.findByToken("12324")).thenReturn(verificationToken);

        when(mockPasswordEncoder.encode("password")).thenReturn("");
        when(mockUserRepository.save(user)).thenReturn(user);

        UserResponse result = userServiceUnderTest.resetPassword(request);

        verify(mockUserRepository).save(isNotNull());
    }

    @Test
    void testDeleteUser() {

        userServiceUnderTest.deleteUser(1L);

        verify(mockUserRepository).deleteById(1L);
    }

    @Test
    void testLogin() {
        User user = new User();
        user.setId(1L);
        user.setEmail("email@test.com");
        user.setPassword("pass");
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);

        //LoginRequest request = new LoginRequest();
        //request.setEmail("");
        //request.setPassword("");

        //String token = "dhsagdasjdjas";
        //when(mockJwtProvider.generateToken(
        //        new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()))).thenReturn(token);

        AuthResponse result = userServiceUnderTest.login(user.getEmail(), user.getPassword());
       // MatcherAssert.assertThat(result, is("dhsagdasjdjas, email=email@test.com"));
    }
    

    @Test
    void testRemoveNotActivatedUsers() {
        User user = new User();
        user.setId(1L);
        user.setEmail("email@test.com");
        user.setPassword("pass");
        user.setEnabled(false);
        user.setCreatedDate(Instant.now().minus(10, ChronoUnit.DAYS));

        //when(mockUserRepository.findAllByEnabledIsFalseAndCreatedDateBefore
        //        ((Instant.now().minus(6, ChronoUnit.DAYS))))
        //        .thenReturn(Collections.singletonList(user));

        userServiceUnderTest.removeNotActivatedUsers();

    }

}
