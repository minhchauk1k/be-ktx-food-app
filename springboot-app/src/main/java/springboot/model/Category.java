package springboot.model;

import java.io.Serializable;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
@SuppressWarnings("serial")
public class Category implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	private Long id;
	@Column(nullable = false, updatable = false)
	private String type;
	@Column(nullable = false)
	private String categoryKey;
	@Column(nullable = false)
	private String categoryValue;
	
	public Category(String categoryKey, String categoryValue, String type) {
		super();
		this.categoryKey = categoryKey;
		this.categoryValue = categoryValue;
		this.type = type;
	}
	
	
}
