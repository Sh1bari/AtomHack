package ru.noxly.simulation.models.models.socket;

import lombok.*;
import ru.noxly.simulation.models.models.dtos.ReservoirDto;

@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
public class ReservoirUpdateSocketDto {

	private final Long spaceId;

	private final SocketEntityEnum command;

	private final ReservoirDto reservoir;
}
