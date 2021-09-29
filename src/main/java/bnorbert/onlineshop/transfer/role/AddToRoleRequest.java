package bnorbert.onlineshop.transfer.role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToRoleRequest {
    private long permissionId;
    private long roleId;
}
