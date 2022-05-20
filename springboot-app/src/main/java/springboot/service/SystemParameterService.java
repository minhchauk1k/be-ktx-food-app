package springboot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import springboot.exception.EntityNotFoundException;
import springboot.model.SystemParameter;
import springboot.repository.SystemParameterRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class SystemParameterService {
	@Autowired
	private final SystemParameterRepository paramRepo;

	public List<SystemParameter> getParameters() {
		try {
			return paramRepo.findAll();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public SystemParameter addParameter(SystemParameter param) {
		return paramRepo.save(param);
	}

	public SystemParameter getByKey(String key) {
		try {
			return paramRepo.findByParameterKey(key)
					.orElseThrow(() -> new EntityNotFoundException("Parameter with key: " + key + "was not found!"));
		} catch (Exception e) {
			return null;
		}
	}
}
