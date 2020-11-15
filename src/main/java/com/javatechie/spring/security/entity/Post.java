package com.javatechie.spring.security.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "POST")
public class Post {
    @Id
    @GeneratedValue
    private int id;
    private String subject;
    private String description;
    @Enumerated(EnumType.STRING)
    private PostStatus status;
}
