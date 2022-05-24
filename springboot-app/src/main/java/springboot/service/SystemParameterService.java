package springboot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.enums.MConst;
import springboot.exception.EntityNotFoundException;
import springboot.model.SystemParameter;
import springboot.repository.SystemParameterRepository;

@Slf4j
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
			log.info("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public SystemParameter addParameter(SystemParameter param) {
		return paramRepo.save(param);
	}

	public SystemParameter getByKey(String key) {
		return paramRepo.findByParameterKey(key)
				.orElseThrow(() -> new EntityNotFoundException("Parameter with key: " + key + " was not found!"));
	}
	
	public boolean getIsLotControl() {
		String value = getByKey(MConst.LOT_CONTROL).getParameterValue();
		return value.equals(MConst.YES) ? true: false;
	}
}
