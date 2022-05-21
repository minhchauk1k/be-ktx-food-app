package springboot.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@SuppressWarnings("serial")
@Table(name = "order_details")
public class OrderDetails implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;

	@JsonIgnore // cái này để tránh gây null cho orderId
	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	private Order order;

	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	private Product product;

	@Column(nullable = false, updatable = false)
	private int qty;
}
