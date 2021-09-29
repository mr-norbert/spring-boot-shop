package bnorbert.onlineshop.transfer.role;

public class CreateRoleRequest {
    private String name;

    public String getName() {
        return name.toUpperCase();
    }

    public void setName(String name) {
        this.name = "ROLE_" + name.toUpperCase();
    }
}
