package pl.mrndesign.matned.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "LOTTO_NUMBERS")
public class LottoNumbers {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @ElementCollection
    @CollectionTable(
            name = "LOTTO_NUMBERS_VALUES",
            joinColumns = @JoinColumn(name = "LOTTO_NUMBERS_ID")
    )
    @Column(name = "NUMBER_VALUE", nullable = false)
    private List<Integer> numbers = new ArrayList<>();

    public LottoNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }
}
