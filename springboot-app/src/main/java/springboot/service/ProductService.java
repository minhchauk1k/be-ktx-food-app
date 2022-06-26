package springboot.service;

import java.math.BigDecimal;
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
import springboot.model.Category;
import springboot.model.OrderDetails;
import springboot.model.Product;
import springboot.model.SystemParameter;
import springboot.repository.ProductRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductService {
	@Autowired
	private final ProductRepository productRepo;
	@Autowired
	private final CommonService commonService;
	@Autowired
	private final CategoryService categoryService;
	@Autowired
	private final SystemParameterService paramService;

	public void updateProductQtyByOrder(List<OrderDetails> orderDetails) {
		SystemParameter autoInventory = paramService.getByKey(MConst.AUTO_INVENTORY);
		if (!autoInventory.getParameterValue().equals(MConst.NO)) {
			orderDetails.forEach(val -> {
				int checkQty = val.getProduct().getQty() - val.getQty();

				val.getProduct().setQty(checkQty);

				if (checkQty <= 0) {
					val.getProduct().setInventory(false);
					log.info("{} is out of stock!", val.getProduct().getProductName());
				}
				productRepo.save(val.getProduct());
			});
		}
	}

	public Product add(Product product) {
		// tạo ProductCode
		if (product.getProductCode() == null || product.getProductCode().isEmpty()) {
			String createCode = "PD_" + String.format("%05d", productRepo.count() + 1);
			product.setProductCode(createCode);
		}

		// tạo FinalPrice
		if (product.isDiscount()) {
			switch (product.getDiscountType()) {
			case MConst.NUMBER:
				product.setFinalPrice(product.getPrice().subtract(product.getDiscountNumber()));
				product.setDiscountPercent(BigDecimal.ZERO);
				break;

			case MConst.PERCENT:
				BigDecimal hundred = new BigDecimal(100);
				BigDecimal discount = product.getPrice().multiply(product.getDiscountPercent());
				BigDecimal discountHundred = discount.divide(hundred);
				product.setFinalPrice(product.getPrice().subtract(discountHundred));
				product.setDiscountNumber(BigDecimal.ZERO);
				break;
			}
		} else {
			product.setFinalPrice(product.getPrice());
			product.setDiscountPercent(BigDecimal.ZERO);
			product.setDiscountNumber(BigDecimal.ZERO);
		}

		// tạo qty theo plan
		product.setQty(product.getPlanQty());

		product.setCreateDate(new Date());
		product.setCreateUser(commonService.getCurrentUser());

		// thêm mới Category nếu chưa tồn tại
		addCategory(product.getCategory(), product.getCategory(), product.getType());

		log.info("Added new Product: {}", product.getProductName());
		return productRepo.save(product);
	}

	private void addCategory(String key, String value, String type) {
		if (!categoryService.isExist(key, value, type)) {
			categoryService.add(new Category(key, value, type));
		}
	}

	public List<Product> getProducts() {
		try {
			return productRepo.findAll();
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<Product> getByTypeAndIsDelete(String type, boolean isDelete) {
		try {
			return productRepo.findByTypeAndIsDeleted(type, isDelete);
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<Product> getByIsDelete(boolean isDelete) {
		try {
			return productRepo.findByIsDeleted(isDelete);
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public Product update(Product product) {
		Product entity = findById(product.getId());
		product.setCreateUser(entity.getCreateUser());
		product.setCreateDate(entity.getCreateDate());
		product.setUpdateDate(new Date());
		product.setUpdateUser(commonService.getCurrentUser());

		// tạo FinalPrice
		if (product.isDiscount()) {
			switch (product.getDiscountType()) {
			case MConst.NUMBER:
				product.setFinalPrice(product.getPrice().subtract(product.getDiscountNumber()));
				product.setDiscountPercent(BigDecimal.ZERO);
				break;

			case MConst.PERCENT:
				BigDecimal hundred = new BigDecimal(100);
				BigDecimal discount = product.getPrice().multiply(product.getDiscountPercent());
				BigDecimal discountHundred = discount.divide(hundred);
				product.setFinalPrice(product.getPrice().subtract(discountHundred));
				product.setDiscountNumber(BigDecimal.ZERO);
				break;
			}
		} else {
			product.setFinalPrice(product.getPrice());
			product.setDiscountPercent(BigDecimal.ZERO);
			product.setDiscountNumber(BigDecimal.ZERO);
		}

		// tạo qty theo plan
		product.setQty(product.getPlanQty());

		// thêm mới Category nếu chưa tồn tại
		addCategory(product.getCategory(), product.getCategory(), product.getType());

		log.info("Updated Product: {} by {}", new Object[] { entity.getProductCode(), entity.getUpdateUser() });
		return productRepo.save(product);
	}

	public Product findById(Long id) {
		return productRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product with id: " + id + "was not found!"));
	}

	public Product findByProductCode(String code) {
		return productRepo.findByProductCode(code)
				.orElseThrow(() -> new EntityNotFoundException("Product with code: " + code + "was not found!"));
	}

	public void softDeleteById(Long id) {
		Product entity = findById(id);
		entity.setUpdateDate(new Date());
		entity.setUpdateUser(commonService.getCurrentUser());
		entity.setDeleted(true);
		log.info("Soft delete a Product: {}", entity.getProductCode());
		productRepo.save(entity);
	}

	public void deleteById(Long id) {
		productRepo.deleteById(id);
	}
}
