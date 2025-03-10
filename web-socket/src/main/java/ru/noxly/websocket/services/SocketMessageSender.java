package ru.noxly.websocket.services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.noxly.websocket.models.ReservoirDto;
import ru.noxly.websocket.models.socket.PipeUpdateSocketDto;
import ru.noxly.websocket.models.socket.ReservoirUpdateSocketDto;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class SocketMessageSender {

	private final SimpMessagingTemplate messagingTemplate;

	public void sendReservoirInfo(final String spaceId, final ReservoirUpdateSocketDto reservoir) {
		messagingTemplate.convertAndSend(format("/topic/space/%s", spaceId), reservoir);
	}

	public void sendPipeInfo(final String spaceId, final PipeUpdateSocketDto pipe) {
		messagingTemplate.convertAndSend(format("/topic/space/%s", spaceId), pipe);
	}
}
