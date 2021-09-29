package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Permission;
import bnorbert.onlineshop.domain.Role;
import bnorbert.onlineshop.transfer.role.CreatePermissionRequest;
import bnorbert.onlineshop.transfer.role.CreateRoleRequest;
import bnorbert.onlineshop.transfer.role.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "request.name")
    public abstract Role map(CreateRoleRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "permissionRequest.name")
    public abstract Permission mapper(CreatePermissionRequest permissionRequest);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "role.name")
    public abstract RoleResponse mapToRoleResponse(Role role);
}
