package springboot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "system_parameter")
public class SystemParameter {
	@Column(nullable = false, columnDefinition = "default minchu")
	private String serectKey;
	@Column(nullable = false, columnDefinition = "default 9")
	private int openTime;
	@Column(nullable = false, columnDefinition = "default 20")
	private int closeTime;
}
