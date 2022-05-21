package springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
}
