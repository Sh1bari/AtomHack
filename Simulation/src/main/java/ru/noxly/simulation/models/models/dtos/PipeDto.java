package ru.noxly.simulation.models.models.dtos;

import lombok.*;
@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
public class PipeDto {

	private final Long id;

	private final Long sourceId;

	private final Long targetId;

	private final Double diameter;
}
