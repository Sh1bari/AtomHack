package ru.noxly.websocket.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.noxly.websocket.models.ReservoirDto;
import ru.noxly.websocket.models.socket.ReservoirUpdateSocketDto;
import ru.noxly.websocket.services.SocketMessageSender;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservoirSubscriber {

	private final SocketMessageSender sender;

	private final ObjectMapper objectMapper;

	public void receiveMessage(String message, String channel) {
		try {
			val reservoirDto = objectMapper.readValue(message, ReservoirUpdateSocketDto.class);
			String spaceId = reservoirDto.getReservoir().getSpaceId().toString();
			sender.sendReservoirInfo(spaceId, reservoirDto);
			log.info("🔔 Получено обновление для spaceId={} из канала {}: {}", spaceId, channel, reservoirDto);


		} catch (IOException e) {
			log.error("Ошибка десериализации сообщения: {}", e.getMessage(), e);
		}
	}
}
