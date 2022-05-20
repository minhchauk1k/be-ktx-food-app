package springboot.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.exception.EntityNotFoundException;
import springboot.model.Product;
import springboot.repository.ProductRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductService {
	@Autowired
	private final ProductRepository productRepo;

	public Product add(Product product) {
		if (product.getProductCode().isEmpty()) {
			String createCode = "PD_" + String.format("%05d", productRepo.count() + 1);
			product.setProductCode(createCode);
		}
		product.setCreateDate(new Date());
		product.setCreateUser("admin");
		product.setUpdateDate(new Date());
		product.setUpdateUser("admin");
		log.info("Added new Product: {}", product.getProductName());
		return productRepo.save(product);
	}

	public List<Product> getProducts() {
		try {
			return productRepo.findAll();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public List<Product> getProductsWithParams(String type, boolean isDelete) {
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
			product.setUpdateUser("admin");
			log.info("Updated a Product: {}", product.getProductCode());
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
