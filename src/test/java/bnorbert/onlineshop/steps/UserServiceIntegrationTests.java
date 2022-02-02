package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.domain.VerificationToken;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.repository.VerificationTokenRepository;
import bnorbert.onlineshop.service.UserService;
import bnorbert.onlineshop.transfer.user.login.AuthResponse;
import bnorbert.onlineshop.transfer.user.request.ResendTokenRequest;
import bnorbert.onlineshop.transfer.user.request.ResetPasswordRequest;
import bnorbert.onlineshop.transfer.user.response.UserResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceIntegrationTests {

    @Autowired
    private UserSteps userSteps;
    @Autowired
    private UserService userService;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testCreateUser_whenValidRequest_thenReturnCreatedUser() {
        userSteps.createUser();
    }

    @Test
    public void testCreateCustomer_whenValidRequest_thenReturnCreatedCustomer() {

        User user = userSteps.createCustomer();

        assertThat(user, notNullValue());
        assertThat(user.getId(), is(user.getId()));
    }

    @Test
    public void testGetUserId_whenExistingEntity_thenVerifyToken() {

        UserResponse user = userService.getUserId(1L);
        assertThat(user, notNullValue());
        assertThat(user.getId(), is(user.getId()));

        String token = "4457eab5";

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token).orElse(null);

        assertThat(verificationToken, notNullValue());
        assertEquals(verificationToken.getToken(), token);
    }

    @Test
    public void testGetUser_whenExistingEntity_thenReturnUser() {

        UserResponse createdUser = userSteps.createUser();
        User user = userService.getUser(createdUser.getId());

        assertThat(user, notNullValue());
        assertThat(user.getId(), is(user.getId()));
        assertThat(user.getEmail(), is(user.getEmail()));
        assertThat(user.getPassword(), is(user.getPasswordConfirm()));
        assertThat(user.isEnabled(), is(Boolean.TRUE));
        assertThat(user.getCreatedDate(), is(user.getCreatedDate()));
    }

    @Test
    public void testGetUser_whenExistingEntity_thenReturnUserId() {

        UserResponse user = userService.getUserId(1L);

        assertEquals(1, user.getId());
        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), is(user.getEmail()));
    }

    @Test
    public void testLogin_thenReturnToken_AbsentIdentifier() {

        UserResponse user = userService.getUserId(1L);

        try {
            AuthResponse authResponse = userService.login(user.getEmail(), user.getPassword());
            String token = authResponse.getAuthenticationToken();
            assertEquals(user.getEmail(), authResponse.getEmail());
            assertThat(authResponse.getEmail(), notNullValue());
            assertThat(authResponse.getAuthenticationToken(), notNullValue());
            assertThat(authResponse.getAuthenticationToken(), is(token));
        }catch (RuntimeException e){
            e.toString();
        }
        assertEquals(1, user.getId());
        assertThat(user.getEmail(), notNullValue());
        assertThat(user.getEmail(), is(user.getEmail()));
        assertThat(user.getPassword(), is(user.getPassword()));

    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetUser_thenDeleteUser() {

        UserResponse user = userService.getUserId(9L);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), is(user.getEmail()));

        userService.deleteUser(user.getId());

        UserResponse test = userService.getUserId(9L);

    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetUser_whenNonExistingEntity_thenThrowNotFoundException() {
        userService.getUser(999999L);
    }

    @Test
    public void testGetUser_thenDeleteNotActivatedUsers() {

        UserResponse _user = userSteps.unconfirmedUser();
        User user = userService.getUser(_user.getId());

        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), is(user.getEmail()));

        userService.removeNotActivatedUsers();

    }


    @Test
    public void testGetUserId_whenExistingEntity_thenResetPassword() {

        UserResponse user = userService.getUserId(7L);
        assertThat(user, notNullValue());
        assertThat(user.getId(), is(user.getId()));

        String token = "2370ae1";

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token).orElse(null);

        assertThat(verificationToken, notNullValue());
        assertEquals(verificationToken.getToken(), token);

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setVerificationToken(token);
        request.setPassword(passwordEncoder.encode("test1234&^$@#!@d.A"));
        request.setPasswordConfirm(passwordEncoder.encode("test1234&^$@#!@d.A"));

        UserResponse userResponse = userService.resetPassword(request);
        assertThat(userResponse.getPassword(), is(userResponse.getPassword()));

    }


    @Test
    public void testGetUserId_whenExistingEntity_resendToken() {

        User user = userService.getUser(7L);
        assertThat(user, notNullValue());
        assertThat(user.getId(), is(user.getId()));

        VerificationToken verificationToken = new VerificationToken(user);
        verificationToken.setUser(user);

        ResendTokenRequest request = new ResendTokenRequest();
        request.setEmail(user.getEmail());

        UserResponse userResponse = userService.resendToken(request);

        assertThat(verificationToken.getToken(), notNullValue());
        assertThat(userResponse.getId(), notNullValue());
        assertThat(user.getId(), notNullValue());

    }
}
