package com.lwms.backend.dao;

import com.lwms.backend.entities.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	/**
	 * Find user by unique username.
	 * Input: username string.
	 * Output: Optional user; read-only.
	 */
	Optional<User> findByUsername(String username);

	/**
	 * Find user by unique email.
	 * Input: email string.
	 * Output: Optional user; read-only.
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Fetch user with role eagerly by username.
	 * Input: username string.
	 * Output: Optional user (role populated); read-only.
	 */
	@EntityGraph(attributePaths = {"role"})
	Optional<User> findWithRoleByUsername(String username);

	/**
	 * Fetch user with role eagerly by email.
	 * Input: email string.
	 * Output: Optional user (role populated); read-only.
	 */
	@EntityGraph(attributePaths = {"role"})
	Optional<User> findWithRoleByEmail(String email);

	/**
	 * Fetch all users with role eagerly.
	 * Input: none.
	 * Output: List of users (roles populated); read-only.
	 */
	@EntityGraph(attributePaths = {"role"})
	List<User> findAllBy();

	/**
	 * Fetch user with role eagerly by user id.
	 * Input: userId integer.
	 * Output: Optional user (role populated); read-only.
	 */
	@EntityGraph(attributePaths = {"role"})
	Optional<User> findWithRoleByUserId(Integer userId);
}