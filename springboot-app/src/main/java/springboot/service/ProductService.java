package springboot.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import springboot.exception.EntityNotFoundException;
import springboot.model.Product;
import springboot.repository.ProductRepository;

@Service
public class ProductService {
	private final ProductRepository productRepo;

	@Autowired
	public ProductService(ProductRepository productRepo) {
		this.productRepo = productRepo;
	}

	public Product addProduct(Product product) {
		if (product.getProductCode().isEmpty()) {
			String createCode = "PD_" + String.format("%05d", productRepo.count());
			product.setProductCode(createCode);
		}
		product.setCreateDate(new Date());
		product.setCreateUser("admin");
		product.setUpdateDate(new Date());
		product.setUpdateUser("admin");
		return productRepo.save(product);
	}

	public List<Product> getAllProducts() {
		return productRepo.findAll();
	}

	public Product updateProduct(Product product) {
		product.setUpdateDate(new Date());
		product.setUpdateUser("admin");
		return productRepo.save(product);
	}

	public Product findProductById(Long id) {
		return productRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product by id " + id + "was not found!"));
	}

	public void deteteProductById(Long id) {
		Product entity = this.findProductById(id);
		entity.setUpdateDate(new Date());
		entity.setUpdateUser("admin");
		entity.setIsDeleted(true);
		productRepo.save(entity);
//		productRepo.deteteById(id);
	}
}
