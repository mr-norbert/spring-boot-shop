package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.service.UserService;
import bnorbert.onlineshop.transfer.user.login.AuthResponse;
import bnorbert.onlineshop.transfer.user.login.LoginRequest;
import bnorbert.onlineshop.transfer.user.request.ResendTokenRequest;
import bnorbert.onlineshop.transfer.user.request.ResetPasswordRequest;
import bnorbert.onlineshop.transfer.user.request.SaveUserRequest;
import bnorbert.onlineshop.transfer.user.request.VerifyTokenRequest;
import bnorbert.onlineshop.transfer.user.response.RoleResponse;
import bnorbert.onlineshop.transfer.user.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class UserControllerTest {

    @Mock
    private UserService mockUserService;

    private UserController userControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        userControllerUnderTest = new UserController(mockUserService);
    }

    @Test
    void testCreateUser() {

        final SaveUserRequest request = new SaveUserRequest();
        request.setEmail("email");
        request.setPassword("password");
        request.setPasswordConfirm("password");

        final UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("email");
        userResponse.setPassword("password");
        final RoleResponse roleResponse = new RoleResponse();
        roleResponse.setId(1L);
        roleResponse.setName("name");
        userResponse.setRoles(new HashSet<>(Collections.singletonList(roleResponse)));
        when(mockUserService.createUser(any(SaveUserRequest.class))).thenReturn(userResponse);

        final ResponseEntity<UserResponse> result = userControllerUnderTest.createUser(request);

    }

    @Test
    void testGetUserId() {

        final UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("email");
        userResponse.setPassword("password");
        final RoleResponse roleResponse = new RoleResponse();
        roleResponse.setId(1L);
        roleResponse.setName("name");
        userResponse.setRoles(new HashSet<>(Collections.singletonList(roleResponse)));
        when(mockUserService.getUserId(1L)).thenReturn(userResponse);

        final ResponseEntity<UserResponse> result = userControllerUnderTest.getUserId(1L);
    }

    @Test
    void testConfirmUser() {

        final VerifyTokenRequest request = new VerifyTokenRequest();
        request.setVerificationToken("verificationToken");


        final UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("email");
        userResponse.setPassword("password");
        final RoleResponse roleResponse = new RoleResponse();
        roleResponse.setId(1L);
        roleResponse.setName("name");
        userResponse.setRoles(new HashSet<>(Collections.singletonList(roleResponse)));
        when(mockUserService.confirmUser(any(VerifyTokenRequest.class))).thenReturn(userResponse);


        final ResponseEntity<UserResponse> result = userControllerUnderTest.confirmUser(request);

    }

    @Test
    void testResendToken() {

        final ResendTokenRequest request = new ResendTokenRequest();
        request.setEmail("email");

        final UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("email");
        userResponse.setPassword("password");
        final RoleResponse roleResponse = new RoleResponse();
        roleResponse.setId(1L);
        roleResponse.setName("name");
        userResponse.setRoles(new HashSet<>(Collections.singletonList(roleResponse)));
        when(mockUserService.resendToken(any(ResendTokenRequest.class))).thenReturn(userResponse);

        final ResponseEntity<UserResponse> result = userControllerUnderTest.resendToken(request);
    }

    @Test
    void testGetUser() {

        final ResponseEntity<User> expectedResult = new ResponseEntity<>(new User(), HttpStatus.CONTINUE);
        when(mockUserService.getUser(1L)).thenReturn(new User());

        final ResponseEntity<User> result = userControllerUnderTest.getUser(1L);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testResetPassword() {

        final ResetPasswordRequest request = new ResetPasswordRequest();
        request.setPassword("password");
        request.setPasswordConfirm("password");
        request.setVerificationToken("verificationToken");

        final UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("email");
        userResponse.setPassword("password");
        final RoleResponse roleResponse = new RoleResponse();
        roleResponse.setId(1L);
        roleResponse.setName("name");
        userResponse.setRoles(new HashSet<>(Collections.singletonList(roleResponse)));
        when(mockUserService.resetPassword(any(ResetPasswordRequest.class))).thenReturn(userResponse);

        final ResponseEntity<UserResponse> result = userControllerUnderTest.resetPassword(request);
    }

    @Test
    void testDeleteUser() {

        final ResponseEntity result = userControllerUnderTest.deleteUser(1L);

        verify(mockUserService).deleteUser(1L);
    }

    @Test
    void testLogin() {

        final LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("email");
        loginRequest.setPassword("password");


        final AuthResponse authResponse = new AuthResponse("eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzdHJpbmdAZ21haWwuY29tIiwiZXhwIjoxNTk3MjQxMDM5fQ.TIRna-xNOykPK3oA66_oGBnzxAUT4P-vYmWVYy0N7FqrbDFvKVM4CDwI5mH70uI3N7GUK8F7gsc5j3nvddquqPiaUlkhHFC4LGHZK4J9HKlBw6y9MUGWI2R70ETM7B3b1UJ_8XR3smVndb1HYbv19v8VtswoSvNkNndAELnqRSiqyLYtGU-CDVxvvbE81g5wkGCPPfECKSeK9Ky7gT2B-oKyPMj9XbalT-JGc1vVZqYBa2Pce10kBCSCqrL7I9zZS29HiuqKcdBCrKWnk4neo-yElvjyQoDE8bbzIgKlNi7uWObPSeyffKhzo5EXmdTaPZPfLWBAaxqUAwYbPx2j6w", "email");
        when(mockUserService.login(any(LoginRequest.class))).thenReturn(authResponse);


        final ResponseEntity<AuthResponse> result = userControllerUnderTest.login(loginRequest);
    }
}
