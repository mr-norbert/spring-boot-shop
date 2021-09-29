package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private LoginAttemptService mockLoginAttemptService;

    private MyUserDetailsService myUserDetailsServiceUnderTest;

    @BeforeEach
    void setUp() {
        myUserDetailsServiceUnderTest = new MyUserDetailsService(mockUserRepository, mockLoginAttemptService, new MockHttpServletRequest());
    }


    @Test
    void testLoadUserByUsername() {

        User user = new User();
        user.setId(1L);
        user.setEmail("email@test.com");
        user.setPassword("pass");
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);

        when(mockLoginAttemptService.isBlocked("127.0.0.1")).thenReturn(false);

        when(mockUserRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        final UserDetails result = myUserDetailsServiceUnderTest.loadUserByUsername("email@test.com");
    }


    @Test
    void testLoadUserByUsername_thenThrowException() {

        User user = new User();
        user.setId(10L);
        user.setEmail("email@test.com");
        user.setPassword("pass");
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);

        when(mockLoginAttemptService.isBlocked("127.0.0.1")).thenReturn(true);

        when(mockUserRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        final UserDetails result = myUserDetailsServiceUnderTest.loadUserByUsername("email@test.com");

    }



}
