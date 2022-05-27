package springboot.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.enums.MConst;
import springboot.exception.EntityNotFoundException;
import springboot.model.Order;
import springboot.model.OrderDetails;
import springboot.model.OrderLot;
import springboot.repository.OrderLotRepository;
import springboot.repository.OrderRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
	@Autowired
	private final OrderRepository orderRepo;
	@Autowired
	private final OrderLotRepository lotRepo;
	@Autowired
	private final SystemParameterService parameterService;
	@Autowired
	private final CommonService commonService;

	public Order add(Order order) {
		if (order.getOrderCode() == null || order.getOrderCode().isEmpty()) {
			String createCode = "ODR_" + String.format("%05d", orderRepo.count() + 1);
			order.setOrderCode(createCode);
		}

		for (OrderDetails detail : order.getDetails()) {
			detail.setOrder(order);
		}
		
		order.setCreateDate(new Date());
		order.setCreateUser(commonService.getCurrentUser());
		log.info("Added new Order: {}", order.getOrderCode());
		return orderRepo.save(order);
	}

	public List<Order> getOrders() {
		try {
			return orderRepo.findAll();
		} catch (Exception e) {
			log.info("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<Order> getOrdersJustPaid() {
		try {
			List<String> list = new ArrayList<>();
			list.add(MConst.PAID);
			list.add(MConst.WAITFORPAY);
			return orderRepo.findByOrderStatusInAndIsCompleted(list, false);
		} catch (Exception e) {
			log.info("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<Order> getOrdersJustRepaired() {
		try {
			return orderRepo.findByOrderStatusAndIsCompleted(MConst.PREPARING, false);
		} catch (Exception e) {
			log.info("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<Order> getOrdersJustDelivered() {
		try {
			return orderRepo.findByOrderStatusAndIsCompleted(MConst.DELIVERY, false);
		} catch (Exception e) {
			log.info("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public Order updateStatusByIdAndStatus(Long id, String status) {
		Order entity = findById(id);
		entity.setOrderStatus(status);
		entity.setUpdateUser(commonService.getCurrentUser());
		entity.setUpdateDate(new Date());
		log.info("Updated Order with Status: {} by id: {}", new Object[] { entity.getOrderStatus(), id });
		return orderRepo.save(entity);
	}

	public void deliveryOrders(List<Long> idList) {
		boolean isLotControl = parameterService.getIsLotControl();

		if (isLotControl) {
			// tạo lô mới
			OrderLot orderLot = new OrderLot();
			String createCode = "LOT_" + String.format("%05d", lotRepo.count() + 1);
			orderLot.setLotCode(createCode);
			// thêm các đơn hàng vào lô
			for (Long id : idList) {
				Order entity = findById(id);
				entity.setOrderStatus(MConst.DELIVERY);
				orderLot.getDetails().add(orderRepo.save(entity));
			}
			// lưu lại lô vừa tạo
			lotRepo.save(orderLot);
		} else {
			// cập nhật Status cho từng đơn hàng theo Id
			updateOrderStatusByListIdAndValue(idList, MConst.DELIVERY);
		}
		log.info("Updated Orders with Status: {} by id: {}", new Object[] { MConst.DELIVERY, idList });
	}

	public void completeOrders(List<Long> idList) {
		updateOrderStatusByListIdAndValue(idList, MConst.COMPLETED);
		log.info("Updated Orders with Status: {} by id: {}", new Object[] { MConst.COMPLETED, idList });
	}

	private void checkUpdateCompleteLot(List<Long> idList) {
		boolean isLotControl = parameterService.getIsLotControl();
		if (!isLotControl) {
			return;
		}
		
		// tạo list Order từ các id lẻ tẻ
		List<Order> orderList = new ArrayList<>();
		idList.forEach(val -> {
			orderList.add(orderRepo.getById(val));
		});

		// tìm các lot theo order lẻ tẻ
		List<OrderLot> lotList = lotRepo.findByDetailsIn(orderList);
		
		for (OrderLot lot: lotList) {
			if (lot.isCompleted()) {
				return;
			}
			
			boolean isCompleted = true;
			for (Order order: lot.getDetails()) {
				if (!order.isCompleted()) {
					isCompleted = false;
					return;
				}
			}
			
			if (isCompleted) {
				lot.setCompleted(isCompleted);
				lotRepo.save(lot);
				log.info("Lot: {} status is: {}", new Object[] { lot.getLotCode(), MConst.COMPLETED });
			}
		}
	}

	private void updateOrderStatusByListIdAndValue(List<Long> idList, String value) {
		for (Long id : idList) {
			Order entity = findById(id);
			if (value.equals(MConst.COMPLETED)) {
				entity.setOrderStatus(value);
				entity.setCompleted(true);
			} else {
				entity.setOrderStatus(value);
			}
			entity.setOrderStatus(value);
			orderRepo.save(entity);
		}

		if (value.equals(MConst.COMPLETED)) {
			checkUpdateCompleteLot(idList);
		}
	}

	public Order findById(Long id) {
		return orderRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Order with id: " + id + "was not found!"));
	}
}
