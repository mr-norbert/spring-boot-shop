package bnorbert.onlineshop.transfer.user.request;

import javax.validation.constraints.Pattern;

public class ResendTokenRequest {

    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ResendTokenRequest{" +
                ", email='" + email + '\'' +
                '}';
    }
}
