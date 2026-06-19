package pl.mrndesign.matned.app.model.config;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.mrndesign.matned.app.model.auth.User;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
@Entity
@Table(name = "PROPERTIES")
public class Properties {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE", nullable = false)
	private PropertyType type;

	@Column(name = "NAME", length = 100, nullable = false)
	private String name;

    @Column(name = "VALUE", length = 200, nullable = false)
	private String value;

	@Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

}
