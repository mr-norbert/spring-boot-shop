package bnorbert.onlineshop.transfer.user.request;

import bnorbert.onlineshop.transfer.user.validator.PasswordMatches;

import javax.persistence.Transient;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@PasswordMatches
public class SaveUserRequest {

    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-~`!@#$%^&*_+=)(}{|;':<>,.?/])(?=\\S+$).{8,}$", message =
            "Password must contain at least 1 uppercase character. Password must contain at least 1 digit character. " +
                    " Password must contain at least 1 lowercase character. " +
                    " Use 8 characters or more for your password. " +
                    " Password must contain at least 1 special character(-~`!@#$%^&*_+=)(}{|;':<>,.?/). " +
                    " No whitespace allowed.")
    @Size(max = 100)
    private String password;

    //1. ^ = start-of-string
    //2. (?=.*[0-9]) = a digit must occur at least once
    //3. (?=.*[a-z]) = a lower case letter must occur at least once
    //4. (?=.*[A-Z]) = an upper case letter must occur at least once
    //5. (?=.*[-~`!@#$%^&*_+=)(}{|;':<>,.?/]) = a special character must occur at least once
    //6. (?=\\S+$) = no whitespace allowed in the entire string
    //7. .{8,} = anything, at least eight places though
    //8. $  = end-of-string
    //9. Special characters = brackets and quotation marks do not work

    @Transient
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-~`!@#$%^&*_+=)(}{|;':<>,.?/])(?=\\S+$).{8,}$", message =
            "Password must contain at least 1 uppercase character. Password must contain at least 1 digit character. " +
                    " Password must contain at least 1 lowercase character. " +
                    " Use 8 characters or more for your password. " +
                    " Password must contain at least 1 special character(-~`!@#$%^&*_+=)(}{|;':<>,.?/). " +
                    " No whitespace allowed.")
    @Size(max = 100)
    private String passwordConfirm;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    @Override
    public String toString() {
        return "SaveUserRequest{" +
                "email='" + email + '\'' +
                '}';
    }

}
