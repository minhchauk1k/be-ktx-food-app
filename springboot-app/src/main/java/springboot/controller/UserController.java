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
import springboot.model.User;
import springboot.service.CommonService;
import springboot.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	@Autowired
	private final UserService userService;
	@Autowired
	private final CommonService commonService;

	@GetMapping("/all")
	public ResponseEntity<List<User>> getUsers() {
		List<User> users = userService.getUsers();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}
	@GetMapping("/current")
	public ResponseEntity<User> getMyInfo() {
		User user = userService.findByUserName(commonService.getCurrentUser());
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@GetMapping("/find/{id}")
	public ResponseEntity<User> getById(@PathVariable("id") Long id) {
		User user = userService.findById(id);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PostMapping("/add")
	public ResponseEntity<User> add(@RequestBody User user) {
		User newUser = userService.add(user);
		return new ResponseEntity<>(newUser, HttpStatus.CREATED);
	}
	
	@PostMapping("/exist/userName")
	public ResponseEntity<Boolean> checkExistByUserName(@RequestBody String userName) {
		boolean isExist = userService.checkExistByUserName(userName);
		return new ResponseEntity<>(isExist, HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<User> update(@RequestBody User user) {
		User updatedUser = userService.updateUser(user);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
		userService.softDeleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
