package springboot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import springboot.exception.EntityNotFoundException;
import springboot.model.Role;
import springboot.repository.RoleRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {
	private final RoleRepository roleRepo;

	public Role addRole(Role role) {
		return roleRepo.save(role);
	}

	public Role updateRole(Role role) {
		return roleRepo.save(role);
	}

	public Role findRoleById(Long id) {
		return roleRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Role by id " + id + " was not found!"));
	}

	public List<Role> getAllRoles() {
		return roleRepo.findAll();
	}
}
