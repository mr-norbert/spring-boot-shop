package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.MyUserDetails;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    private final HttpServletRequest request;

    @Autowired
    public MyUserDetailsService(UserRepository userRepository, LoginAttemptService loginAttemptService, HttpServletRequest request) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
        this.request = request;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("load {}", email);
        String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip)) {
            log.info("blocked: {}", ip);
            throw new SecurityException();
        }
        try {
            return getUserDetails(email);
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException(email + " not found!");
        }
    }

    private UserDetails getUserDetails(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isPresent()) {
            UserDetails userDetails = new MyUserDetails(user.get());
            new AccountStatusUserDetailsChecker().check(userDetails);
            return userDetails;
        } else {
            throw new ResourceNotFoundException("Invalid email");
        }
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }



}
