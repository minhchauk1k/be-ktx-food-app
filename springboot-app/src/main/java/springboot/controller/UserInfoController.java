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

import springboot.model.UserInfo;
import springboot.service.UserInfoService;

@RestController
@RequestMapping("/user")
public class UserInfoController {

	private final UserInfoService userInfoService;
	
	public UserInfoController(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}
	
	@GetMapping
	public ResponseEntity<List<UserInfo>> getAllUser() {
		List<UserInfo> usersList = userInfoService.getAllUserInfo();
		return new ResponseEntity<>(usersList, HttpStatus.OK);
	}
	
	@GetMapping("/find/{id}")
	public ResponseEntity<UserInfo> getUserById(@PathVariable("id") Long id) {
		UserInfo user = userInfoService.findUserInfoById(id);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	@PostMapping("/add")
	public ResponseEntity<UserInfo> addUser(@RequestBody UserInfo userInfo) {
		UserInfo user = userInfoService.addUserInfo(userInfo);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}
	
	@PutMapping("/update")
	public ResponseEntity<UserInfo> updateUser(@RequestBody UserInfo userInfo) {
		UserInfo user = userInfoService.updateUserInfo(userInfo);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
		userInfoService.deleteUserInfoById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
