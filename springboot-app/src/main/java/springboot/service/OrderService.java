package springboot.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.common.ConstDefined;
import springboot.exception.EntityNotFoundException;
import springboot.model.Order;
import springboot.model.OrderDetails;
import springboot.repository.OrderRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {
	@Autowired
	private final OrderRepository orderRepo;

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

	public List<Order> getOrders() {
		try {
			return orderRepo.findAll();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public List<Order> getOrdersJustPaid() {
		try {
			List<String> list = new ArrayList<>();
			list.add(ConstDefined.PAID);
			list.add(ConstDefined.WAITFORPAY);
			return orderRepo.findByOrderStatusIn(list);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public List<Order> getOrdersJustRepaired() {
		try {
			return orderRepo.findByOrderStatus(ConstDefined.PREPARING);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public Order updateStatusByIdAndStatus(Long id, String status) {
		Order entity = findById(id);
		if (entity != null) {
			entity.setOrderStatus(status);
			log.info("Updated Order with Status: {} by id: {}", new Object[] { entity.getOrderStatus(), id });
			return orderRepo.save(entity);
		} else {
			throw new EntityNotFoundException("Order with id: " + id + " was not found!");
		}

	}

	public Order findById(Long id) {
		try {
			return orderRepo.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Order with id: " + id + "was not found!"));
		} catch (Exception e) {
			return null;
		}
	}
}
