package springboot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.exception.EntityNotFoundException;
import springboot.model.Address;
import springboot.repository.AddressRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {
	@Autowired
	private final AddressRepository addressRepo;

	public List<Address> getAddresses() {
		try {
			return addressRepo.findAll();
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<Address> getByType(String type) {
		try {
			return addressRepo.findByType(type);
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public Address add(Address address) {
		log.info("Added new Address: {} with Type: {}",
				new Object[] { address.getArea() + ", " + address.getZone(), address.getType() });
		return addressRepo.save(address);
	}

	public Address update(Address address) {
		log.info("Updated Address with Id: {}", new Object[] { address.getId() });
		return addressRepo.save(address);
	}

	public void deleteById(Long id) {
		Address entity = findById(id);
		if (entity != null) {
			log.info("Deleted Address with Id: {}", new Object[] { entity.getId() });
			addressRepo.deleteById(id);
		} else {
			throw new EntityNotFoundException("Address with id: " + id + " was not found!");
		}
	}

	public Address findById(Long id) {
		try {
			return addressRepo.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Address with id: " + id + "was not found!"));
		} catch (Exception e) {
			return null;
		}
	}

}
