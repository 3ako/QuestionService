package ru.zako.questionservice.user.role;

import jakarta.persistence.*;
import lombok.Data;
import ru.zako.questionservice.user.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles") @Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
