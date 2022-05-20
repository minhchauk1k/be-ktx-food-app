package springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import springboot.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category> findById(Long id);

	Optional<Category> findByCategoryKey(String key);
	
	Optional<Category> findByCategoryValue(String value);

	void deleteById(Long id);

	boolean existsByCategoryValueAndType(String value, String type);
	
}
