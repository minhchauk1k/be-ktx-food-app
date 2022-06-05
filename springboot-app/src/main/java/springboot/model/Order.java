package springboot.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "orders")
@SuppressWarnings("serial")
public class Order implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;
	@Column(nullable = false, updatable = false)
	private String orderCode;
	@Column(nullable = false)
	private String userDisplayName;
	@Column(nullable = false, updatable = false)
	private String payType;
	@Column(nullable = false, updatable = false)
	private String phoneNumber;
	@Column(nullable = false, updatable = false)
	private String address;
	@Column(nullable = false, updatable = false)
	private String createUser;
	@Column(nullable = false, updatable = false)
	private Date createDate;

	private Date updateDate;
	private BigDecimal totalAmount;
	private String note;
	private String cancelReasons;
	private String updateUser;
	private String orderStatus;
	private int totalQty;
	private boolean isPaid;
	private boolean isCancel;
	private boolean isCompleted;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderDetails> details = new ArrayList<>();
}
