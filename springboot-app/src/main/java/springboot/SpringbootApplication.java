package springboot;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import springboot.model.Role;
import springboot.model.UserInfo;
import springboot.service.RoleService;
import springboot.service.UserInfoService;

@SpringBootApplication
public class SpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}
	
//	@Bean
	CommandLineRunner run(UserInfoService userService, RoleService roleService) {
		return args -> {
			
			if (userService.findUserById(2) != null) {
				userService.deleteUserById(2);
			}
			
			
			if (userService.findUserByName("admin") == null) {
				UserInfo user = new UserInfo();
				user.setUserName("admin");
				user.setPassword("admin123");
				user.setCreateDate(new Date());
				userService.addUser(user);
			}

			if (roleService.getAllRoles().size() == 0) {
				Role role = new Role();
				role.setName("ADMIN");
				roleService.addRole(role);
				userService.addRoleToUser("admin", role.getName());
			}
		};
	}
}
