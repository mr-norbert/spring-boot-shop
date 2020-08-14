package bnorbert.onlineshop.transfer.user.response;

import java.util.HashSet;
import java.util.Set;

public class UserResponse {
    private long id;
    private String email;
    private String password;
    private Set<RoleResponse> roles = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public Set<RoleResponse> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleResponse> roles) {
        this.roles = roles;
    }
}
