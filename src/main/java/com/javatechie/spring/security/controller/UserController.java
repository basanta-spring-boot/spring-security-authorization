package com.javatechie.spring.security.controller;

import com.javatechie.spring.security.common.UserConstant;
import com.javatechie.spring.security.entity.User;
import com.javatechie.spring.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository repository;

    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(UserConstant.DEFAULT_ROLE);
        return repository.save(user);
    }

    @GetMapping("/giveAccess/{userId}/{roleType}")
    @Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String provideAdminAccess(@PathVariable int userId, @PathVariable String roleType, Principal principal) {
        String assignRole = "";
        User user = repository.findById(userId).get();
        List<String> activeRoles = getRolesByLoggedInUser(principal);
        if (activeRoles.contains(roleType)) {
            assignRole = user.getRoles() + "," + roleType;
            user.setRoles(assignRole);
        }
        repository.save(user);
        return "Hi " + user.getUserName() + " ADMIN access provided to you by " + principal.getName();
    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<User> loadUsers() {
        return repository.findAll();
    }

    @GetMapping("/test")
    @Secured("ROLE_USER")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String testUserAccess() {
        return "user can only access this !";
    }

    private List<String> getRolesByLoggedInUser(Principal principal) {
        String loggedInUserRoles = getLoggedInUser(principal).getRoles();
        List<String> roles = Arrays.stream(loggedInUserRoles.split(","))
                .collect(Collectors.toList());
        if (roles.contains("ROLE_ADMIN")) {
            return Arrays.stream(UserConstant.ADMIN_ACCESS).collect(Collectors.toList());
        }
        if (roles.contains("ROLE_MODERATOR")) {
            return Arrays.stream(UserConstant.MODERATOR_ACCESS).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private User getLoggedInUser(Principal principal) {
        return repository.findByUserName(principal.getName()).get();
    }

}
