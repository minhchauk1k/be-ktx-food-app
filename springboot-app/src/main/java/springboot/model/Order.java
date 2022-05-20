package springboot.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "order")
@SuppressWarnings("serial")
public class Order implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	private Long id;
	@Column(nullable = false, updatable = false)
	private String orderCode;
	private String userCode;
	@Column(nullable = false, updatable = false)
	private String payType;
	private String note;
	@Column(nullable = false)
	@OneToMany(fetch = FetchType.EAGER)
	private List<OrderDetails> details = new ArrayList<>();
	@Column(nullable = false, updatable = false)
	private String phoneNumber;
	@Column(nullable = false, updatable = false)
	private String addresss;
	@Column(nullable = false, updatable = false)
	private String createUser;
	@Column(nullable = false, updatable = false)
	private Date createDate;

}
