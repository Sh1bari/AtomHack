package ru.noxly.simulation.models.models.socket;

import lombok.*;
import ru.noxly.simulation.models.models.dtos.PipeDto;

@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
public class PipeUpdateSocketDto {

    private final Long spaceId;

    private final SocketEntityEnum command;

    private final PipeDto pipe;
}
