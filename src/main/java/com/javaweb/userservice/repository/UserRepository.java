package com.javaweb.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.javaweb.userservice.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {

     Optional<UserEntity> findByUsername(String name);
}