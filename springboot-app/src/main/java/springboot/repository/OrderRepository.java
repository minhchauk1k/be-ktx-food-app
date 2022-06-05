package springboot.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import springboot.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	Optional<Order> findById(Long id);

	void deleteById(Long id);

	List<Order> findByOrderStatusIn(List<String> value);

	List<Order> findByOrderStatus(String value);

	List<Order> findByOrderStatusInAndIsCompleted(List<String> value, boolean isCompleted);

	List<Order> findByOrderStatusAndIsCompleted(String value, boolean isCompleted);

	List<Order> findByCreateDateGreaterThanEqual(Date today);

	@Query("select o from Order o where o.createUser = :user AND o.createDate > :dateFrom AND o.createDate < :dateTo")
	List<Order> findByCreateUserAndCreateDateBetween(@Param("user") String user, @Param("dateFrom") Date dateFrom,
			@Param("dateTo") Date dateTo);

	@Query("select o from Order o where o.createUser = :user AND o.orderStatus = :status AND o.createDate > :dateFrom AND o.createDate < :dateTo")
	List<Order> findByCreateUserAndOrderStatusAndCreateDateBetween(@Param("user") String user,
			@Param("status") String status, @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

	@Query("select o from Order o where o.createDate >= :today AND o.isCompleted = false AND (o.orderStatus = 'WAITFORPAY' OR o.orderStatus = 'PAID')")
	List<Order> getJustPaid(@Param("today") Date today);
	
	@Query("select o from Order o where o.createDate >= :today AND o.isCompleted = false AND o.orderStatus = 'PREPARING'")
	List<Order> getJustRepaired(@Param("today") Date today);
	
	@Query("select o from Order o where o.createDate >= :today AND o.isCompleted = false AND o.orderStatus = 'DELIVERY'")
	List<Order> getJustDelivered(@Param("today") Date today);
}
