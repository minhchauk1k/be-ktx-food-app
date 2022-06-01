package springboot.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.enums.MConst;
import springboot.exception.EntityNotFoundException;
import springboot.model.Role;
import springboot.model.User;
import springboot.repository.AddressRepository;
import springboot.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
	@Autowired
	private final UserRepository userRepo;
	@Autowired
	private final RoleService roleService;
	@Autowired
	private final CommonService commonService;
	@Autowired
	private final AddressService addressService;
	@Autowired
	private final AddressRepository addressRepo;
	private final PasswordEncoder pwEncoder;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		User user = findByUserName(userName);
		if (user == null) {
			throw new UsernameNotFoundException("Username: " + userName + " is not exist in DB!");
		}
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
		});

		// cập nhật lần login cuối
		user.setLastLoginDate(new Date());
		userRepo.save(user);

		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
				authorities);
	}

	public User add(User user) {
		if (user.getUserCode() == null || user.getUserCode().isEmpty()) {
			String createCode = "USER_" + String.format("%05d", userRepo.count() + 1);
			user.setUserCode(createCode);
		}
		user.setPassword(pwEncoder.encode(user.getPassword()));
		user.setCreateDate(new Date());
		user.setCreateUser(commonService.getCurrentUser());

		// thêm địa chỉ
		if (user.getAddresses().size() == 1) {
			addressService.add(user.getAddresses().get(0));
		}

		// thêm role mặc định
		Role role = roleService.findByRoleName(MConst.ROLE_USER);
		user.getRoles().add(role);

		log.info("Added new User: {}", user.getUserName());
		return userRepo.save(user);
	}

	public boolean checkExistByUserName(String userName) {
		return userRepo.existsByUserName(userName);
	}

	public User updateUser(User user) {
		user.setUpdateDate(new Date());
		user.setUpdateUser(commonService.getCurrentUser());
		addressRepo.saveAll(user.getAddresses());
		log.info("Updated User: {} by {}", new Object[] { user.getUserName(), commonService.getCurrentUser() });
		return userRepo.save(user);
	}

	public List<User> getUsers() {
		try {
			return userRepo.findAll();
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public void softDeleteById(Long id) {
		User entity = findById(id);
		entity.setUpdateDate(new Date());
		entity.setUpdateUser(commonService.getCurrentUser());
		entity.setBlocked(true);
		userRepo.save(entity);
	}

	public void deleteById(Long id) {
		userRepo.deleteById(id);
	}

	public User findById(Long id) {
		return userRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("User with id: " + id + " was not found!"));
	}

	public User findByUserName(String name) {
		return userRepo.findByUserName(name)
				.orElseThrow(() -> new EntityNotFoundException("User with username: " + name + " was not found!"));
	}

	public void addRoleToUser(String username, String rolename) {
		User user = findByUserName(username);
		Role role = roleService.findByRoleName(rolename);
		log.info("Added role: {} for User: {}", new Object[] { role.getRoleName(), user.getUserName() });
		user.getRoles().add(role);
	}

}
