package bnorbert.onlineshop.steps;

import bnorbert.onlineshop.domain.Role;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.domain.VerificationToken;
import bnorbert.onlineshop.repository.RoleRepository;
import bnorbert.onlineshop.repository.UserRepository;
import bnorbert.onlineshop.repository.VerificationTokenRepository;
import bnorbert.onlineshop.transfer.user.response.UserResponse;
import org.hamcrest.MatcherAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Component
public class UserSteps {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private RoleRepository roleRepository;

    public UserResponse createUser() {

        User user = new User();
        user.setEmail("user@email.com");
        user.setPassword("$2a$13$TjooSXHAnq9GRFVG0C..LOVkc3rRRasqSN/n4TrnJGEgK.TgL4mL6");
        user.setPasswordConfirm("$2a$13$TjooSXHAnq9GRFVG0C..LOVkc3rRRasqSN/n4TrnJGEgK.TgL4mL6");
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCreatedDate(Instant.now());

        User savedUser = userRepository.save(user);
        MatcherAssert.assertThat(savedUser, notNullValue());

        return mapUserResponse(savedUser);
    }

    private UserResponse mapUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setPassword(user.getPassword());

        return userResponse;
    }


    public User createCustomer() {
        User user = UserSteps.create(UserSteps::setCustomerDetails);

        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setVerificationToken("b3679a");
        token.setCreatedDate(LocalDateTime.now());
        token.setExpirationDate(LocalDateTime.now().plusMinutes(3));

        Role role = new Role();
        role.setName("ROLE_TEST");
        user.addToUser(role);
        //user.getRoles().add(role);

        userRepository.save(user);
        tokenRepository.save(token);
        roleRepository.save(role);

        assertThat(token, notNullValue());
        assertThat(role, notNullValue());
        assertTrue(user.getRoles().size() > 0);
        assertTrue(2 > user.getRoles().size());
        assertEquals(1, user.getRoles().size());
        assertThat(user, notNullValue());
        assertThat(user.getId(), is(user.getId()));
        assertThat(user.getEmail(), is(user.getEmail()));
        assertThat(user.getPassword(), is(user.getPasswordConfirm()));
        assertThat(user.isEnabled(), is(Boolean.TRUE));
        assertThat(user.getCreatedDate(), is(user.getCreatedDate()));

        return user;
    }

    User customer = new User();

    public static User create(Consumer<UserSteps> consumer) {
        UserSteps builder = new UserSteps();
        consumer.accept( builder );
        return builder.customer;
    }

    public void setCustomerDetails() {
        customer.setEmail("customer@mail.com");
        customer.setPassword("test");
        customer.setPasswordConfirm("test");
        customer.setAccountNonExpired(true);
        customer.setAccountNonLocked(true);
        customer.setCredentialsNonExpired(true);
        customer.setEnabled(true);
        customer.setCreatedDate(Instant.now());

        MatcherAssert.assertThat(customer, notNullValue());
    }


    public UserResponse unconfirmedUser() {

        User user = new User();
        user.setEmail("user_1@email.com");
        user.setPassword("$2a$13$TjooSXHAnq9GRFVG0C..LOVkc3rRRasqSN/n4TrnJGEgK.TgL4mL6");
        user.setPasswordConfirm("$2a$13$TjooSXHAnq9GRFVG0C..LOVkc3rRRasqSN/n4TrnJGEgK.TgL4mL6");
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(false);
        user.setCreatedDate((Instant.now().minus(7, ChronoUnit.DAYS)));

        User savedUser = userRepository.save(user);
        MatcherAssert.assertThat(savedUser, notNullValue());

        return mapUserResponse(savedUser);
    }

}
