package springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.model.OrderDetails;
import springboot.model.OrderDetailsId;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, OrderDetailsId> {
//	public interface BookRepository extends JpaRepository<Book, BookId> {
//	    List<Book> findByIdName(String name);
//	    List<Book> findByIdAuthor(String author);
//	}
}
