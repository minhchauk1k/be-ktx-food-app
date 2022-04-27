package springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import springboot.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findById(Long id);
	
	Role findByName(String name);
	
	void deleteById(Long id);
}
