package ru.noxly.websocket.models.socket;

import lombok.*;
import ru.noxly.websocket.models.PipeDto;

@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
public class PipeUpdateSocketDto {

    private final SocketEntityEnum command;

    private final PipeDto pipe;
}
