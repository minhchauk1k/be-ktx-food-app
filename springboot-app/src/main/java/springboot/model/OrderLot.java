package springboot.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Table(name = "order_lots")
@SuppressWarnings("serial")
public class OrderLot implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;

	@Column(nullable = false, updatable = false)
	private String lotCode;
	
	@Column(nullable = false, updatable = false)
	private String createUser;
	private String updateUser;
	@Column(nullable = false, updatable = false)
	private Date createDate;
	private Date updateDate;
	
	private boolean isCompleted;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Order> details = new ArrayList<>();
}
