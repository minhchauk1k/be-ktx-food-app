package springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import springboot.model.Category;
import springboot.model.SystemParameter;
import springboot.service.CategoryService;
import springboot.service.SystemParameterService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {
	@Autowired
	private final SystemParameterService paramService;
	@Autowired
	private final CategoryService categoryService;

	@GetMapping("/parameter/{key}")
	public ResponseEntity<SystemParameter> getParameter(@PathVariable("key") String key) {
		SystemParameter param = paramService.getByKey(key);
		return new ResponseEntity<>(param, HttpStatus.OK);
	}

	@GetMapping("/parameters")
	public ResponseEntity<List<SystemParameter>> getParameters() {
		List<SystemParameter> params = paramService.getParameters();
		return new ResponseEntity<>(params, HttpStatus.OK);
	}
	
	@GetMapping("/categorys")
	public ResponseEntity<List<Category>> getCategorys() {
		List<Category> params = categoryService.getCategorys();
		return new ResponseEntity<>(params, HttpStatus.OK);
	}
}
