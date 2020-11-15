package com.javatechie.spring.security.controller;

import com.javatechie.spring.security.entity.User;
import com.javatechie.spring.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
        user.setRoles("ROLE_USER");
        return repository.save(user);
    }

    @GetMapping("/changeAccess/{userId}")
   /* @Secured("ROLE_ADMIN")*/
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String provideAdminAccess(@PathVariable int userId, Principal principal) {
        User user = repository.findById(userId).get();
        String assignRole = user.getRoles() + "," + "ROLE_ADMIN";
        user.setRoles(assignRole);
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
}
