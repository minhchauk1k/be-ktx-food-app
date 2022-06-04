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
import springboot.model.Order;
import springboot.model.OrderLot;
import springboot.repository.OrderLotRepository;
import springboot.repository.OrderRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderLotService {
	@Autowired
	private final OrderLotRepository lotRepo;
	@Autowired
	private final OrderRepository orderRepo;
	@Autowired
	private final CommonService commonService;

	public OrderLot add(OrderLot orderLot) {
		// tạo LotCode
		if (orderLot.getLotCode() == null || orderLot.getLotCode().isEmpty()) {
			String createCode = "LOT_" + String.format("%05d", lotRepo.count() + 1);
			orderLot.setLotCode(createCode);
		}

		orderLot.setLotStatus(MConst.PREPARING);
		changeOrdersStatus(orderLot.getDetails(), MConst.PREPARING);

		orderLot.setCreateDate(new Date());
		orderLot.setCreateUser(commonService.getCurrentUser());
		log.info("Added new Lot: {} with Status: {}", new Object[] { orderLot.getLotCode(), MConst.PREPARING });
		return lotRepo.save(orderLot);
	}

	private void changeOrdersStatus(List<Order> list, String value) {
		list.forEach(order -> {
			order.setOrderStatus(value);
			order.setUpdateUser(commonService.getCurrentUser());
			order.setUpdateDate(new Date());
		});

		orderRepo.saveAll(list);
	}

	public List<OrderLot> getOrderLots() {
		try {
			return lotRepo.findAll();
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<OrderLot> getOrderLotsIncompleted() {
		try {
			return lotRepo.findByIsCompleted(false);
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}
	
	public List<OrderLot> getOrderLotsJustRepaired() {
		try {
			return lotRepo.findByLotStatus(MConst.PREPARING);
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<OrderLot> getOrdersByLotId() {
		try {
			return lotRepo.findByIsCompleted(false);
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

}
