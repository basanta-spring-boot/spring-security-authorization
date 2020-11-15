package com.javatechie.spring.security.repository;

import com.javatechie.spring.security.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Integer> {
}
