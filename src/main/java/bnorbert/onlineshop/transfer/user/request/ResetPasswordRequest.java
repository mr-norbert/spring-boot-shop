package bnorbert.onlineshop.transfer.user.request;

import bnorbert.onlineshop.transfer.user.validator.PasswordMatchesForReset;

import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@PasswordMatchesForReset
public class ResetPasswordRequest {

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-~`!@#$%^&*_+=)(}{|;':<>,.?/])(?=\\S+$).{8,}$", message =
            "Password must contain at least 1 uppercase character. Password must contain at least 1 digit character. " +
                    " Password must contain at least 1 lowercase character. " +
                    " Use 8 characters or more for your password. " +
                    " Password must contain at least 1 special character(-~`!@#$%^&*_+=)(}{|;':<>,.?/). " +
                    " No whitespace allowed.")
    @Size(max = 100)
    private String password;

    @Transient
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-~`!@#$%^&*_+=)(}{|;':<>,.?/])(?=\\S+$).{8,}$", message =
            "Password must contain at least 1 uppercase character. Password must contain at least 1 digit character. " +
                    " Password must contain at least 1 lowercase character. " +
                    " Use 8 characters or more for your password. " +
                    " Password must contain at least 1 special character(-~`!@#$%^&*_+=)(}{|;':<>,.?/). " +
                    " No whitespace allowed.")
    @Size(max = 100)
    private String passwordConfirm;

    @NotNull
    @NotEmpty
    private String verificationToken;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    @Override
    public String toString() {
        return "ResetPasswordRequest{" +
                "verificationToken='" + verificationToken + '\'' +
                '}';
    }
}