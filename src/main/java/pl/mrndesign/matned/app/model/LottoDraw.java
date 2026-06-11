package pl.mrndesign.matned.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "LOTTO_DRAW")
public class LottoDraw {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DATE")
    private LocalDate date;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "NUMBERS_ID")
    private LottoNumbers numbers;

    @Column(name = "DRAW_TYPE")
    @Enumerated(EnumType.STRING)
    private DrawType drawType;

	public LottoDraw(LocalDate date, LottoNumbers numbers, DrawType drawType) {
		this.date = date;
		this.numbers = numbers;
		this.drawType = drawType;
	}

}
