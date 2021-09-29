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

    @GetMapping("/getUserId/{userId}")
    public ResponseEntity<UserResponse> getUserId(@PathVariable("userId") long userId) {
        UserResponse user = userService.getUserId(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/confirmUser")
    public ResponseEntity<UserResponse> confirmUser(
            @RequestBody @Valid VerifyTokenRequest request) {
        UserResponse user = userService.confirmUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/resendToken")
    public ResponseEntity<UserResponse> resendToken(
            @RequestBody @Valid ResendTokenRequest request) {
        UserResponse user = userService.resendToken(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") long id) {
        User user = userService.getUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<UserResponse> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request) {
        UserResponse user = userService.resetPassword(request);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //@PostMapping("/login")
    //public  ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
    //    AuthResponse user = userService.login(loginRequest);
    //    return new ResponseEntity<>(user, HttpStatus.OK);
    //}

    @GetMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestParam(value = "email")@NotNull String email,
                                               @RequestParam(value = "password")@NotNull String password) {
        AuthResponse authResponse = userService.login(email, password);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

}
