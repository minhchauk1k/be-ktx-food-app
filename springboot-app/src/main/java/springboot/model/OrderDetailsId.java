package springboot.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Embeddable
@SuppressWarnings("serial")
public class OrderDetailsId implements Serializable {
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false, name = "id")
	private Long id;

	@Column(nullable = false, updatable = false, name = "order_code")
	private String orderCode;
}