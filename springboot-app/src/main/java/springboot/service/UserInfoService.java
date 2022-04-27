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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import springboot.exception.EntityNotFoundException;
import springboot.model.Role;
import springboot.model.UserInfo;
import springboot.repository.RoleRepository;
import springboot.repository.UserInfoRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserInfoService implements UserDetailsService {
	private final UserInfoRepository userRepo;
	private final RoleRepository roleRepo;

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

	public UserInfo addUser(UserInfo userInfo) {
		userInfo.setCreateDate(new Date());
		return userRepo.save(userInfo);
	}

	public UserInfo updateUser(UserInfo userInfo) {
		return userRepo.save(userInfo);
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
		Role role = roleRepo.findByName(roleName);
		user.getRoles().add(role);
	}

}
