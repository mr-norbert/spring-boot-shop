package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.RoleService;
import bnorbert.onlineshop.transfer.role.AddToRoleRequest;
import bnorbert.onlineshop.transfer.role.AddToUserRequest;
import bnorbert.onlineshop.transfer.role.CreatePermissionRequest;
import bnorbert.onlineshop.transfer.role.CreateRoleRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/roles")
@CrossOrigin

@AllArgsConstructor
public class RoleController {

    private final RoleService service;

    @PostMapping
    public ResponseEntity<Void> createRole(@RequestBody CreateRoleRequest request) {
        service.createRole(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/createPermission")
    public ResponseEntity<Void> createPermission(@RequestBody CreatePermissionRequest request) {
        service.createPermission(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/addRole")
    public ResponseEntity<Void> addRoleToUser(
            @RequestBody @Valid AddToUserRequest request) {
        service.addRoleToUser(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/addPermissionToRole")
    public ResponseEntity<Void> addPermissionToRole(
            @RequestBody @Valid AddToRoleRequest request) {
        service.addPermissionToRole(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
