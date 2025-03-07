package ru.noxly.simulation.utils;

import lombok.*;

public class ReservoirIdUtil {

	/**
	 * Возвращает остаток от деления переданного идентификатора резервуара на 10.
	 *
	 * @param id идентификатор резервуара, не должен быть null
	 * @return остаток от деления {@code id} на 10 или {@code null}, если {@code id} равно null
	 */
	public static Long resolveReservoirId(final Long id) {
		return (id != null) ? id % 10 : null;
	}

}
