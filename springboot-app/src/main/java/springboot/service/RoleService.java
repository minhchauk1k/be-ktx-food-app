package springboot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.exception.EntityNotFoundException;
import springboot.model.Role;
import springboot.repository.RoleRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {
	@Autowired
	private final RoleRepository roleRepo;

	public Role add(Role role) {
		log.info("Added new Role: {}", role.getRoleName());
		return roleRepo.save(role);
	}

	public Role update(Role role) {
		return roleRepo.save(role);
	}

	public Role findById(Long id) {
		return roleRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Role by id " + id + " was not found!"));
	}

	public Role findByRoleName(String name) {
		return roleRepo.findByRoleName(name)
				.orElseThrow(() -> new EntityNotFoundException("Role by name " + name + " was not found!"));
	}

	public List<Role> getRoles() {
		try {
			return roleRepo.findAll();
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}
}
