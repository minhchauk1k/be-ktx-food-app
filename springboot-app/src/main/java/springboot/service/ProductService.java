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
import springboot.common.ConstDefined;
import springboot.exception.EntityNotFoundException;
import springboot.model.Category;
import springboot.model.Product;
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

	private Product _add(Product product) {
		// tạo ProductCode
		if (product.getProductCode() == null || product.getProductCode().isEmpty()) {
			String createCode = "PD_" + String.format("%05d", productRepo.count() + 1);
			product.setProductCode(createCode);
		}

		// tạo FinalPrice
		if (product.isDiscount()) {
			switch (product.getDiscountType()) {
			case ConstDefined.NUMBER:
				product.setFinalPrice(product.getPrice().subtract(product.getDiscountNumber()));
				product.setDiscountPercent(BigDecimal.ZERO);
				break;

			case ConstDefined.PERCENT:
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

		product.setCreateDate(new Date());
		product.setCreateUser(commonService.getCurrentUser());
		product.setUpdateDate(new Date());
		product.setUpdateUser(commonService.getCurrentUser());
		log.info("Added new Product: {}", product.getProductName());
		return productRepo.save(product);
	}

	public Product add(Product product) {
		product = _add(product);
		addCategory(product.getCategory(), product.getCategory(), product.getType());
		return product;
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
			return new ArrayList<>();
		}
	}

	public List<Product> getByTypeAndIsDelete(String type, boolean isDelete) {
		try {
			return productRepo.findByTypeAndIsDeleted(type, isDelete);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public Product update(Product product) {
		Product entity = findById(product.getId());
		if (entity == null) {
			entity = findByCode(product.getProductCode());
		}

		if (entity != null) {
			product.setCreateUser(entity.getCreateUser());
			product.setCreateDate(entity.getCreateDate());
			product.setUpdateDate(new Date());
			product.setUpdateUser(commonService.getCurrentUser());
			
			// tạo FinalPrice
			if (product.isDiscount()) {
				switch (product.getDiscountType()) {
				case ConstDefined.NUMBER:
					product.setFinalPrice(product.getPrice().subtract(product.getDiscountNumber()));
					product.setDiscountPercent(BigDecimal.ZERO);
					break;

				case ConstDefined.PERCENT:
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

			// thêm mới Category nếu chưa tồn tại
			addCategory(product.getCategory(), product.getCategory(), product.getType());

			log.info("Updated a Product: {}", entity.getProductCode());
			return productRepo.save(product);
		} else {
			throw new EntityNotFoundException("Product with id: " + product.getId() + " was not found!");
		}
	}

	public Product findById(Long id) {
		try {
			return productRepo.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Product with id: " + id + "was not found!"));
		} catch (Exception e) {
			return null;
		}
	}

	public Product findByCode(String code) {
		try {
			return productRepo.findByProductCode(code)
					.orElseThrow(() -> new EntityNotFoundException("Product with code: " + code + "was not found!"));
		} catch (Exception e) {
			return null;
		}
	}

	public void softDeleteById(Long id) {
		Product entity = findById(id);
		if (entity != null) {
			entity.setUpdateDate(new Date());
			entity.setUpdateUser("admin");
			entity.setDeleted(true);
			log.info("Soft delete a Product: {}", entity.getProductCode());
			productRepo.save(entity);
		} else {
			throw new EntityNotFoundException("Product with id: " + id + " was not found!");
		}
	}

	public void deleteById(Long id) {
		Product entity = findById(id);
		if (entity != null) {
			productRepo.deleteById(id);
		} else {
			throw new EntityNotFoundException("Product with id: " + id + " was not found!");
		}
	}
}
