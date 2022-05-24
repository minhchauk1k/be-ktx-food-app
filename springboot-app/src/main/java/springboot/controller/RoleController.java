package springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import springboot.model.Role;
import springboot.service.RoleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
public class RoleController {
	@Autowired
	private final RoleService roleService;

	@GetMapping("/all")
	public ResponseEntity<List<Role>> getRoles() {
		List<Role> roles = roleService.getRoles();
		return new ResponseEntity<>(roles, HttpStatus.OK);
	}
}
