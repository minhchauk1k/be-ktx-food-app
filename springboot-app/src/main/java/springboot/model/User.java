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
import javax.persistence.ManyToMany;
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
@Table(name = "users")
@SuppressWarnings("serial")
public class User implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;
	private String displayName;
	@Column(nullable = false, updatable = false)
	private String userCode;
	@Column(nullable = false, updatable = false)
	private String userName;
	@Column(nullable = false, updatable = false)
	private String password;
	private String phoneNumber;
	private String email;
	private String urlAvatar;
	private boolean isBlocked;
	private Date lastLoginDate;

	@ManyToMany(fetch = FetchType.LAZY)
	private List<Role> roles = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY)
	private List<Address> addresses = new ArrayList<>();

	@Column(nullable = false, updatable = false)
	private String createUser;
	private String updateUser;
	@Column(nullable = false, updatable = false)
	private Date createDate;
	private Date updateDate;
}
