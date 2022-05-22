package springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import springboot.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	Optional<Order> findById(Long id);

	void deleteById(Long id);

	List<Order> findByOrderStatusIn(List<String> value);

	List<Order> findByOrderStatus(String value);

}
