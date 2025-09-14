package com.lwms.backend.dao;

import com.lwms.backend.entities.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);

	@EntityGraph(attributePaths = {"role"})
	Optional<User> findWithRoleByUsername(String username);

	@EntityGraph(attributePaths = {"role"})
	Optional<User> findWithRoleByEmail(String email);

	@EntityGraph(attributePaths = {"role"})
	List<User> findAllBy();

	@EntityGraph(attributePaths = {"role"})
	Optional<User> findWithRoleByUserId(Integer userId);
}