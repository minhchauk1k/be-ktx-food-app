package springboot.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.exception.EntityNotFoundException;
import springboot.model.Role;
import springboot.model.UserInfo;
import springboot.repository.UserInfoRepository;

@Service
@RequiredArgsConstructor
@Transactional @Slf4j
public class UserInfoService implements UserDetailsService {
	private final UserInfoRepository userRepo;
	private final RoleService roleService;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserInfo user = findUserByName(username);
		if (user == null) {
			throw new UsernameNotFoundException("User is not exist in DB!");
		}
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		user.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		});
		return new User(user.getUserName(), user.getPassword(), authorities);
	}

	public UserInfo addUser(UserInfo user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setCreateDate(new Date());
		log.info("Created user: {}", user.getUserName());
		return userRepo.save(user);
	}

	public UserInfo updateUser(UserInfo user) {
		return userRepo.save(user);
	}

	public List<UserInfo> getAllUsers() {
		return userRepo.findAll();
	}

	public void deleteUserById(int id) {
//		UserInfo entity = this.findUserById(id);
//		entity.setIsDeleted(true);
//		userRepo.save(entity);
		userRepo.deleteById(id);
	}

	public UserInfo findUserById(int i) {
		try {
			return userRepo.findById(i)
					.orElseThrow(() -> new EntityNotFoundException("User by id " + i + " was not found!"));
		} catch (EntityNotFoundException e) {
			return null;
		}

	}

	public UserInfo findUserByName(String name) {
		try {
			return userRepo.findByUserName(name)
					.orElseThrow(() -> new EntityNotFoundException("User by name " + name + " was not found!"));
		} catch (EntityNotFoundException e) {
			return null;
		}

	}

	public void addRoleToUser(String userName, String roleName) {
		UserInfo user = findUserByName(userName);
		Role role = roleService.findRoleByName(roleName);
		user.getRoles().add(role);
	}

}
