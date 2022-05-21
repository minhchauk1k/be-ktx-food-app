package springboot;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import springboot.common.ConstDefined;
import springboot.model.Category;
import springboot.model.Role;
import springboot.model.SystemParameter;
import springboot.model.User;
import springboot.service.CategoryService;
import springboot.service.RoleService;
import springboot.service.SystemParameterService;
import springboot.service.UserService;

@SpringBootApplication
public class SpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Đoạn code tao data mặc định cho DB
	 */
	@Bean
	CommandLineRunner createDefaultDB(RoleService roleService, UserService userService,
			SystemParameterService paramService, CategoryService categoryService) {
		return args -> {
			// tạo Role table
			if (roleService.getRoles().size() == 0) {
				roleService.addRole(new Role(ConstDefined.ROLE_ADMIN));
				roleService.addRole(new Role(ConstDefined.ROLE_MANAGER));
				roleService.addRole(new Role(ConstDefined.ROLE_OWNER));
				roleService.addRole(new Role(ConstDefined.ROLE_STAFF));
				roleService.addRole(new Role(ConstDefined.ROLE_USER));
			}

			// tạo Category table
			if (categoryService.getCategories().size() == 0) {
				categoryService.add(new Category(ConstDefined.ALL, "Tất cả", ConstDefined.FOOD));
				categoryService.add(new Category(ConstDefined.ALL, "Tất cả", ConstDefined.SERVICE));
				categoryService.add(new Category(ConstDefined.DISCOUNT, "Đang giảm giá", ConstDefined.FOOD));
				categoryService.add(new Category(ConstDefined.DISCOUNT, "Đang giảm giá", ConstDefined.SERVICE));
			}

			// tạo user mặc định ADMIN
			User user = userService.findByUsername("admin");
			if (user == null) {
				user = new User();
				user.setUsername("admin");
				user.setPassword("admin123");
				user.setCreateDate(new Date());
				user.setCreateUser("admin");
				userService.add(user);
				userService.addRoleToUser(user.getUsername(), ConstDefined.ROLE_ADMIN);
			}

			// tạo SystemParameter table
			if (paramService.getParameters().size() == 0) {
				paramService.addParameter(new SystemParameter(ConstDefined.SERECT_KEY, "minchu"));
				paramService.addParameter(new SystemParameter(ConstDefined.OPEN_TIME, "10"));
				paramService.addParameter(new SystemParameter(ConstDefined.CLOSE_TIME, "20"));
			}
		};
	}
}
