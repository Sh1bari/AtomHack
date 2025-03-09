package ru.noxly.websocket.models.socket;

import lombok.*;
import ru.noxly.websocket.models.ReservoirDto;

@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
public class ReservoirUpdateSocketDto {

	private final SocketEntityEnum command;

	private final ReservoirDto reservoir;
}
