package springboot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.enums.MConst;
import springboot.model.Product;
import springboot.model.SystemParameter;
import springboot.repository.ProductRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ScheduledService {
	@Autowired
	private final SystemParameterService paramService;
	@Autowired
	private final ProductService productService;
	@Autowired
	private final ProductRepository productRepo;

	@Scheduled(cron = "0 0 0 * * *")
	public void scheduleTask() throws InterruptedException {

		// check AUTO_INVENTORY
		SystemParameter autoInventory = paramService.getByKey(MConst.AUTO_INVENTORY);
		if (!autoInventory.getParameterValue().equals(MConst.NO)) {
			List<Product> products = new ArrayList<>();

			switch (autoInventory.getParameterValue()) {
			case MConst.BOTH:
				products = productService.getByIsDelete(false);
				break;

			case MConst.FOOD:
				products = productService.getByTypeAndIsDelete(MConst.FOOD, false);
				break;

			case MConst.SERVICE:
				products = productService.getByTypeAndIsDelete(MConst.SERVICE, false);
				break;
			}

			products.forEach(val -> {
				val.setInventory(true);
			});
			log.info("Updated Inventory for product list by {} = {}",
					new Object[] { MConst.AUTO_INVENTORY, autoInventory.getParameterValue() });
			productRepo.saveAll(products);
		}
	}
}
