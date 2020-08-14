package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.MyUserDetails;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

            Optional<User> userOptional = userRepository.findByEmail(email);
            userOptional.orElseThrow(() -> new ResourceNotFoundException("Invalid email"));

            UserDetails userDetails = new MyUserDetails(userOptional.get());
            new AccountStatusUserDetailsChecker().check(userDetails);
            return userDetails;
        }



}
