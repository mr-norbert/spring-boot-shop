package bnorbert.onlineshop.transfer.user.login;

public class AuthResponse {
    private String authenticationToken;
    private String email;

    public AuthResponse(String authenticationToken, String email) {
        this.authenticationToken = authenticationToken;
        this.email = email;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
