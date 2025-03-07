package ru.noxly.simulation.models.models.dtos;

import lombok.*;
@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
public class SpaceDto {

	private final Long id;

	private final String name;

	private final String createDate;
}
