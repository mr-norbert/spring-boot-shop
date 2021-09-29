package bnorbert.onlineshop.transfer.role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToUserRequest {

    private long userId;
    private long roleId;
}
