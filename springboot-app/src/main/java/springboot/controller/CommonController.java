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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import springboot.common.ConstDefined;
import springboot.model.Category;
import springboot.model.Role;
import springboot.model.SystemParameter;
import springboot.model.User;
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

	@GetMapping("/categorys/{type}")
	public ResponseEntity<List<Category>> getCategorys(@PathVariable("type") String type) {
		List<Category> params = categoryService.getByType(type);
		return new ResponseEntity<>(params, HttpStatus.OK);
	}

	@GetMapping("/refresh_token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws JsonGenerationException, JsonMappingException, IOException {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith(ConstDefined.BEARER)) {
			try {
				String refreshToken = authorizationHeader.substring(ConstDefined.BEARER.length());
				Algorithm algorithm = Algorithm.HMAC256("minhchau".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(refreshToken);
				String username = decodedJWT.getSubject();
				User user = userService.findByUsername(username);
				String accessToken = JWT.create().withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + ConstDefined.WEEK))
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
