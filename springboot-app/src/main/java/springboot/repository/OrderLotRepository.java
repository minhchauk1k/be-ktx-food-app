package springboot.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import springboot.model.Order;
import springboot.model.OrderLot;

@Repository
public interface OrderLotRepository extends JpaRepository<OrderLot, Long> {
	Optional<OrderLot> findById(Long id);

	void deleteById(Long id);

	@Query("select o from OrderLot o where o.isCompleted = :isCompleted AND o.createDate >= :today")
	List<OrderLot> findByIsCompleted(@Param("isCompleted") boolean isCompleted, @Param("today") Date today);

	List<OrderLot> findByLotStatus(String status);

	List<OrderLot> findByDetailsIn(List<Order> orderList);
}
