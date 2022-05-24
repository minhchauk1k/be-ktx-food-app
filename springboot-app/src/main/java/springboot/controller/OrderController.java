package springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import springboot.model.Order;
import springboot.model.OrderLot;
import springboot.service.OrderLotService;
import springboot.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private final OrderService orderService;
	@Autowired
	private final OrderLotService lotService;

	@PostMapping("/add")
	public ResponseEntity<Order> add(@RequestBody Order order) {
		Order newOrder = orderService.add(order);
		return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
	}

	@GetMapping("/all")
	public ResponseEntity<List<Order>> getOrders() {
		List<Order> orders = orderService.getOrders();
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	@GetMapping("/all/just/paid")
	public ResponseEntity<List<Order>> getOrdersJustPaid() {
		List<Order> orders = orderService.getOrdersJustPaid();
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	@GetMapping("/all/just/repaired")
	public ResponseEntity<List<Order>> getOrdersJustRepaired() {
		List<Order> orders = orderService.getOrdersJustRepaired();
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}
	
	@GetMapping("/all/just/delivered")
	public ResponseEntity<List<Order>> getOrdersJustDelivered() {
		List<Order> orders = orderService.getOrdersJustDelivered();
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	@PutMapping("update/status/id={id}&status={status}")
	public ResponseEntity<Order> updateStatusByIdAndStatus(@PathVariable("id") Long id,
			@PathVariable("status") String status) {
		Order updatedOrder = orderService.updateStatusByIdAndStatus(id, status);
		return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
	}
	
	@PutMapping("update/delivery")
	public ResponseEntity<Order> deliveryOrders(@RequestBody List<Long> idList) {
		orderService.deliveryOrders(idList);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("update/complete")
	public ResponseEntity<Order> completeOrders(@RequestBody List<Long> idList) {
		orderService.completeOrders(idList);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/lot/all")
	public ResponseEntity<List<OrderLot>> getOrderLots() {
		List<OrderLot> orders = lotService.getOrderLots();
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}
	
	@GetMapping("/lot/all/incompleted")
	public ResponseEntity<List<OrderLot>> getOrderLotsIncompleted() {
		List<OrderLot> orders = lotService.getOrderLotsIncompleted();
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}
}
