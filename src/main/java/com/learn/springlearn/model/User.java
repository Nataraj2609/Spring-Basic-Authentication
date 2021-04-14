package com.learn.springlearn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isAccountNonExpired;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isAccountNonLocked;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isCredentialsNonExpired;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isEnabled;

    @Column(nullable = false)
    private String authorities;
}
