package springboot.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.common.ConstDefined;
import springboot.exception.EntityNotFoundException;
import springboot.model.Role;
import springboot.model.User;
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
	private final PasswordEncoder pwEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Username: " + username + " is not exist in DB!");
		}
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
		});
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				authorities);
	}

	private User _add(User user) {
		if (user.getUserCode() == null || user.getUserCode().isEmpty()) {
			String createCode = "USER_" + String.format("%05d", userRepo.count() + 1);
			user.setUserCode(createCode);
		}
		user.setPassword(pwEncoder.encode(user.getPassword()));
		user.setCreateDate(new Date());
		user.setCreateUser(getCurrentUser());
		log.info("Added new User: {}", user.getUsername());
		return userRepo.save(user);
	}

	public User add(User user) {
		user = _add(user);
		addRoleToUser(user.getUsername(), ConstDefined.ROLE_USER);
		return user;
	}

	public User updateUser(User user) {
		user.setUpdateDate(new Date());
		user.setUpdateUser(getCurrentUser());
		log.info("Updated user: {} by {}", new Object[] { user.getUsername(), getCurrentUser() });
		return userRepo.save(user);
	}

	public List<User> getUsers() {
		try {
			return userRepo.findAll();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public void softDeleteById(Long id) {
		User entity = findById(id);
		if (entity != null) {
			entity.setUpdateDate(new Date());
			entity.setUpdateUser("admin");
			entity.setDeleted(true);
			userRepo.save(entity);
		} else {
			throw new EntityNotFoundException("User with id: " + id + " was not found!");
		}
	}

	public void deleteById(Long id) {
		User user = findById(id);
		if (user != null) {
			userRepo.deleteById(id);
		} else {
			throw new EntityNotFoundException("User with id: " + id + " was not found!");
		}
	}

	public User findById(Long id) {
		try {
			return userRepo.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("User with id: " + id + " was not found!"));
		} catch (EntityNotFoundException e) {
			return null;
		}

	}

	public User findByUsername(String name) {
		try {
			return userRepo.findByUsername(name)
					.orElseThrow(() -> new EntityNotFoundException("User with username: " + name + " was not found!"));
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	public void addRoleToUser(String username, String rolename) {
		User user = findByUsername(username);
		Role role = roleService.findRoleByName(rolename);
		if (user != null && role != null) {
			log.info("Add role: {} cho user: {}", new Object[] { role.getRoleName(), user.getUsername() });
			user.getRoles().add(role);
		} else {
			throw new EntityNotFoundException("Can't add role: " + rolename + " to user: " + username);
		}
	}

	private String getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (auth != null && auth.getName() != null) ? auth.getName() : "";
	}

}
