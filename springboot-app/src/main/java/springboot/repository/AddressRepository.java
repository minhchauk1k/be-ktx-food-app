package springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import springboot.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
	Optional<Address> findById(Long id);

	void deleteById(Long id);

	List<Address> findByArea(String area);

	List<Address> findByZone(String zone);

	List<Address> findByRoom(String room);
	
	List<Address> findByType(String type);
}
