package bnorbert.onlineshop.transfer.role;

import bnorbert.onlineshop.domain.Permission;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class RoleResponse {

    private String name;
    private Long id;
    Set<Permission> permissions = new HashSet<>();

}
