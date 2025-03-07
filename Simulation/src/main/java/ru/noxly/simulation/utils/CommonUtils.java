package ru.noxly.simulation.utils;

import java.util.function.Function;

import static java.util.Optional.ofNullable;

public class CommonUtils {

	public static <R, S> R nullOrApply(S source, Function<S, R> mapper) {
		return ofNullable(source)
				.map(mapper)
				.orElse(null);
	}

	public static <T> T nullOrDefault(T source, T defaultValue) {
		return ofNullable(source)
				.orElse(defaultValue);
	}

}
