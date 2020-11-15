package com.javatechie.spring.security.controller;

import com.javatechie.spring.security.entity.Post;
import com.javatechie.spring.security.entity.PostStatus;
import com.javatechie.spring.security.repository.PostRepository;
import com.javatechie.spring.security.repository.UserRepository;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository repository;

    @PostMapping("/new")
    public String addNewPost(@RequestBody Post post, Principal principal) {
        post.setStatus(PostStatus.PENDING);
        postRepository.save(post);
        return principal.getName() + " Your post published successfully , Required ADMIN/MODERATOR Action !";
    }


    @GetMapping("/approve/{postId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String approvePost(@PathVariable int postId) {
        Post post = postRepository.findById(postId).get();
        post.setStatus(PostStatus.APPROVED);
        postRepository.save(post);
        return "post approved !";
    }

    @GetMapping("/approveAll")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String approveAllPost() {
        postRepository.findAll().stream().filter(post -> post.getStatus().equals(PostStatus.PENDING)).forEach(post -> {
            post.setStatus(PostStatus.APPROVED);
            postRepository.save(post);
        });
        return "All pending post has been approved !";
    }

    @GetMapping("/remove/{postId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String removePost(@PathVariable int postId) {
        Post post = postRepository.findById(postId).get();
        post.setStatus(PostStatus.REJECT);
        postRepository.save(post);
        return "post removed !";
    }

    @GetMapping("/removeAll")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String removeAllPost() {
        postRepository.findAll().stream().filter(post -> post.getStatus().equals(PostStatus.PENDING)).forEach(post -> {
            post.setStatus(PostStatus.REJECT);
            postRepository.save(post);
        });
        return "All pending post has been removed !";
    }

    @GetMapping("/all")
    public List<Post> viewPosts() {
        return postRepository.findAll().stream()
                .filter(post -> post.getStatus().equals(PostStatus.APPROVED))
                .collect(Collectors.toList());
    }

    @GetMapping("/countPendingPost")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public long notifyPendingPostCount(Principal principal) {
        long pendingPostCount = 0;
        String loggedInUserRoles = repository.findByUserName(principal.getName()).get().getRoles();
        for (String role : loggedInUserRoles.split(",")) {
            if (role.equals("ROLE_ADMIN") || role.equals("ROLE_MODERATOR")) {
                pendingPostCount = postRepository.findAll().stream()
                        .filter(post -> post.getStatus().equals(PostStatus.PENDING))
                        .count();
            }
        }
        return pendingPostCount;
    }

}
