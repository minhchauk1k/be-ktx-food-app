package springboot.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.exception.EntityNotFoundException;
import springboot.model.Product;
import springboot.repository.ProductRepository;

@Service @Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductService {
	private final ProductRepository productRepo;

	public Product addProduct(Product product) {
		if (product.getProductCode().isEmpty()) {
			String createCode = "PD_" + String.format("%05d", productRepo.count());
			product.setProductCode(createCode);
		}
		product.setCreateDate(new Date());
		product.setCreateUser("admin");
		product.setUpdateDate(new Date());
		product.setUpdateUser("admin");
		log.info("Added new Product: {}", product.getProductCode());
		return productRepo.save(product);
	}

	public List<Product> getProducts() {
		return productRepo.findAll();
	}

	public Product updateProduct(Product product) {
		product.setUpdateDate(new Date());
		product.setUpdateUser("admin");
		return productRepo.save(product);
	}

	public Product findProductById(Long id) {
		try {
			return productRepo.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Product by id " + id + "was not found!"));
		} catch (Exception e) {
			return null;
		}

	}

	public void deteteProductById(Long id) {
		Product entity = this.findProductById(id);
		entity.setUpdateDate(new Date());
		entity.setUpdateUser("admin");
		entity.setDeleted(true);
		productRepo.save(entity);
//		productRepo.deteteById(id);
	}
}
