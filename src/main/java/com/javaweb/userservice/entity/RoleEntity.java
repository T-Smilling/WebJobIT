package com.javaweb.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RoleEntity {
    @Id
    private String name;

    private String description;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name="role_permission",
            joinColumns = @JoinColumn(name="role_name"),
            inverseJoinColumns = @JoinColumn(name="permission_name"))
    private Set<PermissionEntity> permissions;
}
