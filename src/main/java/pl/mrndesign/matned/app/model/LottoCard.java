package pl.mrndesign.matned.app.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "LOTTO_CARD")
public class LottoCard {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "OWNER_SUBJECT", nullable = false)
    private String ownerSubject;

    @Column(name = "FIRST_DRAW_DATE")
    private LocalDate firstDrawDate;

    @Column(name = "NUMBER_OF_DRAWINGS")
    private int numberOfDrawings;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "LOTTO_CARD_NUMBERS",
            joinColumns = @JoinColumn(name = "LOTTO_CARD_ID"),
            inverseJoinColumns = @JoinColumn(name = "LOTTO_NUMBERS_ID")
    )
    private List<LottoNumbers> numbers = new ArrayList<>();

    @Column(name = "DRAW_TYPE")
    @Enumerated(EnumType.STRING)
    private DrawType drawType;

}
