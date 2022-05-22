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
import springboot.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private final OrderService orderService;

	@PostMapping("/add")
	public ResponseEntity<Order> add(@RequestBody Order order) {
		Order newOrder = orderService.add(order);
		return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
	}

	@GetMapping("/all")
	public ResponseEntity<List<Order>> getOrders() {
		List<Order> users = orderService.getOrders();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@GetMapping("/all/just/paid")
	public ResponseEntity<List<Order>> getOrdersJustPaid() {
		List<Order> users = orderService.getOrdersJustPaid();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@GetMapping("/all/just/repaired")
	public ResponseEntity<List<Order>> getOrdersJustRepaired() {
		List<Order> users = orderService.getOrdersJustRepaired();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@PutMapping("/update/id={id}&status={status}")
	public ResponseEntity<Order> updateByIdAndStatus(@PathVariable("id") Long id,
			@PathVariable("status") String status) {
		Order updatedOrder = orderService.updateStatusByIdAndStatus(id, status);
		return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
	}
}
