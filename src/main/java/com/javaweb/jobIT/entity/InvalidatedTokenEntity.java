package com.javaweb.jobIT.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "token")
public class InvalidatedTokenEntity {
    @Id
    private String id;

    private Date expiryTime;
}
