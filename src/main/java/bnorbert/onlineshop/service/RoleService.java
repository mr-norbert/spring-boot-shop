package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Permission;
import bnorbert.onlineshop.domain.Role;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.RoleMapper;
import bnorbert.onlineshop.repository.PermissionRepository;
import bnorbert.onlineshop.repository.RoleRepository;
import bnorbert.onlineshop.repository.UserRepository;
import bnorbert.onlineshop.transfer.role.AddToRoleRequest;
import bnorbert.onlineshop.transfer.role.AddToUserRequest;
import bnorbert.onlineshop.transfer.role.CreatePermissionRequest;
import bnorbert.onlineshop.transfer.role.CreateRoleRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;

    public void createRole(CreateRoleRequest request) {
        log.info("Creating role: {}", request);
        Role role = roleMapper.map(request);
        roleRepository.save(role);
    }

    public void createPermission(CreatePermissionRequest request) {
        log.info("Creating permission: {}", request);
        Permission permission = roleMapper.mapper(request);
        permissionRepository.save(permission);
    }

    public void addRoleToUser(AddToUserRequest request){
        log.info("Adding role to user: {}", request);
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Role" + request.getRoleId() + "not found"));
        User user = userService.getUser(request.getUserId());
        user.addToUser(role);

        userRepository.save(user);
    }

    public void addPermissionToRole(AddToRoleRequest request){
        log.info("Adding permission to role: {}", request);
        Permission permission = permissionRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Permission" + request.getPermissionId() + "not found"));
        Role role = getRole(request.getRoleId());
        role.addToRole(permission);

        roleRepository.save(role);
    }


    public Role getRole(long id){
        log.info("Retrieving role {}", id);
        return roleRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException
                        ("Role" + id + "not found"));
    }

}
