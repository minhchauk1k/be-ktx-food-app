package springboot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.model.OrderLot;
import springboot.repository.OrderLotRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderLotService {
	@Autowired
	private final OrderLotRepository lotRepo;

	public List<OrderLot> getOrderLots() {
		try {
			return lotRepo.findAll();
		} catch (Exception e) {
			log.info("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}
	
	public List<OrderLot> getOrderLotsIncompleted() {
		try {
			return lotRepo.findByIsCompleted(false);
		} catch (Exception e) {
			log.info("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<OrderLot> getOrdersByLotId() {
		try {
			return lotRepo.findByIsCompleted(false);
		} catch (Exception e) {
			log.info("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}
	
	
}
