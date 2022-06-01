package springboot;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import springboot.enums.MConst;
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

	@Bean
	CommandLineRunner createDefaultDB(RoleService roleService, UserService userService,
			SystemParameterService paramService, CategoryService categoryService) {
		return args -> {
			// tạo Role table
			if (roleService.getRoles().size() == 0) {
				roleService.add(new Role(MConst.ROLE_ADMIN));
				roleService.add(new Role(MConst.ROLE_OWNER));
				roleService.add(new Role(MConst.ROLE_MANAGER));
				roleService.add(new Role(MConst.ROLE_STAFF));
				roleService.add(new Role(MConst.ROLE_USER));
			}

			// tạo Category table
			if (categoryService.getCategories().size() == 0) {
				categoryService.add(new Category(MConst.ALL, "Tất cả", MConst.FOOD));
				categoryService.add(new Category(MConst.ALL, "Tất cả", MConst.SERVICE));
				categoryService.add(new Category(MConst.DISCOUNT, "Đang giảm giá", MConst.FOOD));
				categoryService.add(new Category(MConst.DISCOUNT, "Đang giảm giá", MConst.SERVICE));
			}

			// tạo user mặc định ADMIN
			if (userService.getUsers().size() == 0) {
				User user = new User();
				user.setUserName("admin");
				user.setPassword("admin123");
				user.setCreateDate(new Date());
				user.setCreateUser("admin");
				userService.add(user);
				userService.addRoleToUser(user.getUserName(), MConst.ROLE_ADMIN);
			}

			// tạo SystemParameter table
			if (paramService.getParameters().size() == 0) {
				paramService.addParameter(new SystemParameter(MConst.SERECT_KEY, "minchu"));
				paramService.addParameter(new SystemParameter(MConst.OPEN_TIME, "10"));
				paramService.addParameter(new SystemParameter(MConst.CLOSE_TIME, "20"));
				paramService.addParameter(new SystemParameter(MConst.LOT_CONTROL, MConst.YES));
			}
		};
	}
}
