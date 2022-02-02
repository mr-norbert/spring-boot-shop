package bnorbert.onlineshop.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MyUserDetails extends User implements UserDetails {

    public MyUserDetails(User user) {
        super(user);
        initAuthorities(user);
    }

    private void initAuthorities(User user) {
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            role.getPermissions().forEach(permission -> authorities.add
                    (new SimpleGrantedAuthority(permission.getName())));
        }
    }

    private final Set<GrantedAuthority> authorities = new HashSet<>();

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return null;
    }
}
