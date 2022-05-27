package springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import springboot.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findById(Long i);
	
	Optional<User> findByUserName(String userName);

	void deleteById(Long id);
}
