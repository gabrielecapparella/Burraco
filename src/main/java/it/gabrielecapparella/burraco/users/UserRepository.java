package it.gabrielecapparella.burraco.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	List<User> findAll();

	User getById(Long id);

	User findByUsername(String username);

	User findByEmail(String email);
}
