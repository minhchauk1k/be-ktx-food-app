package springboot.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.enums.MConst;
import springboot.exception.EntityNotFoundException;
import springboot.model.Order;
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

	private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	public Order add(Order order) {
		if (order.getOrderCode() == null || order.getOrderCode().isEmpty()) {
			String createCode = "ODR_" + String.format("%05d", orderRepo.count() + 1);
			order.setOrderCode(createCode);
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
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<Order> getOrdersOfUser(String status, String dateFrom, String dateTo) {
		try {
			String user = commonService.getCurrentUser();
			Date dateFromD = new SimpleDateFormat("dd-MM-yyyy").parse(dateFrom);
			Date dateToD = new SimpleDateFormat("dd-MM-yyyy").parse(dateTo);
			List<Order> result = new ArrayList<>();

			// increase 1 day
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateToD);
			calendar.add(Calendar.DATE, 1);
			dateToD = calendar.getTime();
			switch (status) {
			case MConst.ALL:
				result = orderRepo.findByCreateUserAndCreateDateBetween(user, dateFromD, dateToD);
				break;
			case MConst.NOT_COMPLETED:
				result.addAll(orderRepo.findByCreateUserAndOrderStatusAndCreateDateBetween(user, MConst.WAITFORPAY,
						dateFromD, dateToD));
				result.addAll(orderRepo.findByCreateUserAndOrderStatusAndCreateDateBetween(user, MConst.PAID, dateFromD,
						dateToD));
				result.addAll(orderRepo.findByCreateUserAndOrderStatusAndCreateDateBetween(user, MConst.PREPARING,
						dateFromD, dateToD));
				result.addAll(orderRepo.findByCreateUserAndOrderStatusAndCreateDateBetween(user, MConst.DELIVERY,
						dateFromD, dateToD));
				break;
			case MConst.COMPLETED:
				result = orderRepo.findByCreateUserAndOrderStatusAndCreateDateBetween(user, MConst.COMPLETED, dateFromD,
						dateToD);
				break;
			}
			log.info("Get orders history with Status: {} for {}", new Object[] { status, user });
			return result;
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<Order> getOrdersJustPaid() {
		try {
			return orderRepo.getJustPaid(getToday());
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	private Date getToday() throws ParseException {
		String todayStr = dateFormat.format(new Date());
		Date today = dateFormat.parse(todayStr);
		return today;
	}

	public List<Order> getOrdersJustRepaired() {
		try {
			return orderRepo.getJustRepaired(getToday());
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<Order> getOrdersJustDelivered() {
		try {
			return orderRepo.getJustDelivered(getToday());
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
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

	public OrderLot deliveryOrders(List<Long> idList) {
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
				entity.setUpdateUser(commonService.getCurrentUser());
				entity.setUpdateDate(new Date());
				orderLot.getDetails().add(orderRepo.save(entity));
			}
			// lưu lại lô vừa tạo
			orderLot.setCreateUser(commonService.getCurrentUser());
			orderLot.setCreateDate(new Date());
			log.info("Updated Orders with Status: {} by id: {}", new Object[] { MConst.DELIVERY, idList });
			return lotRepo.save(orderLot);
		} else {
			// cập nhật Status cho từng đơn hàng theo Id
			updateOrderStatusByListIdAndValue(idList, MConst.DELIVERY);
			log.info("Updated Orders with Status: {} by id: {}", new Object[] { MConst.DELIVERY, idList });
			return null;
		}
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

		for (OrderLot lot : lotList) {
			if (lot.isCompleted()) {
				return;
			}

			boolean isCompleted = true;
			for (Order order : lot.getDetails()) {
				if (!order.isCompleted()) {
					isCompleted = false;
					return;
				}
			}

			if (isCompleted) {
				lot.setCompleted(isCompleted);
				lot.setUpdateUser(commonService.getCurrentUser());
				lot.setUpdateDate(new Date());
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
			entity.setUpdateUser(commonService.getCurrentUser());
			entity.setUpdateDate(new Date());
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

	public List<List<Order>> getOrdersReportWeek() {
		try {
			List<List<Order>> result = new ArrayList<>();
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getToday(), getFromToday(+1)));
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getFromToday(-1), getToday()));
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getFromToday(-2), getFromToday(-1)));
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getFromToday(-3), getFromToday(-2)));
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getFromToday(-4), getFromToday(-3)));
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getFromToday(-5), getFromToday(-4)));
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getFromToday(-6), getFromToday(-5)));
			return result;
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<List<Order>> getOrdersReportLastWeek() {
		try {
			List<List<Order>> result = new ArrayList<>();
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getFromToday(-7), getFromToday(-6)));
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getFromToday(-8), getFromToday(-7)));
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getFromToday(-9), getFromToday(-8)));
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getFromToday(-10), getFromToday(-9)));
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getFromToday(-11), getFromToday(-10)));
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getFromToday(-12), getFromToday(-11)));
			result.add(orderRepo.findByIsCompletedAndCreateDateBetween(true, getFromToday(-13), getFromToday(-12)));
			return result;
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	private Date getFromToday(int value) throws ParseException {
		String todayStr = dateFormat.format(new Date());
		Date today = dateFormat.parse(todayStr);
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, value);
		return cal.getTime();
	}

	public List<Order> updateMomoStatus() {
		try {
			List<Order> entityList = orderRepo.getNewestOrderMomoByUserName(MConst.MOMO, commonService.getCurrentUser(),
					getToday(), PageRequest.of(0, 1));
			entityList.get(0).setOrderStatus(MConst.PAID);
			entityList.get(0).setPaid(true);
			entityList.get(0).setUpdateDate(new Date());
			entityList.get(0).setUpdateUser(commonService.getCurrentUser());
			log.info("Order: {} is {}", new Object[] { entityList.get(0).getOrderCode(), MConst.PAID });
			return orderRepo.saveAll(entityList);
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}
}
