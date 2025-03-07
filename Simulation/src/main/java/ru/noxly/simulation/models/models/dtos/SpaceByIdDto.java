package ru.noxly.simulation.models.models.dtos;

import lombok.*;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
public class SpaceByIdDto {

	private final SpaceDto space;

	private final List<ReservoirDto> reservoirs;

	private final List<PipeDto> pipes;
}
