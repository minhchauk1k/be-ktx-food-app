package springboot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
			log.info("Error: {}", e.getMessage());
			return new ArrayList<>();
		}
	}
	
	public Address add(Address address) {
		log.info("Added new Address: {}", address.getArea() + ", " + address.getZone());
		return addressRepo.save(address);
	}

}
