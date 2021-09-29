package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/loginAttempt")

@AllArgsConstructor
public class LoginAttemptController {

    private final LoginAttemptService loginAttemptService;

    @GetMapping("/mockLoginAttempts")
    public ResponseEntity<Void> testLoginAttempts() {
        loginAttemptService.testLoginAttempts();
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
