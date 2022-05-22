package springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import lombok.RequiredArgsConstructor;
import springboot.model.Product;
import springboot.service.ProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
	@Autowired
	private final ProductService productService;

	@GetMapping("/all")
	public ResponseEntity<List<Product>> getProducts() {
		List<Product> productsList = productService.getProducts();
		return new ResponseEntity<>(productsList, HttpStatus.OK);
	}

	@GetMapping("/all/type={type}&isDelete={isDelete}")
	public ResponseEntity<List<Product>> getByTypeAndIsDelete(@PathVariable("type") String type,
			@PathVariable("isDelete") boolean isDelete) {
		List<Product> productsList = productService.getByTypeAndIsDelete(type, isDelete);
		return new ResponseEntity<>(productsList, HttpStatus.OK);
	}

	@GetMapping("/all/isDelete={isDelete}")
	public ResponseEntity<List<Product>> getByIsDelete(@PathVariable("isDelete") boolean isDelete) {
		List<Product> productsList = productService.getByIsDelete(isDelete);
		return new ResponseEntity<>(productsList, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getById(@PathVariable("id") Long id) {
		Product product = productService.findById(id);
		return new ResponseEntity<>(product, HttpStatus.OK);
	}

	@PostMapping("/add")
	public ResponseEntity<Product> add(@RequestBody Product product) {
		Product newProduct = productService.add(product);
		return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<Product> update(@RequestBody Product product) {
		Product updatedProduct = productService.update(product);
		return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deletet(@PathVariable("id") Long id) {
		productService.softDeleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
