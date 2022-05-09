package springboot.service;

import java.util.List;

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
	private final RoleRepository roleRepo;

	public Role addRole(Role role) {
		log.info("Added new Role: {}", role.getName());
		return roleRepo.save(role);
	}

	public Role updateRole(Role role) {
		return roleRepo.save(role);
	}

	public Role findRoleById(Long id) {
		try {
			return roleRepo.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Role by id " + id + " was not found!"));
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	public Role findRoleByName(String name) {
		try {
			return roleRepo.findByName(name)
					.orElseThrow(() -> new EntityNotFoundException("Role by name " + name + " was not found!"));
		} catch (EntityNotFoundException e) {
			return null;
		}

	}

	public List<Role> getRoles() {
		return roleRepo.findAll();
	}
}
