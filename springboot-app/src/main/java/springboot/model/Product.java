package springboot.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@SuppressWarnings("serial")
public class Product implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;
	@Column(nullable = false, updatable = false)
	private String productCode;
	@Column(nullable = false)
	private String productName;
	@Column(nullable = false)
	private BigDecimal price;
	private BigDecimal finalPrice;
	private BigDecimal discountNumber;
	private BigDecimal discountPercent;
	private int qty;
	@Column(nullable = false)
	private int planQty;
	private String urlAvatar;
	private String description;
	private String category;
	private String type;
	private String discountType;
	private boolean isDeleted;
	private boolean isDiscount;
	private boolean isInventory = true;
	@Column(nullable = false, updatable = false)
	private String createUser;
	private String updateUser;
	@Column(nullable = false, updatable = false)
	private Date createDate;
	private Date updateDate;
}
