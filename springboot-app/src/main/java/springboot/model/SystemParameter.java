package springboot.model;

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
@Table(name = "system_parameters")
public class SystemParameter {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private int id;
	@Column(nullable = false)
	private String parameterKey;
	@Column(nullable = false)
	private String parameterValue;
	private String description;

	public SystemParameter(String parameterKey, String parameterValue) {
		super();
		this.parameterKey = parameterKey;
		this.parameterValue = parameterValue;
	}
}
