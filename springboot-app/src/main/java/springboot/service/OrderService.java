package springboot.service;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.model.Order;
import springboot.model.OrderDetails;
import springboot.repository.OrderDetailsRepository;
import springboot.repository.OrderRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {
	private final OrderRepository orderRepo;
	private final OrderDetailsRepository detailsRepo;

	public Order add(Order order) {
		if (order.getOrderCode() == null || order.getOrderCode().isEmpty()) {
			String createCode = "ODR_" + String.format("%05d", orderRepo.count() + 1);
			order.setOrderCode(createCode);
		}

		for (OrderDetails detail : order.getDetails()) {
			detail.setOrder(order);
		}

		order.setCreateDate(new Date());
		order.setCreateUser("admin");
		log.info("Added new Order: {}", order.getOrderCode());
		return orderRepo.save(order);
	}
}
