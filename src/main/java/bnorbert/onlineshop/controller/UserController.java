package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.service.UserService;
import bnorbert.onlineshop.transfer.user.login.AuthResponse;
import bnorbert.onlineshop.transfer.user.request.ResendTokenRequest;
import bnorbert.onlineshop.transfer.user.request.ResetPasswordRequest;
import bnorbert.onlineshop.transfer.user.request.SaveUserRequest;
import bnorbert.onlineshop.transfer.user.request.VerifyTokenRequest;
import bnorbert.onlineshop.transfer.user.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@CrossOrigin
@RestController
@RequestMapping("/users")

public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody @Valid SaveUserRequest request) {
        UserResponse user = userService.createUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserId(@PathVariable("userId") long userId) {
        UserResponse user = userService.getUserId(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/settings/confirmation")
    public ResponseEntity<UserResponse> confirmUser(
            @RequestBody @Valid VerifyTokenRequest request) {
        UserResponse user = userService.confirmUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/settings/tokens")
    public ResponseEntity<UserResponse> resendToken(
            @RequestBody @Valid ResendTokenRequest request) {
        UserResponse user = userService.resendToken(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/settings/passwords")
    public ResponseEntity<UserResponse> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request) {
        UserResponse user = userService.resetPassword(request);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/settings/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestParam(value = "email")@NotNull String email,
                                              @RequestParam(value = "password")@NotNull String password) {
        AuthResponse authResponse = userService.login(email, password);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

}
