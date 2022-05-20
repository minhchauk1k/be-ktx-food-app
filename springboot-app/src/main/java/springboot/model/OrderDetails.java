package springboot.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_details")
public class OrderDetails {
	@EmbeddedId
	private OrderDetailsId detailsId;

	@Column(nullable = false, updatable = false)
	private String productCode;
	@Column(nullable = false, updatable = false)
	private int quantity;

}
