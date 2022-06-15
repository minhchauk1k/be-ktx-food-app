package springboot.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.enums.MConst;
import springboot.model.Address;
import springboot.model.Category;
import springboot.model.Role;
import springboot.model.SystemParameter;
import springboot.model.User;
import springboot.service.AddressService;
import springboot.service.CategoryService;
import springboot.service.SystemParameterService;
import springboot.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {
	@Autowired
	private final SystemParameterService paramService;
	@Autowired
	private final CategoryService categoryService;
	@Autowired
	private final UserService userService;
	@Autowired
	private final AddressService addressService;

	@GetMapping("address/all")
	public ResponseEntity<List<Address>> getAddresses() {
		List<Address> addresses = addressService.getAddresses();
		return new ResponseEntity<>(addresses, HttpStatus.OK);
	}

	@GetMapping("address/all/type={type}")
	public ResponseEntity<List<Address>> getByType(@PathVariable("type") String type) {
		List<Address> addresses = addressService.getByType(type);
		return new ResponseEntity<>(addresses, HttpStatus.OK);
	}

	@PostMapping("address/add")
	public ResponseEntity<Address> addAddress(@RequestBody Address address) {
		Address newAddress = addressService.add(address);
		return new ResponseEntity<>(newAddress, HttpStatus.CREATED);
	}

	@PutMapping("/address/update")
	public ResponseEntity<Address> updateAddress(@RequestBody Address address) {
		Address addressUpdated = addressService.update(address);
		return new ResponseEntity<>(addressUpdated, HttpStatus.OK);
	}

	@DeleteMapping("/address/delete/{id}")
	public ResponseEntity<?> deletetAddress(@PathVariable("id") Long id) {
		addressService.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/parameter/{key}")
	public ResponseEntity<SystemParameter> getParameter(@PathVariable("key") String key) {
		SystemParameter param = paramService.getByKey(key);
		return new ResponseEntity<>(param, HttpStatus.OK);
	}

	@PutMapping("/parameter/{key}")
	public ResponseEntity<SystemParameter> updateParameter(@RequestBody SystemParameter param) {
		SystemParameter paramUpdated = paramService.updateParameter(param);
		return new ResponseEntity<>(paramUpdated, HttpStatus.OK);
	}

	@GetMapping("/parameters")
	public ResponseEntity<List<SystemParameter>> getParameters() {
		List<SystemParameter> params = paramService.getParameters();
		return new ResponseEntity<>(params, HttpStatus.OK);
	}

	@GetMapping("/category/{type}")
	public ResponseEntity<List<Category>> getCategorys(@PathVariable("type") String type) {
		List<Category> categories = categoryService.getByType(type);
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	@GetMapping("/category/all")
	public ResponseEntity<List<Category>> getCategorys() {
		List<Category> categories = categoryService.getCategories();
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	@PostMapping("/category/add")
	public ResponseEntity<Category> addCategory(@RequestBody Category category) {
		Category categoryNew = categoryService.add(category);
		return new ResponseEntity<>(categoryNew, HttpStatus.OK);
	}

	@PutMapping("/category/update")
	public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
		Category categoryUpdated = categoryService.update(category);
		return new ResponseEntity<>(categoryUpdated, HttpStatus.OK);
	}

	@DeleteMapping("/category/delete/{id}")
	public ResponseEntity<?> deletetCategory(@PathVariable("id") Long id) {
		categoryService.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/refresh_token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws JsonGenerationException, JsonMappingException, IOException {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith(MConst.BEARER)) {
			try {
				String refreshToken = authorizationHeader.substring(MConst.BEARER.length());
				Algorithm algorithm = Algorithm.HMAC256("minhchau".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(refreshToken);
				String username = decodedJWT.getSubject();
				User user = userService.findByUserName(username);
				String accessToken = JWT.create().withSubject(user.getUserName())
						.withExpiresAt(new Date(System.currentTimeMillis() + MConst.WEEK))
						.withIssuer(request.getRequestURL().toString()).withClaim("roles", getRoleList(user))
						.sign(algorithm);

				// createResponseToken
				Map<String, String> tokens = new HashMap<>();
				tokens.put("accessToken", accessToken);
				tokens.put("refreshToken", refreshToken);

				// set type is JSON
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
			} catch (Exception e) {
				log.error(e.getMessage());

				response.setStatus(HttpStatus.FORBIDDEN.value());
				response.setHeader("error", e.getMessage());
				Map<String, String> error = new HashMap<>();
				error.put("error_message", e.getMessage());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		} else {
			throw new RuntimeException("Refresh Token is missing!");
		}
	}

	private List<String> getRoleList(User user) {
		return user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList());
	}
}
