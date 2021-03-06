package springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import springboot.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findById(Long id);

	Optional<Product> findByProductCode(String code);

	void deleteById(Long id);

	List<Product> findByTypeAndIsDeleted(String type, boolean isDeleted);

	List<Product> findByIsDeleted(boolean isDeleted);
}
