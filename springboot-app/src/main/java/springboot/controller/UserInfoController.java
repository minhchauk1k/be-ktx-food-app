package springboot.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import springboot.model.Role;
import springboot.model.UserInfo;
import springboot.service.UserInfoService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserInfoController {
	private final UserInfoService userService;
	private static final String BEARER = "Bearer ";
	private static final int hour = 60 * 60;

	@GetMapping
	public ResponseEntity<List<UserInfo>> getAllUser() {
		List<UserInfo> usersList = userService.getAllUsers();
		return new ResponseEntity<>(usersList, HttpStatus.OK);
	}

	@GetMapping("/find/{id}")
	public ResponseEntity<UserInfo> getUserById(@PathVariable("id") int id) {
		UserInfo user = userService.findUserById(id);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PostMapping("/add")
	public ResponseEntity<UserInfo> addUser(@RequestBody UserInfo userInfo) {
		UserInfo user = userService.addUser(userInfo);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<UserInfo> updateUser(@RequestBody UserInfo userInfo) {
		UserInfo user = userService.updateUser(userInfo);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable("id") int id) {
		userService.deleteUserById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/refresh_token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws JsonGenerationException, JsonMappingException, IOException {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
			try {
				String refreshToken = authorizationHeader.substring(BEARER.length());
				Algorithm algorithm = Algorithm.HMAC256("minhchau".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(refreshToken);
				String username = decodedJWT.getSubject();
				UserInfo user = userService.findUserByName(username);
				String accessToken = JWT.create().withSubject(user.getUserName())
						.withExpiresAt(new Date(System.currentTimeMillis() + hour * 100))
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

	private List<String> getRoleList(UserInfo user) {
		return user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
	}
}
