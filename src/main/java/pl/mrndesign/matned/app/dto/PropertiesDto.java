package pl.mrndesign.matned.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import pl.mrndesign.matned.app.model.config.PropertyType;

@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class PropertiesDto {

	private Long id;
	private Long userId;
	private PropertyType type;
	private String label;
	private String name;
	private String value;
	private Boolean enabled;

}
