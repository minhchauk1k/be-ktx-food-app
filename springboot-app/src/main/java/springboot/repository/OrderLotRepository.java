package springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import springboot.model.Order;
import springboot.model.OrderLot;

@Repository
public interface OrderLotRepository extends JpaRepository<OrderLot, Long> {
	Optional<OrderLot> findById(Long id);

	void deleteById(Long id);

	List<OrderLot> findByIsCompleted(boolean isCompleted);

	List<OrderLot> findByDetailsIn(List<Order> orderList);
}
