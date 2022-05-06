package springboot;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import springboot.common.ConstDefined;
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

	/**
	 * Đoạn code tao data mặc định cho DB
	 */
	@Bean
	CommandLineRunner createDefaultDB(RoleService roleService, UserInfoService userService) {
		return args -> {
			// tạo Role table
			if (roleService.getAllRoles().size() == 0) {
				Role newRole = new Role();
				newRole.setName(ConstDefined.ROLE_ADMIN);
				roleService.addRole(newRole);
				newRole = new Role();
				newRole.setName(ConstDefined.ROLE_MANAGER);
				roleService.addRole(newRole);
				newRole = new Role();
				newRole.setName(ConstDefined.ROLE_OWNER);
				roleService.addRole(newRole);
				newRole = new Role();
				newRole.setName(ConstDefined.ROLE_STAFF);
				roleService.addRole(newRole);
				newRole = new Role();
				newRole.setName(ConstDefined.ROLE_USER);
				roleService.addRole(newRole);
			}
			
			// tạo user mặc định ADMIN
			UserInfo user = userService.findUserByName("admin");
			if (user == null) {
				user = new UserInfo();
				user.setUserName("admin");
				user.setPassword("admin123");
				user.setCreateDate(new Date());
				userService.addUser(user);
			}
		};
	}
}
