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
@Table(name = "user_info")
@SuppressWarnings("serial")
public class UserInfo implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	private Long id;
	private String displayName;
	private String userId;
	@Column(nullable = false, updatable = false)
	private String username;
	@Column(nullable = false, updatable = false)
	private String password;
	private String phoneNumber;
	private String email;
	private String addresss;
	private String urlAvatar;
	private boolean isDeleted;
	private boolean isActive;
	private Date lastLoginDate;
	@ManyToMany(fetch = FetchType.EAGER)
	private List<Role> roles = new ArrayList<>();
	@Column(nullable = false, updatable = false)
	private String createUser;
	private String updateUser;
	@Column(nullable = false, updatable = false)
	private Date createDate;
	private Date updateDate;
}
