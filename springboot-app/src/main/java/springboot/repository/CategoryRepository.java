package springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import springboot.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category> findById(Long id);

	Optional<Category> findByCategoryKey(String key);

	Optional<Category> findByCategoryValue(String value);
	
	List<Category> findByType(String type);

	void deleteById(Long id);

	boolean existsByCategoryKeyAndCategoryValueAndType(String key, String value, String type);

}
