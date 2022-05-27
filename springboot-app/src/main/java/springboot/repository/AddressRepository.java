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

	List<Address> findByArea(String Area);

	List<Address> findByZone(String Area);

	List<Address> findByRoom(String Area);
}
