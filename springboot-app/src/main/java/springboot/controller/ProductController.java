package springboot.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springboot.model.Product;
import springboot.service.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController {
	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping("/all")
	public ResponseEntity<List<Product>> getAllProducts() {
		List<Product> productsList = productService.getProducts();
		return new ResponseEntity<>(productsList, HttpStatus.OK);
	}

	@GetMapping("/all/{type}/{isDelete}")
	public ResponseEntity<List<Product>> getAllProductsWithParam(@PathVariable("type") String type,
			@PathVariable("isDelete") boolean isDelete) {
		List<Product> productsList = productService.getProductsWithParams(type, isDelete);
		return new ResponseEntity<>(productsList, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
		Product product = productService.findById(id);
		return new ResponseEntity<>(product, HttpStatus.OK);
	}

	@PostMapping("/add")
	public ResponseEntity<Product> addProduct(@RequestBody Product product) {
		Product newProduct = productService.add(product);
		return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
		Product updateProduct = productService.update(product);
		return new ResponseEntity<>(updateProduct, HttpStatus.OK);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deletetProduct(@PathVariable("id") Long id) {
		productService.softDeleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
