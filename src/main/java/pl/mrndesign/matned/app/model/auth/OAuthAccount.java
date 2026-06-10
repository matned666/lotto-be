package pl.mrndesign.matned.app.model.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Table(
        name = "OAUTH_ACCOUNTS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"PROVIDER_TYPE", "PROVIDER_USER_ID"})
        }
)
public class OAuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROVIDER_TYPE", nullable = false)
    private AuthProviderType provider;

    @Column(name = "PROVIDER_USER_ID", nullable = false)
    private String providerUserId;
}
