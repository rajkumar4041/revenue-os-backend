package com.revenueos.user;

import com.revenueos.common.web.PageResponse;
import com.revenueos.user.dto.AssignRolesRequest;
import com.revenueos.user.dto.CreateUserRequest;
import com.revenueos.user.dto.UpdateUserRequest;
import com.revenueos.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasAuthority('USER_MANAGE')")
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(operationId = "listUsers", summary = "List users in the current organization")
    public PageResponse<UserResponse> list(@ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return userService.list(pageable);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getUser", summary = "Get a user by id")
    public UserResponse get(@PathVariable UUID id) {
        return userService.get(id);
    }

    @PostMapping
    @Operation(operationId = "createUser", summary = "Create a user")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateUser", summary = "Update a user")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @PostMapping("/{id}/roles")
    @Operation(operationId = "assignUserRoles", summary = "Assign roles to a user")
    public UserResponse assignRoles(@PathVariable UUID id, @Valid @RequestBody AssignRolesRequest request) {
        return userService.assignRoles(id, request.roleIds());
    }
}
