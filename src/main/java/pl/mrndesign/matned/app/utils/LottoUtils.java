package pl.mrndesign.matned.app.utils;

import java.util.*;
import java.util.stream.Collectors;

public final class LottoUtils {

	private LottoUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Returns the most frequent numbers.
	 *
	 * @param draws list of drawNumbers
	 * @param numberOfNumbers
	 * @return
	 */
	public static List<Integer> calculateMostProbableSet(List<Integer[]> draws, int numberOfNumbers) {
		validateDraws(draws);

		Map<Integer, Long> frequency = draws.stream()
				.filter(Objects::nonNull)
				.flatMap(Arrays::stream)
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(
						number -> number,
						Collectors.counting()
				));

		return frequency.entrySet()
				.stream()
				.sorted(Map.Entry.<Integer, Long>comparingByValue()
						.reversed()
						.thenComparing(Map.Entry::getKey))
				.limit(numberOfNumbers)
				.map(Map.Entry::getKey)
				.sorted()
				.collect(Collectors.toList());
	}

	/**
	 * Returns the 6 numbers with the highest weighted score.
	 * Newer draws have a greater impact on the result.
	 *
	 * @param draws List of draws in chronological order (oldest -> newest)
	 * @param numberOfNumbers
	 * @return
	 */
	public static List<Integer> calculateMostProbableSetWeighted(List<Integer[]> draws, int numberOfNumbers) {
		validateDraws(draws);

		Map<Integer, Double> score = new HashMap<>();

		int totalDraws = draws.size();

		for (int i = 0; i < totalDraws; i++) {
			Integer[] draw = draws.get(i);

			if (draw == null) {
				continue;
			}

			double weight = (double) (i + 1) / totalDraws;

			for (Integer number : draw) {
				if (number != null) {
					score.merge(number, weight, Double::sum);
				}
			}
		}

		return score.entrySet()
				.stream()
				.sorted(Map.Entry.<Integer, Double>comparingByValue()
						.reversed()
						.thenComparing(Map.Entry::getKey))
				.limit(numberOfNumbers)
				.map(Map.Entry::getKey)
				.sorted()
				.collect(Collectors.toList());
	}

	private static void validateDraws(List<Integer[]> draws) {
		if (draws == null || draws.isEmpty()) {
			throw new IllegalArgumentException("Draws cannot be null or empty");
		}
	}
}
