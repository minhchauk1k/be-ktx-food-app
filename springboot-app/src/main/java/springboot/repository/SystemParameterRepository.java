package springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import springboot.model.SystemParameter;

@Repository
public interface SystemParameterRepository extends JpaRepository<SystemParameter, Integer> {
	Optional<SystemParameter> findByParameterKey(String key);
}
