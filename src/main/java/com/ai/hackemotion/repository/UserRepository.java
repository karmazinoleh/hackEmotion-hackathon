package com.ai.hackemotion.repository;


import com.ai.hackemotion.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

   Optional<User> findByUsername(String username);

   Optional<User> findByUsernameOrEmail(String username, String email);

   Optional<User> findByEmail(String email);
}
